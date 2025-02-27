import { inject, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return false upon $isLogged call on a default Service', () => {
    let latestValue : Boolean = true;
    service.$isLogged().subscribe((value : Boolean) => latestValue = value);
    expect(latestValue).toBe(false);
  });

  it('should successfully update sessionService attributes upon user login', () => {

    const mockUser: SessionInformation = {
      token: 'test-token',
      type: 'user',
      id: 1,
      username: 'testUser',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };

    service.logIn(mockUser);

    expect(service.sessionInformation).toEqual(mockUser);
    expect(service.isLogged).toBe(true);
  });

  it('should successfully update sessionService attributes upon user logout', () => {

    const mockUser: SessionInformation = {
      token: 'test-token',
      type: 'user',
      id: 1,
      username: 'testUser',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };

    service.logIn(mockUser);
    service.logOut();

    expect(service.sessionInformation).toEqual(undefined);
    expect(service.isLogged).toBe(false);
  });
});
