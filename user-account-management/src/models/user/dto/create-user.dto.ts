import { Genders, MaritalStatuses, Nationalities, Roles } from '@prisma/client';
import { IsDateString, IsEnum, IsNotEmpty } from 'class-validator';
import { LoginDto } from './login.dto';

export class CreateUserDto extends LoginDto {
  @IsNotEmpty()
  readonly firstName: string;

  @IsNotEmpty()
  readonly lastName: string;

  @IsNotEmpty()
  @IsEnum(Nationalities, { each: true })
  readonly nationality: Nationalities;

  @IsNotEmpty()
  @IsEnum(Genders, { each: true })
  readonly gender: Genders;

  @IsNotEmpty()
  @IsEnum(MaritalStatuses, { each: true })
  readonly maritalStatus: MaritalStatuses;

  @IsEnum(Roles, { each: true })
  readonly role?: Roles;

  @IsNotEmpty()
  @IsDateString()
  readonly dateOfBirth: Date;

  profilePhoto?: string;
}
