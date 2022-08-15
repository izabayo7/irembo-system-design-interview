import { Module } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { JwtModule } from '@nestjs/jwt';
import { PassportModule } from '@nestjs/passport';
import { PrismaService } from 'src/database/services/prisma.service';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { JwtStrategy } from './strategies/jwt.strategy';

@Module({
  imports: [
    PassportModule.register({ defaultStrategy: 'jwt ' }),
    JwtModule.register({
      secret: process.env.JWT_SECRET,
      signOptions: {
        // TODO reduce the time to 60s
        expiresIn: '4h',
      },
    }),
  ],
  providers: [
    AuthController,
    AuthService,
    JwtStrategy,
    ConfigService,
    PrismaService,
  ],
  exports: [AuthService, JwtModule],
  controllers: [AuthController],
})
export class AuthModule {}
