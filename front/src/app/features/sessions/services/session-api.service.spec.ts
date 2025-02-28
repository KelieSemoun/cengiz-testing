import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';
import { of } from 'rxjs';

describe('SessionsService', () => {
  let service: SessionApiService;
  let httpClientMock: jest.Mocked<HttpClient>;

  const mockSession: Session = {
    id: 1,
    name: 'test',
    description: 'mockSession Test',
    date: new Date(),
    teacher_id: 5,
    users: [10, 65, 21],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(() => {
    httpClientMock = {
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ],
      providers: [
        SessionApiService,
        {provide: HttpClient, useValue: httpClientMock}
      ]
    });
    service = TestBed.inject(SessionApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create the session after the create API call', (done) => {
    httpClientMock.post.mockReturnValue(of(mockSession));

    service.create(mockSession).subscribe((session) => {
      expect(session).toEqual(mockSession);
      expect(httpClientMock.post).toHaveBeenCalledWith('api/session', mockSession);
      done();
    });
  });

  it('should delete the session after the delete API call', (done) => {
    httpClientMock.delete.mockReturnValue(of(mockSession));

    service.create(mockSession);
    service.delete('1').subscribe((session) => {
      expect(session).toEqual(mockSession);
      expect(httpClientMock.delete).toHaveBeenCalledWith('api/session/1');
      done();
    });
  });

  it('should update the session after the update API call', (done) => {
    const mockUpdatedSession: Session = {
      id: 1,
      name: 'testUpdated',
      description: 'mockUpdatedSession Test',
      date: new Date(),
      teacher_id: 3,
      users: [2, 54, 33],
      createdAt: new Date(),
      updatedAt: new Date()
    };
    httpClientMock.put.mockReturnValue(of(mockUpdatedSession));

    service.create(mockSession);
    service.update('1', mockUpdatedSession).subscribe((session) => {
      expect(session).toEqual(mockUpdatedSession);
      expect(httpClientMock.put).toHaveBeenCalledWith('api/session/1', mockUpdatedSession);
      done();
    });
  });

  it('should update the session users after the participate API call', (done) => {
    const mockUpdatedSession: Session = {
      id: 1,
      name: 'test',
      description: 'mockSession Test',
      date: new Date(),
      teacher_id: 5,
      users: [10, 65, 21, 33],
      createdAt: new Date(),
      updatedAt: new Date()
    };
    httpClientMock.post.mockReturnValue(of(mockUpdatedSession));

    service.create(mockSession);
    service.participate('1', '33').subscribe((session) => {
      expect(session).toEqual(mockUpdatedSession);
      expect(httpClientMock.post).toHaveBeenCalledWith('api/session/1/participate/33', null);
      done();
    });
  });

  it('should remove the user in the session after the unParticipate API call', (done) => {
    const mockUpdatedSession: Session = {
      id: 1,
      name: 'test',
      description: 'mockSession Test',
      date: new Date(),
      teacher_id: 5,
      users: [10, 65],
      createdAt: new Date(),
      updatedAt: new Date()
    };
    httpClientMock.delete.mockReturnValue(of(mockUpdatedSession));

    service.create(mockSession);
    service.unParticipate('1', '21').subscribe((session) => {
      expect(session).toEqual(mockUpdatedSession);
      expect(httpClientMock.delete).toHaveBeenCalledWith('api/session/1/participate/21');
      done();
    });
  });
});
