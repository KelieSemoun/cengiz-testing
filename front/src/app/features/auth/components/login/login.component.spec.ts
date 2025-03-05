import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { AuthService } from '../../services/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let sessionService: SessionService;
  let router: Router;
  let compiled: HTMLElement;

  const mockAuthService = {
    login: jest.fn()
  };

  const mockSessionService = {
    logIn: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService }
      ],
      imports: [
        RouterTestingModule.withRoutes([]),
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    compiled = fixture.nativeElement;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('submit()', () => {
    beforeEach(() => {
      component.form.setValue({
        email: 'test@example.com',
        password: 'password123'
      });
    });

    it('should call login() and navigate to "/sessions" on success', () => {
      const mockResponse = { id: 1, email: 'test@example.com', token: 'abc123' };
      mockAuthService.login.mockReturnValue(of(mockResponse));

      const navigateSpy = jest.spyOn(router, 'navigate');

      fixture.ngZone?.run(() => {
        component.submit();
      });

      expect(authService.login).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password123'
      });
      expect(sessionService.logIn).toHaveBeenCalledWith(mockResponse);
      expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
      expect(component.onError).toBe(false);
    });

    it('should set onError to true when login fails', () => {
      mockAuthService.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));

      fixture.ngZone?.run(() => {
        component.submit();
      });

      expect(authService.login).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password123'
      });
      expect(component.onError).toBe(true);
    });
  });

  describe('Integration Tests', () => {
    it('should have all form fields', () => {
      expect(compiled.querySelector('input[formControlName="email"]')).toBeTruthy();
      expect(compiled.querySelector('input[formControlName="password"]')).toBeTruthy();
      expect(compiled.querySelector('button[type="submit"]')).toBeTruthy();
    });
  
    it('should disable submit button when form is invalid', () => {
      component.form.patchValue({
        email: '', // Missing required field
        password: ''
      });
  
      fixture.detectChanges();
  
      const submitButton = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
      expect(submitButton.disabled).toBe(true);
    });
  
    it('should toggle password visibility when visibility button is clicked', () => {
      const passwordInput = compiled.querySelector('input[formControlName="password"]') as HTMLInputElement;
      const toggleButton = compiled.querySelector('button[mat-icon-button]') as HTMLButtonElement;
      
      expect(passwordInput.type).toBe('password');
  
      fixture.ngZone?.run(() => {
        toggleButton.click();
      });
  
      fixture.detectChanges();
      expect(passwordInput.type).toBe('text');
    });
  
    it('should display error message when onError is true', () => {
      component.onError = true;
      fixture.detectChanges();
  
      const errorMessage = compiled.querySelector('p.error');
      expect(errorMessage).toBeTruthy();
      expect(errorMessage?.textContent?.trim()).toBe('An error occurred');
    });
  });
  
});
