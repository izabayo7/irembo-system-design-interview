import { VerificationStatuses } from '@prisma/client';
export class Verification {
  readonly id: string;
  readonly userId: string;
  readonly officialDocument: string;
  readonly nidOrPassport: string;
  readonly createdAt: Date;
  readonly updatedAt: Date;
  readonly verificationStatus: VerificationStatuses;
}
