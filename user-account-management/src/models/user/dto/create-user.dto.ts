import { Genders, MaritalStatuses, Roles } from '@prisma/client';
import { IsDate, IsEnum, IsNotEmpty } from 'class-validator';
import { LoginDto } from './login.dto';

export class CreateUserDto extends LoginDto {
  @IsNotEmpty()
  readonly firstName: string;

  @IsNotEmpty()
  readonly lastName: string;

  @IsNotEmpty()
  @IsEnum(Genders, { each: true })
  readonly gender: Genders;

  @IsNotEmpty()
  @IsEnum(MaritalStatuses, { each: true })
  readonly maritalStatus: MaritalStatuses;

  @IsEnum(Roles, { each: true })
  readonly role: Roles;

  @IsNotEmpty()
  @IsDate()
  readonly dateOfBirth: string;
}
