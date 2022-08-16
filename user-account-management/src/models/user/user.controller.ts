import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  UseGuards,
  Put,
  UploadedFile,
  UseInterceptors,
  HttpException,
  HttpStatus,
  ParseFilePipe,
  MaxFileSizeValidator,
  FileTypeValidator,
  Request,
  Res,
  StreamableFile,
  Response,
} from '@nestjs/common';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { ApiBearerAuth, ApiBody, ApiConsumes, ApiTags } from '@nestjs/swagger';
import { JwtAuthGuard } from 'src/common/guards/jwt-auth.guard';
import { FileInterceptor } from '@nestjs/platform-express/multer/interceptors';
import { v4 } from 'uuid';
import { createReadStream, existsSync, writeFile } from 'fs';

@Controller('users')
@ApiTags('users')
export class UserController {
  constructor(private readonly userService: UserService) { }

  //   user

  // - signup
  // - signin
  // - reset password

  // - update account protected
  // - delete account protected
  // - view accounts protected

  @Post()
  @UseInterceptors(FileInterceptor('profilePhoto'))
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        profilePhoto: {
          type: 'string',
          format: 'binary',
        },
        firstName: {
          type: 'string',
        },
        email: {
          type: 'string',
        },
        password: {
          type: 'string',
        },
        lastName: {
          type: 'string',
        },
        gender: {
          type: 'string',
          enum: ['MALE', 'FEMALE'],
        },
        maritalStatus: {
          type: 'string',
          enum: ['SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED'],
        },
        role: {
          type: 'string',
          enum: ['USER', 'ADMIN'],
        },
        dateOfBirth: {
          type: 'date',
        },
      },
      required: [
        'profilePhoto',
        'firstName',
        'email',
        'password',
        'lastName',
        'gender',
        'maritalStatus',
        'dateOfBirth',
      ],
    },
  })
  async create(
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
    @Body() createUserDto: CreateUserDto,
  ) {
    if (!file) {
      throw new HttpException('No file uploaded', HttpStatus.BAD_REQUEST);
    }

    let fileName: any = file.originalname.split('.');
    const extension = fileName[fileName.length - 1];
    fileName = `${v4()}.${extension}`;
    const path = `${process.env.userProfilePicturesPath}/${fileName}`;

    createUserDto.profilePhoto = fileName;

    const response = await this.userService.create(createUserDto);

    writeFile(path, file.buffer, async (err) => {
      if (err) {
        await this.userService.update(response.id, { profilePhoto: null });
        throw new HttpException(
          'Error uploading profile photo',
          HttpStatus.INTERNAL_SERVER_ERROR,
        );
      }
    });

    return response;
  }

  @Get()
  @ApiBearerAuth('access-token')
  @UseGuards(JwtAuthGuard)
  @ApiConsumes('multipart/form-data')
  findAll() {
    return this.userService.findAll();
  }

  @Get('current')
  @ApiBearerAuth('access-token')
  @UseGuards(JwtAuthGuard)
  getCurrentUser(@Request() req) {
    return { ...req.user, password: undefined, tfaSecret: undefined };
  }

  @Get('/profile/:fileName')
  @UseGuards(JwtAuthGuard)
  getUserProfile(@Request() req, @Response() res) {
    const path = `${process.env.userProfilePicturesPath}/${req.params.fileName}`;
    if (!existsSync(path)) {
      throw new HttpException('File not found', HttpStatus.NOT_FOUND);
    }
    return res.sendFile(path);
  }

  @Get(':id')
  @ApiBearerAuth('access-token')
  @UseGuards(JwtAuthGuard)
  findOne(@Param('id') id: string) {
    return this.userService.findOne(id);
  }

  @Put(':id')
  @ApiBearerAuth('access-token')
  @UseGuards(JwtAuthGuard)
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        firstName: {
          type: 'string',
        },
        email: {
          type: 'string',
        },
        password: {
          type: 'string',
        },
        lastName: {
          type: 'string',
        },
        gender: {
          type: 'string',
          enum: ['MALE', 'FEMALE'],
        },
        maritalStatus: {
          type: 'string',
          enum: ['SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED'],
        },
        role: {
          type: 'string',
          enum: ['USER', 'ADMIN'],
        },
        dateOfBirth: {
          type: 'date',
        },
      },
      required: [
        'firstName',
        'email',
        'password',
        'lastName',
        'gender',
        'maritalStatus',
        'dateOfBirth',
      ],
    },
  })
  async update(@Param('id') id: string, @Body() updateUserDto: UpdateUserDto) {
    return await this.userService.update(id, updateUserDto);
  }

  @Put('with-file/:id')
  @ApiBearerAuth('access-token')
  @UseInterceptors(FileInterceptor('profilePhoto'))
  @UseGuards(JwtAuthGuard)
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        profilePhoto: {
          type: 'string',
          format: 'binary',
        },
        firstName: {
          type: 'string',
        },
        email: {
          type: 'string',
        },
        password: {
          type: 'string',
        },
        lastName: {
          type: 'string',
        },
        gender: {
          type: 'string',
          enum: ['MALE', 'FEMALE'],
        },
        maritalStatus: {
          type: 'string',
          enum: ['SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED'],
        },
        role: {
          type: 'string',
          enum: ['USER', 'ADMIN'],
        },
        dateOfBirth: {
          type: 'date',
        },
      },
      required: [
        'profilePhoto',
        'firstName',
        'email',
        'password',
        'lastName',
        'gender',
        'maritalStatus',
        'dateOfBirth',
      ],
    },
  })
  async updateWithFile(
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
    @Body() updateUserDto: UpdateUserDto,
  ) {
    let fileName: any = file.originalname.split('.');
    const extension = fileName[fileName.length - 1];
    fileName = `${v4()}.${extension}`;
    const path = `${process.env.userProfilePicturesPath}/${fileName}`;

    updateUserDto.profilePhoto = fileName;
    // TODO delete existing profile
    const response = await this.userService.update(id, updateUserDto);

    writeFile(path, file.buffer, async (err) => {
      if (err) {
        await this.userService.update(response.id, { profilePhoto: null });
        throw new HttpException(
          'Error uploading profile photo',
          HttpStatus.INTERNAL_SERVER_ERROR,
        );
      }
      return response;
    });
  }

  // TODO add api to serve user profile photo
  @Delete(':id')
  @ApiBearerAuth('access-token')
  @UseGuards(JwtAuthGuard)
  remove(@Param('id') id: string) {
    return this.userService.remove(id);
  }
}
