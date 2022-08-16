import { Module } from '@nestjs/common';
import { VerificationService } from './verification.service';
import { VerificationController } from './verification.controller';
import { PrismaService } from 'src/database/services/prisma.service';
import { SendGridService } from 'src/common/services/sendgrid.service';

@Module({
  controllers: [VerificationController],
  providers: [VerificationService, PrismaService, SendGridService],
})
export class VerificationModule { }
