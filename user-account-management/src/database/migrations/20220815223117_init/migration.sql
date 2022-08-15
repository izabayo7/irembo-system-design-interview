/*
  Warnings:

  - The values [PENDING] on the enum `VerificationStatuses` will be removed. If these variants are still used in the database, this will fail.

*/
-- AlterEnum
BEGIN;
CREATE TYPE "VerificationStatuses_new" AS ENUM ('UNVERIFIED', 'PENDING_VERIFICATION', 'VERIFIED');
ALTER TABLE "AccountVerification" ALTER COLUMN "verificationStatus" DROP DEFAULT;
ALTER TABLE "PasswordReset" ALTER COLUMN "verificationStatus" DROP DEFAULT;
ALTER TABLE "AccountVerification" ALTER COLUMN "verificationStatus" TYPE "VerificationStatuses_new" USING ("verificationStatus"::text::"VerificationStatuses_new");
ALTER TABLE "PasswordReset" ALTER COLUMN "verificationStatus" TYPE "VerificationStatuses_new" USING ("verificationStatus"::text::"VerificationStatuses_new");
ALTER TYPE "VerificationStatuses" RENAME TO "VerificationStatuses_old";
ALTER TYPE "VerificationStatuses_new" RENAME TO "VerificationStatuses";
DROP TYPE "VerificationStatuses_old";
ALTER TABLE "AccountVerification" ALTER COLUMN "verificationStatus" SET DEFAULT 'UNVERIFIED';
ALTER TABLE "PasswordReset" ALTER COLUMN "verificationStatus" SET DEFAULT 'UNVERIFIED';
COMMIT;
