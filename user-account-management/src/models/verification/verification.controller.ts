import { Controller, Get, Post, Body, Patch, Param, Delete } from '@nestjs/common';
import { VerificationService } from './verification.service';
import { CreateVerificationDto } from './dto/create-verification.dto';
import { UpdateVerificationDto } from './dto/update-verification.dto';

@Controller('verification')
export class VerificationController {
  constructor(private readonly verificationService: VerificationService) {}

  @Post()
  create(@Body() createVerificationDto: CreateVerificationDto) {
    return this.verificationService.create(createVerificationDto);
  }

  @Get()
  findAll() {
    return this.verificationService.findAll();
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.verificationService.findOne(+id);
  }

  @Patch(':id')
  update(@Param('id') id: string, @Body() updateVerificationDto: UpdateVerificationDto) {
    return this.verificationService.update(+id, updateVerificationDto);
  }

  @Delete(':id')
  remove(@Param('id') id: string) {
    return this.verificationService.remove(+id);
  }
}
