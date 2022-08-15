import { Injectable } from '@nestjs/common';
import { ResponseStatus } from 'src/common/enums/responseStatus.enum';
import { SendGridService } from 'src/common/services/sendgrid.service';
import { PrismaService } from 'src/database/services/prisma.service';
import { CreatePasswordResetDto } from './dto/create-password-reset.dto';
import { CreateUserDto } from './dto/create-user.dto';
import { LoginDto } from './dto/login.dto';
import { UpdatePasswordResetDto } from './dto/update-password-reset.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { v4 } from 'uuid';
import { hash } from 'src/utils/password';

@Injectable()
export class UserService {
  constructor(
    private readonly prismaService: PrismaService,
    private readonly sendgridService: SendGridService,
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
      return {
        status: ResponseStatus.FAILURE,
        message: 'User with the same email already exist',
      };

    const hashedPassword = await hash(createUserDto.password);

    const res = await this.prismaService.user.create({
      data: {
        ...createUserDto,
        password: hashedPassword,
      },
    });

    // initialise an account verification record (so that the user have unverified status immediately after creation)
    await this.prismaService.accountVerification.create({
      data: {
        userId: res.id,
      },
    });

    return {
      status: ResponseStatus.SUCCESS,
      message: 'User created successfully',
    };
  }

  login(loginDto: LoginDto) {
    return 'This action adds a new user';
  }

  createPasswordReset(createPasswordResetDto: CreatePasswordResetDto) {
    return 'This action adds a new user';
  }

  updatePasswordReset(updatePasswordResetDto: UpdatePasswordResetDto) {
    return 'This action adds a new user';
  }

  findAll() {
    return `This action returns all user`;
  }

  findOne(id: number) {
    return `This action returns a #${id} user`;
  }

  update(id: number, updateUserDto: UpdateUserDto) {
    return `This action updates a #${id} user`;
  }

  remove(id: number) {
    return `This action removes a #${id} user`;
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
