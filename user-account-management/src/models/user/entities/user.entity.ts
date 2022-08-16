import { Genders, MaritalStatuses, Roles } from '@prisma/client';

export class User {
  readonly id: string;
  readonly firstName: string;
  readonly lastName: string;
  readonly gender: Genders;
  readonly email: string;
  readonly dateOfBirth: string;
  readonly profilePhoto?: string;
  readonly tfaEnabled?: string;
  readonly tfaSecret?: string;
  readonly password: string;
  readonly createdAt: Date;
  readonly updatedAt: Date;
  readonly maritalStatus: MaritalStatuses;
  readonly role: Roles;
}
