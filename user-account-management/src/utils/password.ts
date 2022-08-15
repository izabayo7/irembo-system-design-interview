import * as bcrpyt from 'bcryptjs';

export async function hash(password: string) {
  return await bcrpyt.hash(password, 10);
}

export async function compare(password: string, hashedPassword: string) {
  return await bcrpyt.compare(password, hashedPassword);
}
