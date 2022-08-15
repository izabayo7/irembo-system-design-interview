import { Body, Controller, Post } from '@nestjs/common';
import { ApiBody, ApiTags } from '@nestjs/swagger';
import { CreatePasswordResetDto } from 'src/models/user/dto/create-password-reset.dto';
import { LoginDto } from 'src/models/user/dto/login.dto';
import { UpdatePasswordResetDto } from 'src/models/user/dto/update-password-reset.dto';
import { AuthService } from './auth.service';

@Controller('auth')
@ApiTags('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('login')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        email: {
          type: 'string',
        },
        password: {
          type: 'string',
        },
      },
      required: ['email', 'password'],
    },
  })
  login(@Body() loginDto: LoginDto) {
    return this.authService.login(loginDto);
  }

  @Post('create-password-reset')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        email: {
          type: 'string',
        },
      },
      required: ['email'],
    },
  })
  createPasswordReset(@Body() createPasswordResetDto: CreatePasswordResetDto) {
    return this.authService.createPasswordReset(createPasswordResetDto);
  }

  @Post('updated-password-reset')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        token: {
          type: 'string',
        },
        password: {
          type: 'string',
        },
      },
      required: ['token', 'password'],
    },
  })
  updatePasswordReset(@Body() updatePasswordResetDto: UpdatePasswordResetDto) {
    return this.authService.updatePasswordReset(updatePasswordResetDto);
  }
}
