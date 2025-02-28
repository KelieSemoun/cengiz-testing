import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';
import { of } from 'rxjs';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpClientMock: jest.Mocked<HttpClient>;

  beforeEach(() => {
    httpClientMock = {
      get: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ],
      providers: [
        TeacherService,
        {provide: HttpClient, useValue: httpClientMock}
      ]
    });
    service = TestBed.inject(TeacherService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve the teacher informations', (done) => {
    const mockTeacher: Teacher = {
      id: 1,
      firstName: 'Margaux',
      lastName: 'Dupont',
      createdAt: new Date(),
      updatedAt: new Date()
    }
    httpClientMock.get.mockReturnValue(of(mockTeacher));

    service.detail('1').subscribe((teacher) => {
      expect(teacher).toEqual(mockTeacher);
      expect(httpClientMock.get).toBeCalledWith('api/teacher/1');
      done();
    });
  });
});
