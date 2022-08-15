import { Injectable } from '@nestjs/common';
import { CreateVerificationDto } from './dto/create-verification.dto';
import { UpdateVerificationDto } from './dto/update-verification.dto';

@Injectable()
export class VerificationService {
  create(createVerificationDto: CreateVerificationDto) {
    return 'This action adds a new verification';
  }

  findAll() {
    return `This action returns all verification`;
  }

  findOne(id: number) {
    return `This action returns a #${id} verification`;
  }

  update(id: number, updateVerificationDto: UpdateVerificationDto) {
    return `This action updates a #${id} verification`;
  }

  remove(id: number) {
    return `This action removes a #${id} verification`;
  }
}
