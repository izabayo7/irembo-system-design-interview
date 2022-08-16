import {
  BadRequestException,
  HttpException,
  HttpStatus,
  Injectable,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { ResponseStatus } from 'src/common/enums/responseStatus.enum';
import { SendGridService } from 'src/common/services/sendgrid.service';
import { PrismaService } from 'src/database/services/prisma.service';
import { CreatePasswordResetDto } from 'src/models/user/dto/create-password-reset.dto';
import { LoginDto } from 'src/models/user/dto/login.dto';
import { UpdatePasswordResetDto } from 'src/models/user/dto/update-password-reset.dto';
import { compare, hash } from 'src/utils/password';
import { v4 } from 'uuid';

@Injectable()
export class AuthService {
  constructor(
    private readonly prismaService: PrismaService,
    private readonly jwtService: JwtService,
    private readonly emailService: SendGridService,
  ) { }

  async login(loginDto: LoginDto) {
    const { email, password } = loginDto;
    const user = await this.prismaService.user.findUnique({
      where: { email },
    });

    if (!user)
      throw new HttpException('Invalid credentials', HttpStatus.UNAUTHORIZED);

    const isPasswordValid = await compare(password, user.password);

    if (!isPasswordValid) {
      throw new HttpException('Invalid credentials', HttpStatus.UNAUTHORIZED);
    }

    // if (user.tfaEnabled) {
    //   if (!loginDto.tfactorAuthCode) {
    //     throw new BadRequestException('TFactor Auth Code is required');
    //   }

    // const isTfACodeValid =
    //   this.twofactorAuthService.isTwoFactorAuthenticationCodeValid(
    //     loginDto.tfactorAuthCode,
    //     user.tfaSecret,
    //   );

    //   if (!isTfACodeValid) {
    //     throw new UnauthorizedException();
    //   }
    // }

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
    console.log(
      process.env.fromEmail,
      process.env.templateId,
      process.env.SEND_GRID_KEY,
    );
    const mail = {
      to: await this.prismaService.user.findMany({
        select: {
          email: true,
        },
      }),
      from: `${process.env.fromEmail}`,
      subject: 'User Account Management System - Password Reset',
      templateId: `${process.env.templateId}`,
      dynamicTemplateData: {
        header: 'Password Reset',
        text:
          'You requested a password reset which will expire' +
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

  // Public methods
  public async verifyRefreshToken(token: string) {
    try {
      const decoded = this.jwtService.verify(token, {
        secret: process.env.JWT_REFRESH_SECRET,
      });

      const user = await this.prismaService.user.findUnique({
        where: { email: decoded.email },
      });

      return user;
    } catch (error) {
      throw new HttpException('Invalid refresh token', 400);
    }
  }
}
