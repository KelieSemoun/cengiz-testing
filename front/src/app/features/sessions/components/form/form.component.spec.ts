import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, fakeAsync, flush, TestBed, tick } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';

import { MatSnackBar } from '@angular/material/snack-bar';
import { Session } from '../../interfaces/session.interface';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;
  let sessionApiService: SessionApiService;
  let sessionService: SessionService;
  let activatedRoute: ActivatedRoute;
  let formBuilder: FormBuilder;
  let compiled: HTMLElement;

  const mockSessionService = {
    sessionInformation: { admin: true }
  };

  const mockSessionApiService = {
    detail: jest.fn().mockReturnValue(of({})),
    create: jest.fn().mockReturnValue(of({})),
    update: jest.fn().mockReturnValue(of({}))
  };

  const mockMatSnackBar = {
    open: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '123' } } } },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        FormBuilder // Needed for creating forms
      ],
      declarations: [FormComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    sessionApiService = TestBed.inject(SessionApiService);
    sessionService = TestBed.inject(SessionService);
    activatedRoute = TestBed.inject(ActivatedRoute);
    formBuilder = TestBed.inject(FormBuilder);

    jest.spyOn(component as any, 'initForm'); // Spy on private method
    jest.spyOn(component as any, 'exitPage'); // Spy on private method
    fixture.detectChanges();
    compiled = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit()', () => {
    it('should navigate to "/sessions" if the user is not an admin', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      (sessionService.sessionInformation as any).admin = false;

      fixture.ngZone?.run(() => {
        component.ngOnInit();
      });

      expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
    });

    it('should initialize the form normally if the URL does not contain "update"', () => {
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');

      fixture.ngZone?.run(() => {
        component.ngOnInit();
      });

      expect(component.onUpdate).toBe(false);
      expect((component as any).initForm).toHaveBeenCalled();
    });

    it('should set onUpdate to true, extract id, and fetch session details if URL contains "update"', () => {
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/123');

      fixture.ngZone?.run(() => {
        component.ngOnInit();
      });

      expect(component.onUpdate).toBe(true);
      expect(component['id']).toBe('123');
      expect(sessionApiService.detail).toHaveBeenCalledWith('123');
      expect((component as any).initForm).toHaveBeenCalled();
    });
  });

describe('submit()', () => {
  beforeEach(() => {
    Object.defineProperty(component, 'sessionForm', {
      get: jest.fn(() => formBuilder.group({
        name: ['Test Session', Validators.required],
        date: ['2025-01-01', Validators.required],
        teacher_id: [123, Validators.required],
        description: ['This is a test session.', [Validators.required, Validators.max(2000)]]
      }))
    });
  });

  it('should create a session when onUpdate is false', () => {
    component.onUpdate = false;

    fixture.ngZone?.run(() => {
      component.submit();
    });

    const session = component.sessionForm?.value;

    expect(session).toBeDefined();
    expect(sessionApiService.create).toHaveBeenCalledWith(session);
    expect((component as any).exitPage).toHaveBeenCalledWith('Session created !');
    expect(mockMatSnackBar.open).toHaveBeenCalled();
  });

  it('should update a session when onUpdate is true', () => {
    component.onUpdate = true;
    component['id'] = '123';

    fixture.ngZone?.run(() => {
      component.submit();
    });

    const session = component.sessionForm?.value;

    expect(session).toBeDefined();
    expect(sessionApiService.update).toHaveBeenCalledWith('123', session);
    expect((component as any).exitPage).toHaveBeenCalledWith('Session updated !');
    expect(mockMatSnackBar.open).toHaveBeenCalled();
  });
});


  describe('initForm()', () => {
    it('should initialize form with default values when no session is provided', () => {
      (component as any).initForm();
  
      expect(component.sessionForm?.value).toEqual({
        name: '',
        date: '',
        teacher_id: '',
        description: ''
      });
    });
  
    it('should initialize form with session values when a session is provided', () => {
      const session: Session = {
        id: 1,
        name: 'Test Session',
        description: 'Test description',
        date: new Date('2025-01-01'),
        teacher_id: 123,
        users: [1, 2, 3],
        createdAt: new Date(),
        updatedAt: new Date()
      };
  
      (component as any).initForm(session);
  
      expect(component.sessionForm?.value).toEqual({
        name: 'Test Session',
        date: '2025-01-01',
        teacher_id: 123, 
        description: 'Test description'
      });
    });
  }); 
  
  describe('Integration Tests', () => {
    it('should display "Create session" when onUpdate is false', () => {
      component.onUpdate = false;
      fixture.detectChanges();
  
      const h1 = compiled.querySelector('h1');
      expect(h1?.textContent?.trim()).toBe('Create session');
    });
  
    it('should display "Update session" when onUpdate is true', () => {
      component.onUpdate = true;
      fixture.detectChanges();
  
      const h1 = compiled.querySelector('h1');
      expect(h1?.textContent?.trim()).toBe('Update session');
    });

    it('should call submit() when the form is submitted', () => {
      jest.spyOn(component, 'submit');
    
      // Fill in the form
      component.sessionForm?.patchValue({
        name: 'New Session',
        date: '2025-01-01',
        teacher_id: 123,
        description: 'This is a test session'
      });
    
      fixture.detectChanges();
    
      // Click the submit button
      const submitButton = compiled.querySelector('button[type="submit"]') as HTMLElement;
      submitButton?.click();
    
      expect(component.submit).toHaveBeenCalled();
    });

    it('should disable submit button when form is invalid', () => {
      component.sessionForm?.patchValue({
        name: '', // Missing required field
        date: '',
        teacher_id: null,
        description: ''
      });
    
      fixture.detectChanges();
    
      const submitButton = compiled.querySelector('button[type="submit"]');
      expect(submitButton?.hasAttribute('disabled')).toBe(true);
    });
  });
});
