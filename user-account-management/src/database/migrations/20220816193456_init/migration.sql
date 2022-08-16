/*
  Warnings:

  - You are about to drop the column `verificationStatus` on the `PasswordReset` table. All the data in the column will be lost.

*/
-- AlterTable
ALTER TABLE "PasswordReset" DROP COLUMN "verificationStatus";

-- AlterTable
ALTER TABLE "User" ADD COLUMN     "nationality" TEXT NOT NULL DEFAULT 'rwandan';
