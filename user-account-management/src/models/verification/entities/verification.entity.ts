import { VerificationStatuses } from '@prisma/client';
import { User } from 'src/models/user/entities/user.entity';

export class Verification {
  readonly id: string;
  readonly userId: string;
  readonly user?: User;
  readonly officialDocument: string;
  readonly nidOrPassport: string;
  readonly createdAt: Date;
  readonly updatedAt: Date;
  readonly verificationStatus: VerificationStatuses;
}
