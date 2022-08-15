import { PartialType } from '@nestjs/mapped-types';
import { CreateVerificaitonDto } from './create-verification.dto';

export class UpdateVerificationDto extends PartialType(CreateVerificaitonDto) { }
