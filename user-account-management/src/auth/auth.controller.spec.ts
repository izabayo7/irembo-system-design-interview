import { ConfigService } from '@nestjs/config';
import { JwtModule } from '@nestjs/jwt';
import { PassportModule } from '@nestjs/passport';
import { Test, TestingModule } from '@nestjs/testing';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { JwtStrategy } from './strategies/jwt.strategy';

describe.skip('AuthController', () => {
  let Controller: AuthController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [
        ConfigService,
        PassportModule.register({ defaultStrategy: 'jwt ' }),
        JwtModule.register({
          secret: process.env.JWT_SECRET,
          signOptions: {
            expiresIn: '24h',
          },
        }),
      ],
      providers: [AuthController, AuthService, JwtStrategy, ConfigService],
    }).compile();

    Controller = module.get<AuthController>(AuthController);
  });

  it('should be defined', () => {
    expect(Controller).toBeDefined();
  });
});
