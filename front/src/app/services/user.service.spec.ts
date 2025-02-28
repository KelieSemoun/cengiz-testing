import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';
import { of } from 'rxjs';
import { User } from '../interfaces/user.interface';

describe('UserService', () => {
  let service: UserService;
  let httpClientMock: jest.Mocked<HttpClient>;

  beforeEach(() => {
    httpClientMock = {
      delete: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ],
      providers: [
        UserService,
        {provide: HttpClient, useValue: httpClientMock}
      ]
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should delete the user', (done) => {
    const mockUser: User = {
      id: 1,
      email: 'test@user.com',
      firstName: 'Margaux',
      lastName: 'Dupont',
      admin: true,
      password:'test!1234',
      createdAt: new Date(),
      updatedAt: new Date()
    }
    httpClientMock.delete.mockReturnValue(of(mockUser));
    
    service.delete('1').subscribe((user) => {
      expect(user).toEqual(mockUser);
      expect(httpClientMock.delete).toBeCalledWith('api/user/1');
      done();
    });
  })
});
