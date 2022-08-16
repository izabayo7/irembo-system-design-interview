import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  UseInterceptors,
  UseGuards,
  MaxFileSizeValidator,
  FileTypeValidator,
  ParseFilePipe,
  UploadedFile,
  HttpException,
  HttpStatus,
  Put,
  Request,
  Response,
} from '@nestjs/common';
import { VerificationService } from './verification.service';
import { ApiBearerAuth, ApiBody, ApiConsumes, ApiTags } from '@nestjs/swagger';
import { FileInterceptor } from '@nestjs/platform-express';
import { JwtAuthGuard } from 'src/common/guards/jwt-auth.guard';
import { CreateVerificaitonDto } from './dto/create-verification.dto';
import { v4 } from 'uuid';
import { createReadStream, existsSync, writeFile } from 'fs';
import { Roles } from '@prisma/client';

// TODO add handle for when user verification is rejected
@ApiTags('verification')
@Controller('verification')
export class VerificationController {
  constructor(private readonly verificationService: VerificationService) { }

  @Put(':id')
  @ApiBearerAuth('access-token')
  @UseInterceptors(FileInterceptor('officialDocument'))
  @UseGuards(JwtAuthGuard)
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        officialDocument: {
          type: 'string',
          format: 'binary',
        },
        nidOrPassport: {
          type: 'string',
        },
      },
      required: ['officialDocument', 'nidOrPassport'],
    },
  })
  async uploadVerificationInfo(
    @Param('id') id: string,
    @UploadedFile(
      new ParseFilePipe({
        validators: [
          new MaxFileSizeValidator({ maxSize: 1000000 }),
          new FileTypeValidator({
            fileType: /^.*(jpg|JPG|jpeg|JPEG|png|PNG|gif|GIF)$/,
          }),
        ],
      }),
    )
    file: Express.Multer.File,
    @Body() createVerificationDto: CreateVerificaitonDto,
  ) {
    if (!file) {
      throw new HttpException('No file uploaded', HttpStatus.BAD_REQUEST);
    }

    let fileName: any = file.originalname.split('.');
    const extension = fileName[fileName.length - 1];
    fileName = `${v4()}.${extension}`;
    const path = `${process.env.userVerificationFilesPath}/${fileName}`;

    createVerificationDto.officialDocument = fileName;

    const response = await this.verificationService.update(
      id,
      createVerificationDto,
    );

    writeFile(path, file.buffer, async (err) => {
      if (err) {
        await this.verificationService.update(response.id, {
          officialDocument: null,
          nidOrPassport: ''
        });
        throw new HttpException(
          'Error uploading profile photo',
          HttpStatus.INTERNAL_SERVER_ERROR,
        );
      }
      return response;
    });

    return response;
  }

  @Post(':id')
  @ApiBearerAuth('access-token')
  @UseGuards(JwtAuthGuard)
  verify(@Param('id') id: string, @Request() req) {
    if (req.user.role !== Roles.ADMIN)
      throw new HttpException('Unauthorized', HttpStatus.UNAUTHORIZED);

    return this.verificationService.verifyAccount(id);
  }

  @Get('/document/:fileName')
  @UseGuards(JwtAuthGuard)
  async getUserProfile(@Request() req, @Response() res) {
    if (req.user.role !== Roles.ADMIN)
      await this.verificationService.findVerification(
        req.params.fileName,
        req.user.id,
      );

    const path = `${process.env.userVerificationFilesPath}/${req.params.fileName}`;
    if (!existsSync(path)) {
      throw new HttpException('File not found', HttpStatus.NOT_FOUND);
    }

    res.sendFile(path);
  }
}
