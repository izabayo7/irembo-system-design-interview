import { Module } from '@nestjs/common';
import { VerificationService } from './verification.service';
import { VerificationController } from './verification.controller';
import { ConfigService } from '@nestjs/config';
import { PrismaService } from '../../database/services/prisma.service';
import { SendGridService } from '../../common/services/sendgrid.service';

@Module({
  controllers: [VerificationController],
  providers: [
    VerificationService,
    PrismaService,
    SendGridService,
    ConfigService,
  ],
})
export class VerificationModule { }
