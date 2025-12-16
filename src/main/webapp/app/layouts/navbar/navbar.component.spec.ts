jest.mock('app/core/auth/account.service');

import { TestBed, waitForAsync } from '@angular/core/testing';
import { signal } from '@angular/core';

import NavbarComponent from './navbar.component';
import { AccountService } from 'app/core/auth/account.service';

describe('NavbarComponent', () => {
  let comp: NavbarComponent;

  beforeEach(waitForAsync(() => {
    const mockAccountService = {
      trackCurrentAccount: jest.fn(() => signal(null)),
    } as Partial<AccountService> as AccountService;

    TestBed.configureTestingModule({ imports: [NavbarComponent], providers: [{ provide: AccountService, useValue: mockAccountService }] })
      .overrideTemplate(
        NavbarComponent,
        `<a class="nav-link" *ngIf="account() !== null" routerLink="/proceso-principal">Gestión de Movimientos</a>`,
      ) // small test template
      .compileComponents();
  }));

  beforeEach(() => {
    const fixture = TestBed.createComponent(NavbarComponent);
    comp = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should not show proceso-principal link when not authenticated', () => {
    const el = document.querySelector('a[routerlink="/proceso-principal"], a[routerLink="/proceso-principal"]');
    expect(el).toBeNull();
  });
});
jest.mock('app/login/login.service');

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';

import { ProfileInfo } from 'app/layouts/profiles/profile-info.model';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { LoginService } from 'app/login/login.service';

import NavbarComponent from './navbar.component';

describe('Navbar Component', () => {
  let comp: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let accountService: AccountService;
  let profileService: ProfileService;
  const account: Account = {
    activated: true,
    authorities: [],
    email: '',
    firstName: 'John',
    langKey: '',
    lastName: 'Doe',
    login: 'john.doe',
    imageUrl: '',
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [NavbarComponent, TranslateModule.forRoot()],
      providers: [provideHttpClient(), provideHttpClientTesting(), LoginService],
    })
      .overrideTemplate(NavbarComponent, '')
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarComponent);
    comp = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    profileService = TestBed.inject(ProfileService);
  });

  it('should call profileService.getProfileInfo on init', () => {
    // GIVEN
    jest.spyOn(profileService, 'getProfileInfo').mockReturnValue(of(new ProfileInfo()));

    // WHEN
    comp.ngOnInit();

    // THEN
    expect(profileService.getProfileInfo).toHaveBeenCalled();
  });

  it('should hold current authenticated user in variable account', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(comp.account()).toBeNull();

    // WHEN
    accountService.authenticate(account);

    // THEN
    expect(comp.account()).toEqual(account);

    // WHEN
    accountService.authenticate(null);

    // THEN
    expect(comp.account()).toBeNull();
  });

  it('should hold current authenticated user in variable account if user is authenticated before page load', () => {
    // GIVEN
    accountService.authenticate(account);

    // WHEN
    comp.ngOnInit();

    // THEN
    expect(comp.account()).toEqual(account);

    // WHEN
    accountService.authenticate(null);

    // THEN
    expect(comp.account()).toBeNull();
  });
});
