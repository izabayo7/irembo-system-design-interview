import { Module } from '@nestjs/common';
import { UserService } from './user.service';
import { UserController } from './user.controller';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { PrismaService } from '../../database/services/prisma.service';
import { SendGridService } from '../../common/services/sendgrid.service';

@Module({
  controllers: [UserController],
  providers: [
    UserService,
    PrismaService,
    SendGridService,
    JwtService,
    ConfigService,
  ],
})
export class UserModule {}
