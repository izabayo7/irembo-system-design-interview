import { Module } from '@nestjs/common';
import { UserService } from './user.service';
import { UserController } from './user.controller';
import { PrismaService } from 'src/database/services/prisma.service';
import { SendGridService } from 'src/common/services/sendgrid.service';

@Module({
  controllers: [UserController],
  providers: [UserService, PrismaService, SendGridService],
})
export class UserModule { }
