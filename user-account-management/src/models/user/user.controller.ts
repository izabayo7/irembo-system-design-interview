import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
} from '@nestjs/common';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { LoginDto } from './dto/login.dto';
import { CreatePasswordResetDto } from './dto/create-password-reset.dto';
import { UpdatePasswordResetDto } from './dto/update-password-reset.dto';

@Controller('user')
export class UserController {
  constructor(private readonly userService: UserService) {}

  //   user

  // - signup
  // - signin
  // - reset password

  // - update account protected
  // - delete account protected
  // - view accounts protected

  @Post()
  create(@Body() createUserDto: CreateUserDto) {
    return this.userService.create(createUserDto);
  }

  @Post()
  login(@Body() loginDto: LoginDto) {
    return this.userService.login(loginDto);
  }

  @Post()
  createPasswordReset(@Body() createPasswordResetDto: CreatePasswordResetDto) {
    return this.userService.createPasswordReset(createPasswordResetDto);
  }

  @Post()
  updatePasswordReset(@Body() updatePasswordResetDto: UpdatePasswordResetDto) {
    return this.userService.updatePasswordReset(updatePasswordResetDto);
  }

  @Get()
  findAll() {
    return this.userService.findAll();
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.userService.findOne(+id);
  }

  @Patch(':id')
  update(@Param('id') id: string, @Body() updateUserDto: UpdateUserDto) {
    return this.userService.update(+id, updateUserDto);
  }

  @Delete(':id')
  remove(@Param('id') id: string) {
    return this.userService.remove(+id);
  }
}
