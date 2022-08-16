import { Test, TestingModule } from '@nestjs/testing';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { UpdatePasswordResetDto } from '../models/user/dto/update-password-reset.dto';
import { CreatePasswordResetDto } from '../models/user/dto/create-password-reset.dto';
import { LoginDto } from '../models/user/dto/login.dto';
import { v4 } from 'uuid';

describe('AuthController Unit Tests', () => {
  let controller: AuthController;
  let spyService: AuthService;

  beforeAll(async () => {
    const ApiServiceProvider = {
      provide: AuthService,
      useFactory: () => ({
        login: jest.fn().mockImplementation((loginDto: LoginDto) =>
          Promise.resolve({
            user: {
              id: '1',
              email: loginDto.email,
            },
            accessToken: 'accessToken',
            refreshToken: 'refreshToken',
          }),
        ),
        createPasswordReset: jest
          .fn()
          .mockImplementation(
            (createPasswordResetDto: CreatePasswordResetDto) =>
              Promise.resolve({
                id: 'id',
                isActive: false,
                validUntil: new Date(Date.now() + 1000 * 60 * 20),
              }),
          ),
        updatePasswordReset: jest
          .fn()
          .mockImplementation(
            (updatePasswordResetDto: UpdatePasswordResetDto) =>
              Promise.resolve({
                id: 'id',
                isActive: false,
                token: updatePasswordResetDto.token,
              }),
          ),
        findPasswordReset: jest.fn().mockImplementation((token: string) =>
          Promise.resolve({
            id: 'id',
            userId: 'userId',
            token,
            isActive: true,
            validUntil: new Date(),
            createdAt: new Date(),
            updatedAt: new Date(),
            expired: false,
          }),
        ),
      }),
    };

    const module: TestingModule = await Test.createTestingModule({
      controllers: [AuthController],
      providers: [AuthService, ApiServiceProvider],
    }).compile();

    controller = module.get<AuthController>(AuthController);
    spyService = module.get<AuthService>(AuthService);
  });

  afterEach(() => jest.clearAllMocks());

  it('Login --> 201', async () => {
    const body: LoginDto = {
      email: 'email1@gmail.com',
      password: 'password',
    };
    controller.login(body);
    expect(spyService.login).toHaveBeenCalled();
    expect(spyService.login).toHaveBeenCalledWith(body);
  });

  it('Create Password Reset --> 201', async () => {
    const body: CreatePasswordResetDto = {
      email: 'email1@gmail.com',
    };
    controller.createPasswordReset(body);
    expect(spyService.createPasswordReset).toHaveBeenCalled();
    expect(spyService.createPasswordReset).toHaveBeenCalledWith(body);
  });

  it('Update Password Reset --> 201', async () => {
    const body: UpdatePasswordResetDto = {
      password: 'strongPassword!229',
      token: v4(),
    };
    controller.updatePasswordReset(body);
    expect(spyService.updatePasswordReset).toHaveBeenCalled();
    expect(spyService.updatePasswordReset).toHaveBeenCalledWith(body);
  });

  it('Create Password Reset --> 201', async () => {
    const body: CreatePasswordResetDto = {
      email: 'email1@gmail.com',
    };
    controller.createPasswordReset(body);
    expect(spyService.createPasswordReset).toHaveBeenCalled();
    expect(spyService.createPasswordReset).toHaveBeenCalledWith(body);
  });

  it('Get Password Reset --> 200', async () => {
    const token = v4();
    controller.getPasswordReset(token);
    expect(spyService.findPasswordReset).toHaveBeenCalled();
    expect(spyService.findPasswordReset).toHaveBeenCalledWith(token);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
