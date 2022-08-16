import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { JwtService } from '@nestjs/jwt';
import { Roles } from '@prisma/client';
import { PrismaService } from '../../database/services/prisma.service';
import { SendGridService } from '../../common/services/sendgrid.service';
import { hash } from '../../utils/password';

@Injectable()
export class UserService {
  constructor(
    private readonly prismaService: PrismaService,
    private readonly sendgridService: SendGridService,
    private readonly jwtService: JwtService,
  ) { }

  public async checkIfUserExists(email: string) {
    const user = await this.prismaService.user.findUnique({
      where: {
        email,
      },
    });

    if (user) {
      return true;
    }

    return false;
  }

  async create(createUserDto: CreateUserDto) {
    const exists = await this.checkIfUserExists(createUserDto.email);

    if (exists)
      throw new HttpException(
        'User with the same email already exist',
        HttpStatus.NOT_FOUND,
      );

    if (createUserDto.role === Roles.ADMIN) {
      const adminFound = await this.prismaService.user.findFirst({
        where: {
          role: Roles.ADMIN,
        },
      });
      if (adminFound)
        throw new HttpException('Admin already exists', HttpStatus.BAD_REQUEST);
    }

    const hashedPassword = await hash(createUserDto.password);

    const res = await this.prismaService.user.create({
      data: {
        ...createUserDto,
        dateOfBirth: new Date(createUserDto.dateOfBirth).toISOString(),
        password: hashedPassword,
      },
    });

    // initialise an account verification record (so that the user have unverified status immediately after creation)
    await this.prismaService.accountVerification.create({
      data: {
        userId: res.id,
      },
    });

    return res;
  }

  async findAll() {
    return await this.prismaService.user.findMany({
      where: {
        role: Roles.USER,
      },
      include: {
        accountVerification: true,
      },
    });
  }

  async findOne(id: string) {
    const user = await this.prismaService.user.findUnique({
      where: {
        id,
      },
      include: {
        accountVerification: true,
      },
    });
    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND);

    return user;
  }

  async update(id: string, updateUserDto: UpdateUserDto) {
    const user = await this.prismaService.user.findUnique({
      where: {
        id,
      },
      include: {
        accountVerification: true,
      },
    });
    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    console.log(updateUserDto);
    return await this.prismaService.user.update({
      where: {
        id,
      },
      data: {
        ...updateUserDto,
        dateOfBirth: new Date(updateUserDto.dateOfBirth).toISOString(),
      },
    });
  }

  async remove(id: string) {
    const user = await this.prismaService.user.findUnique({
      where: {
        id,
      },
      include: {
        accountVerification: true,
        PasswordReset: true,
        RefreshToken: true,
      },
    });
    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND);

    if (user.accountVerification)
      await this.prismaService.accountVerification.delete({
        where: {
          userId: id,
        },
      });

    if (user.RefreshToken)
      await this.prismaService.refreshToken.delete({
        where: {
          userId: id,
        },
      });

    if (user.PasswordReset)
      await this.prismaService.passwordReset.delete({
        where: {
          userId: id,
        },
      });

    return await this.prismaService.user.delete({
      where: {
        id,
      },
    });
  }

  /**
   * @private send email helper
   * @param email password
   *
   */
  async sendConfirmPasswordEmail({ email }: { email: string }) {
    const mail = {
      to: email,
      from: 'cedricizabayo7@gmail.com',
      templateId: 'd-157bf0d2f4834e3b921ded871f8b24f9',
      dynamicTemplateData: {
        email,
      },
    };
    return await this.sendgridService.send(mail);
  }
}
