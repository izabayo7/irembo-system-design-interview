import {
  BadRequestException,
  HttpException,
  HttpStatus,
  Injectable,
} from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { SendGridService } from '../common/services/sendgrid.service';
import { PrismaService } from '../prisma/prisma.service';
import { CreatePasswordResetDto } from '../models/user/dto/create-password-reset.dto';
import { LoginDto } from '../models/user/dto/login.dto';
import { UpdatePasswordResetDto } from '../models/user/dto/update-password-reset.dto';
import { compare, hash } from '../utils/password';
import { v4 } from 'uuid';

@Injectable()
export class AuthService {
  constructor(
    private readonly prismaService: PrismaService,
    private readonly jwtService: JwtService,
    private readonly emailService: SendGridService,
  ) { }

  async login(loginDto: LoginDto) {
    const { twofactorAuthCode, email, password } = loginDto;
    const user = await this.prismaService.user.findUnique({
      where: { email },
    });

    if (!user)
      throw new HttpException('Invalid credentials', HttpStatus.UNAUTHORIZED);

    const isPasswordValid = await compare(password, user.password);

    if (!isPasswordValid) {
      throw new HttpException('Invalid credentials', HttpStatus.UNAUTHORIZED);
    }

    if (user.tfaEnabled) {
      if (!user.tfaSecret || !twofactorAuthCode) {
        const secret = Math.floor(100000 + Math.random() * 900000);

        const mail = {
          to: await this.prismaService.user.findMany({
            select: {
              email: true,
            },
          }),
          from: `${process.env.fromEmail}`,
          subject: 'User Account Management System - 2FA',
          templateId: `${process.env.templateId}`,
          dynamicTemplateData: {
            header: 'Two Factor Authentication',
            text: 'Please use the code below to continue the process.',
            c2a_button: secret.toString(),
          },
        };
        await this.emailService.send(mail);

        return await this.prismaService.user.update({
          where: { id: user.id },
          data: {
            tfaSecret: secret.toString(),
          },
          select: {
            email: true,
          },
        });
      }
      if (!twofactorAuthCode) {
        throw new BadRequestException('twofactorAuthCode Code is required');
      }

      if (twofactorAuthCode !== user.tfaSecret) {
        throw new BadRequestException('invalid twofactorAuthCode');
      }

      await this.prismaService.user.update({
        where: { id: user.id },
        data: {
          tfaSecret: null,
        },
      });
    }

    const { id } = user;
    const accessToken = this.generateAccessToken(email, id, id);
    const refreshToken = await this.generateRefreshToken(email, id, id);

    return {
      user,
      accessToken,
      refreshToken,
    };
  }

  async createPasswordReset(createPasswordResetDto: CreatePasswordResetDto) {
    const userExists = await this.prismaService.user.findUnique({
      where: {
        email: createPasswordResetDto.email,
      },
    });
    if (!userExists)
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);

    const passwordReset = await this.prismaService.passwordReset.findUnique({
      where: { userId: userExists.id },
    });

    const validUntil = new Date(Date.now() + 1000 * 60 * 20);

    const token = v4();
    const mail = {
      to: createPasswordResetDto.email,
      from: `${process.env.fromEmail}`,
      subject: 'User Account Management System - Password Reset',
      templateId: `${process.env.templateId}`,
      dynamicTemplateData: {
        header: 'Password Reset',
        text:
          'You requested a password reset which will expire ' +
          validUntil.toLocaleString() +
          ' (in 20 minutes). Please use the button below to continue the process.',
        c2a_link: process.env.FRONTEND_URL + '/reset-password/' + token,
        c2a_button: 'Reset Password',
      },
    };
    await this.emailService.send(mail);

    if (passwordReset) {
      return await this.prismaService.passwordReset.update({
        where: { id: passwordReset.id },
        data: {
          token,
          validUntil,
        },
        select: {
          validUntil: true,
        },
      });
    } else {
      return await this.prismaService.passwordReset.create({
        data: {
          userId: userExists.id,
          // create a new token for each password reset request (it will be valid for 20 minutes)
          validUntil,
          token,
        },
        select: {
          validUntil: true,
        },
      });
    }
  }

  async updatePasswordReset(updatePasswordResetDto: UpdatePasswordResetDto) {
    const passwordReset = await this.prismaService.passwordReset.findFirst({
      where: { token: updatePasswordResetDto.token },
    });

    if (!passwordReset)
      throw new HttpException('Password reset not found', HttpStatus.NOT_FOUND);

    if (passwordReset.validUntil < new Date())
      throw new HttpException(
        'Password reset token expired',
        HttpStatus.NOT_FOUND,
      );

    const hashedPassword = await hash(updatePasswordResetDto.password);

    await this.prismaService.user.update({
      where: {
        id: passwordReset.userId,
      },
      data: {
        password: hashedPassword,
      },
    });

    return await this.prismaService.passwordReset.update({
      where: { id: passwordReset.id },
      data: {
        isActive: false,
      },
    });
  }

  async findPasswordReset(token: string) {
    const passwordReset = await this.prismaService.passwordReset.findFirst({
      where: { token },
    });

    if (!passwordReset)
      throw new HttpException('Password reset not found', HttpStatus.NOT_FOUND);

    return { ...passwordReset, expired: passwordReset.validUntil < new Date() };
  }

  private generateAccessToken(email: string, id: string, sub: string) {
    const payload = {
      email,
      id,
      sub,
    };

    // Will be expired in 1 day
    return this.jwtService.sign(payload, { expiresIn: '1d' });
  }

  private async generateRefreshToken(email: string, id: string, sub: string) {
    const payload = {
      email,
      id,
      sub,
    };
    // Will be expired in 4 hours
    const refreshToken = this.jwtService.sign(payload, {
      secret: process.env.JWT_REFRESH_SECRET,
      expiresIn: '4h',
    });
    const tokenExists = await this.prismaService.refreshToken.findUnique({
      where: { userId: id },
    });

    if (tokenExists)
      return await this.prismaService.refreshToken.update({
        where: { id: tokenExists.id },
        data: {
          userId: id,
          token: refreshToken,
        },
      });

    return await this.prismaService.refreshToken.create({
      data: {
        userId: id,
        token: refreshToken,
      },
    });
  }
}
