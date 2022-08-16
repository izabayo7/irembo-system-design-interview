import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { JwtService } from '@nestjs/jwt';
import { resolve } from 'path';
import { ResponseStatus } from '../src/common/enums/responseStatus.enum';

let fileUploadResponseStructure: { status: ResponseStatus; url: string } = {
  status: expect.any(String),
  url: expect.any(String),
};

let multipleFilesUploadResponseStructure: {
  status: ResponseStatus;
  urls: string[];
} = {
  status: expect.any(String),
  urls: expect.arrayContaining([expect.any(String)]),
};

const getFilePath = (url: string): string => {
  return url.split(process.env.FILE_API_CDN_URL+'/')[1];
};

describe('FileController (e2e)', () => {
  let app: INestApplication;
  let token: String;
  let jwtService: JwtService = new JwtService();

  const fileUrlExamples: {
    CourseChatFileUrl?: string;
    CourseMaterialFileUrl?: string;
    CourseAssignmentFileUrl?: string;
    ChatFileUrl?: string;
    UserDriveFileUrl?: string;
    UserOtherFileUrl?: string;

    MultipleCourseChatFilesUrls?: string[];
    MultipleCourseMaterialFileUrls?: string[];
    MultipleCourseAssignmentFileUrls?: string[];
    MultipleChatFileUrls?: string[];
    MultipleUserDriveFileUrls?: string[];
    MultipleUserOtherFileUrls?: string[];
  } = {};

  const paths: {
    CourseChatFiles: string;
    CourseMaterialFiles: string;
    CourseAssignmentFiles: string;
    ChatFiles: string;
    UserDriveFiles: string;
    UserOtherFiles: string;
  } = {
    CourseChatFiles:
      'GradeArcFiles/Districts/district_id/Schools/school_id/Courses/course_id/Chat/',
    CourseMaterialFiles:
      'GradeArcFiles/Districts/district_id/Schools/school_id/Courses/course_id/Materials/',
    CourseAssignmentFiles:
      'GradeArcFiles/Districts/district_id/Schools/school_id/Courses/course_id/Assignments/assignment_id/',
    ChatFiles:
      'GradeArcFiles/Districts/district_id/Schools/school_id/Chats/message_id/',
    UserDriveFiles:
      'GradeArcFiles/Districts/district_id/Schools/school_id/Users/user_id/GradeArcDrive/',
    UserOtherFiles:
      'GradeArcFiles/Districts/district_id/Schools/school_id/Users/user_id/Other/',
  };

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    token = jwtService.sign(
      {},
      {
        secret: process.env.JWT_SECRET,
        expiresIn: '4h',
      },
    );

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  it(`/POST uploadFile -> 401 when request is not authenticated`, () => {
    return request(app.getHttpServer()).post('/files/uploadFile').expect(401);
  });

  it(`/GET readFile -> 401 when request is not authenticated`, () => {
    return request(app.getHttpServer()).get('/files/readFile').expect(401);
  });

  it(`/DELETE deleteFile -> 401 when request is not authenticated`, () => {
    return request(app.getHttpServer()).delete('/files/deleteFile').expect(401);
  });

  it(`/POST uploadFile -> 201 Upload a Course Chat File`, () => {
    return request(app.getHttpServer())
      .post('/files/uploadFile')
      .set('Content-Type', 'multipart/form-data')
      .set('Authorization', 'Bearer ' + token)
      .field('path', paths.CourseChatFiles)
      .field('public', 'false')
      .attach('file', resolve('test/files/test.pdf'))
      .expect(201)
      .expect((res) => {
        expect(res.body).toMatchObject(fileUploadResponseStructure);
        fileUrlExamples.CourseChatFileUrl = getFilePath(res.body.url);
        expect(res.body.status).toEqual(ResponseStatus.SUCCESS);
      });
  });

  it(`/GET readFile -> 200 Read a Private File`, () => {
    return request(app.getHttpServer())
      .get(`/files/readFile?path=${fileUrlExamples.CourseChatFileUrl}`)
      .set('Authorization', 'Bearer ' + token)
      .expect(302);
  });

  it(`/DELETE deleteFile -> 200 Delete a Course Chat File`, () => {
    return request(app.getHttpServer())
      .delete(`/files/deleteFile?path=${fileUrlExamples.CourseChatFileUrl}`)
      .set('Authorization', 'Bearer ' + token)
      .expect(200);
  });

  it(`/POST uploadMultipleFiles -> 201 Upload Multiple Course Chat Files`, () => {
    return request(app.getHttpServer())
      .post('/files/uploadFiles')
      .set('Content-Type', 'multipart/form-data')
      .set('Authorization', 'Bearer ' + token)
      .field('path', paths.CourseChatFiles)
      .field('public', 'false')
      .attach('files', resolve('test/files/test.pdf'))
      .attach('files', resolve('test/files/test.mp3'))
      .expect(201)
      .expect((res) => {
        expect(res.body).toMatchObject(multipleFilesUploadResponseStructure);
        fileUrlExamples.MultipleCourseChatFilesUrls = res.body.urls.map((x) =>
          getFilePath(x),
        );
        expect(res.body.status).toEqual(ResponseStatus.SUCCESS);
      });
  });

  it(`/DELETE deleteFiles -> 200 Delete Multiple Course Chat Files`, () => {
    return request(app.getHttpServer())
      .delete(`/files/deleteFiles?paths=${fileUrlExamples.MultipleCourseChatFilesUrls.join('&paths=')}`)
      .set('Authorization', 'Bearer ' + token)
      .expect(200);
  });

  afterAll(async () => {
    await app.close();
  });
});
