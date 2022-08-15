export class User {
  readonly id: string;
  readonly firstName: string;
  readonly lastName: string;
  readonly gender: string;
  readonly email: string;
  readonly dateOfBirth: string;
  readonly profilePhoto?: string;
  readonly tfaEnabled?: string;
  readonly tfaSecret?: string;
  readonly password: string;
  readonly createdAt: Date;
  readonly updatedAt: Date;
  readonly maritalStatus: string;
  readonly role: string;
  // accountVerification
}
