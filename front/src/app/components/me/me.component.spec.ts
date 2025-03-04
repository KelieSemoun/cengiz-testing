import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { MeComponent } from './me.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { User } from '../../interfaces/user.interface';
import { SessionService } from '../../services/session.service';
import { UserService } from '../../services/user.service';
import { By } from '@angular/platform-browser';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let mockSessionService: jest.Mocked<SessionService>;
  let mockUserService: jest.Mocked<UserService>;
  let mockRouter: jest.Mocked<Router>;

  beforeEach(async () => {
    mockUserService = {
      getById: jest.fn().mockReturnValue(of({
        id: 1,
        email: 'test@example.com',
        lastName: 'Doe',
        firstName: 'John',
        admin: true,
        password: 'hashedpassword',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as User)),
      delete: jest.fn().mockReturnValue(of(null)),
    } as unknown as jest.Mocked<UserService>;

    mockSessionService = {
      sessionInformation: {
        id: 1,
        admin: true
      },
      logOut: jest.fn(),
    } as unknown as jest.Mocked<SessionService>;

    mockRouter = {
      navigate: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        NoopAnimationsModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: UserService, useValue: mockUserService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should retrieve user details upon component init', () => {
    component.ngOnInit();

    expect(mockUserService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual({
      id: 1,
      email: 'test@example.com',
      lastName: 'Doe',
      firstName: 'John',
      admin: true,
      password: 'hashedpassword',
      createdAt: expect.any(Date),
      updatedAt: expect.any(Date),
    });
  });

  it('should call back() when the button is clicked', () => {
    const historyBackSpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
  
    const button = fixture.debugElement.query(By.css('button')).nativeElement;
    button.click();
    fixture.detectChanges();
    
    expect(historyBackSpy).toHaveBeenCalled();
    historyBackSpy.mockRestore();
  });

  it('should call logOut and navigate on delete', fakeAsync(() => {
    mockUserService.delete = jest.fn().mockReturnValue(of(null));

    component.delete();

    tick();
    fixture.detectChanges(); // Ensure Angular processes changes

    tick(3000); // Waits for the snackBar duration to expire

    expect(mockUserService.delete).toHaveBeenCalledWith('1');
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  }));
});
