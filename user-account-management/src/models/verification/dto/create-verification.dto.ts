import { IsNotEmpty, MinLength } from 'class-validator';

export class CreateVerificaitonDto {
  @IsNotEmpty()
  @MinLength(6, { message: ' The min length of nidOrPassport is 6 ' })
  readonly nidOrPassport: string;

  officialDocument?: string;
}
