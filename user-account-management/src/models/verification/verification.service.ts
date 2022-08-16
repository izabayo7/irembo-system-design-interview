import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { VerificationStatuses } from '@prisma/client';
import { PrismaService } from 'src/database/services/prisma.service';
import { CreateVerificaitonDto } from './dto/create-verification.dto';

@Injectable()
export class VerificationService {
  constructor(private readonly prismaService: PrismaService) { }

  async verifyAccount(id: string) {
    const res = await this.prismaService.accountVerification.findUnique({
      where: {
        id,
      },
    });
    if (!res)
      throw new HttpException(
        'Account Verification not found',
        HttpStatus.NOT_FOUND,
      );

    if (!res.nidOrPassport)
      throw new HttpException(
        'Can not verify a user who have not uploaded a NID or Passport',
        HttpStatus.NOT_ACCEPTABLE,
      );

    return await this.prismaService.accountVerification.update({
      where: {
        id,
      },
      data: {
        verificationStatus: VerificationStatuses.VERIFIED,
      },
    });
  }

  async update(id: string, createVerificationDto: CreateVerificaitonDto) {
    const res = await this.prismaService.accountVerification.findUnique({
      where: {
        id,
      },
    });
    if (!res)
      throw new HttpException(
        'Account Verification not found',
        HttpStatus.NOT_FOUND,
      );
    return await this.prismaService.accountVerification.update({
      where: {
        id,
      },
      data: {
        ...createVerificationDto,
        verificationStatus: VerificationStatuses.PENDING_VERIFICATION,
      },
    });
  }

  async findVerification(fileName: string, userId: string) {
    const res = await this.prismaService.accountVerification.findFirst({
      where: {
        officialDocument: fileName,
        userId,
      },
    });
    if (!res) throw new HttpException('Access Denied', HttpStatus.UNAUTHORIZED);
    return true;
  }
}
