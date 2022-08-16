import { Test, TestingModule } from '@nestjs/testing';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { v4 } from 'uuid';
import { UpdateUserDto } from './dto/update-user.dto';
import { CreateUserDto } from './dto/create-user.dto';
import { Readable } from 'stream';
import { HttpException } from '@nestjs/common';

const file: Express.Multer.File = {
  fieldname: 'avatar',
  originalname: 'test-key',
  encoding: '7bit',
  mimetype: 'image/jpeg',
  buffer: Buffer.from('Test body', 'utf-8'),
  size: 9,
  stream: new Readable(),
  destination: '',
  filename: 'test.jpg',
  path: '',
};

describe('UserController Unit Tests', () => {
  let controller: UserController;
  let spyService: UserService;

  beforeAll(async () => {
    const ApiServiceProvider = {
      provide: UserService,
      useFactory: () => ({
        create: jest.fn().mockImplementation((createUserDto: CreateUserDto) =>
          Promise.resolve({
            id: v4(),
            role: createUserDto.role,
            email: createUserDto.email,
            firstName: createUserDto.firstName,
            lastName: createUserDto.lastName,
            nationality: createUserDto.nationality,
            createdAt: new Date(),
            updatedAt: new Date(),
          }),
        ),
        findAll: jest.fn().mockImplementation(() =>
          Promise.resolve([
            {
              id: v4(),
              role: 'role',
              email: 'email',
              firstName: 'firstName',
              lastName: 'lastName',
              dateOfBirth: new Date(),
              gender: 'FEMALE',
              maritalStatus: 'SINGLE',
              nationality: 'RWANDAN',
              createdAt: new Date(),
              updatedAt: new Date(),
            },
            {
              id: v4(),
              role: 'role',
              email: 'email',
              firstName: 'firstName',
              lastName: 'lastName',
              dateOfBirth: new Date(),
              gender: 'FEMALE',
              maritalStatus: 'SINGLE',
              nationality: 'RWANDAN',
              createdAt: new Date(),
              updatedAt: new Date(),
            },
            {
              id: v4(),
              role: 'role',
              email: 'email',
              firstName: 'firstName',
              lastName: 'lastName',
              dateOfBirth: new Date(),
              gender: 'FEMALE',
              maritalStatus: 'SINGLE',
              nationality: 'RWANDAN',
              createdAt: new Date(),
              updatedAt: new Date(),
            },
          ]),
        ),
        findOne: jest.fn().mockImplementation((id: string) =>
          Promise.resolve({
            id,
            role: 'role',
            email: 'email',
            firstName: 'firstName',
            lastName: 'lastName',
            dateOfBirth: new Date(),
            gender: 'FEMALE',
            maritalStatus: 'SINGLE',
            nationality: 'RWANDAN',
            createdAt: new Date(),
            updatedAt: new Date(),
          }),
        ),
        update: jest
          .fn()
          .mockImplementation((id: string, updateUserDto: UpdateUserDto) =>
            Promise.resolve({
              id,
              role: 'role',
              email: updateUserDto.email,
              firstName: updateUserDto.firstName,
              lastName: updateUserDto.lastName,
              nationality: updateUserDto.nationality,
              createdAt: new Date(),
              updatedAt: new Date(),
            }),
          ),
        delete: jest.fn().mockImplementation((id: string) =>
          Promise.resolve({
            id,
            role: 'role',
            email: 'email',
            firstName: 'firstName',
            lastName: 'lastName',
            dateOfBirth: new Date(),
            gender: 'FEMALE',
            maritalStatus: 'SINGLE',
            nationality: 'RWANDAN',
            createdAt: new Date(),
            updatedAt: new Date(),
          }),
        ),
      }),
    };

    const module: TestingModule = await Test.createTestingModule({
      controllers: [UserController],
      providers: [UserService, ApiServiceProvider],
    }).compile();

    controller = module.get<UserController>(UserController);
    spyService = module.get<UserService>(UserService);
  });

  afterEach(() => jest.clearAllMocks());

  // it('Create user --> 201', async () => {
  //   const user: CreateUserDto = {
  //     email: 'email1@gmail.com',
  //     password: 'password',
  //     role: 'USER',
  //     firstName: 'firstName',
  //     lastName: 'lastName',
  //     dateOfBirth: new Date(),
  //     gender: 'FEMALE',
  //     maritalStatus: 'SINGLE',
  //     nationality: 'RWANDAN',
  //   };
  //   controller.create(file, user);
  //   expect(spyService.create).toHaveBeenCalled();
  //   expect(spyService.create).toHaveBeenCalledWith(user);
  // });

  // it('Create user --> 400 when you dont upoad a file', async () => {
  //   const user: CreateUserDto = {
  //     email: 'email1@gmail.com',
  //     password: 'password',
  //     role: 'USER',
  //     firstName: 'firstName',
  //     lastName: 'lastName',
  //     dateOfBirth: new Date(),
  //     gender: 'FEMALE',
  //     maritalStatus: 'SINGLE',
  //     nationality: 'RWANDAN',
  //   };
  //   expect(() => {
  //     controller.create(null, user);
  //   }).toThrow(HttpException);
  //   // try {
  //   //   controller.create(null, user);
  //   //   // Fail test if above expression doesn't throw anything.
  //   //   expect(true).toBe(false);
  //   // } catch (e) {
  //   //   expect(e.message).toBe('No file uploaded');
  //   // }
  // });

  // it('Create Password Reset --> 201', async () => {
  //   const body: CreatePasswordResetDto = {
  //     email: 'email1@gmail.com',
  //   };
  //   controller.createPasswordReset(body);
  //   expect(spyService.createPasswordReset).toHaveBeenCalled();
  //   expect(spyService.createPasswordReset).toHaveBeenCalledWith(body);
  // });

  // it('Update Password Reset --> 201', async () => {
  //   const body: UpdatePasswordResetDto = {
  //     password: 'strongPassword!229',
  //     token: v4(),
  //   };
  //   controller.updatePasswordReset(body);
  //   expect(spyService.updatePasswordReset).toHaveBeenCalled();
  //   expect(spyService.updatePasswordReset).toHaveBeenCalledWith(body);
  // });

  // it('Create Password Reset --> 201', async () => {
  //   const body: CreatePasswordResetDto = {
  //     email: 'email1@gmail.com',
  //   };
  //   controller.createPasswordReset(body);
  //   expect(spyService.createPasswordReset).toHaveBeenCalled();
  //   expect(spyService.createPasswordReset).toHaveBeenCalledWith(body);
  // });

  // it('Get Password Reset --> 200', async () => {
  //   const token = v4();
  //   controller.getPasswordReset(token);
  //   expect(spyService.findPasswordReset).toHaveBeenCalled();
  //   expect(spyService.findPasswordReset).toHaveBeenCalledWith(token);
  // });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
