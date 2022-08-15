import { IsNotEmpty } from 'class-validator';

export class CreateVerificaitonDto {
  @IsNotEmpty()
  readonly nidOrPassport: string;

  officialDocument?: string;
}
