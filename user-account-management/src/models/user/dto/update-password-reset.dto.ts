import {
  IsNotEmpty,
  Matches,
  MaxLength,
  MinLength,
  IsUUID,
} from 'class-validator';

export class UpdatePasswordResetDto {
  @IsNotEmpty()
  @IsUUID()
  readonly token: string;

  @IsNotEmpty()
  @MinLength(8, { message: ' The min length of password is 8 ' })
  @MaxLength(20, {
    message: " The password can't accept more than 20 characters ",
  })
  @Matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,20})/, {
    message:
      ' A password at least contains one numeric digit, one supercase char and one lowercase char',
  })
  readonly password: string;
}
