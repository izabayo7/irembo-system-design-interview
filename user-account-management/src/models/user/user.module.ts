import { Module } from '@nestjs/common';
import { UserService } from './user.service';
import { UserController } from './user.controller';
import { PrismaService } from 'src/database/services/prisma.service';
import { SendGridService } from 'src/common/services/sendgrid.service';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';

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
