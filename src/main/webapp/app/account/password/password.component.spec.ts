// Note: use a local mock provider for AccountService instead of jest.mock

import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync, resolveComponentResources } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';

import PasswordComponent from './password.component';
import { PasswordService } from './password.service';

describe('PasswordComponent', () => {
  let comp: PasswordComponent;
  let fixture: ComponentFixture<PasswordComponent>;
  let service: PasswordService;
  let TestPasswordComponent: any;

  beforeEach(waitForAsync(() => {
    // Override the external template of the real PasswordComponent so tests
    // don't need to resolve templateUrl/styleUrls. Then import the real
    // standalone component into the TestBed.
    TestBed.overrideComponent(PasswordComponent as any, { set: { template: '' } });

    TestBed.configureTestingModule({
      imports: [PasswordComponent],
      providers: [FormBuilder, { provide: AccountService, useValue: {} }, provideHttpClient()],
    }).compileComponents();
  }));

  beforeEach(() => {
    // Create the test host component (subclass) instance which inherits the
    // behavior of PasswordComponent but uses an inline template.
    const testFixture = TestBed.createComponent(TestPasswordComponent as any);
    fixture = testFixture as ComponentFixture<PasswordComponent>;
    comp = fixture.componentInstance as PasswordComponent;
    service = TestBed.inject(PasswordService);
  });

  it('should show error if passwords do not match', () => {
    // GIVEN
    comp.passwordForm.patchValue({
      newPassword: 'password1',
      confirmPassword: 'password2',
    });
    // WHEN
    comp.changePassword();
    // THEN
    expect(comp.doNotMatch()).toBe(true);
    expect(comp.error()).toBe(false);
    expect(comp.success()).toBe(false);
  });

  it('should call Auth.changePassword when passwords match', () => {
    // GIVEN
    const passwordValues = {
      currentPassword: 'oldPassword',
      newPassword: 'myPassword',
    };

    jest.spyOn(service, 'save').mockReturnValue(of(new HttpResponse({ body: true })));

    comp.passwordForm.patchValue({
      currentPassword: passwordValues.currentPassword,
      newPassword: passwordValues.newPassword,
      confirmPassword: passwordValues.newPassword,
    });

    // WHEN
    comp.changePassword();

    // THEN
    expect(service.save).toHaveBeenCalledWith(passwordValues.newPassword, passwordValues.currentPassword);
  });

  it('should set success to true upon success', () => {
    // GIVEN
    jest.spyOn(service, 'save').mockReturnValue(of(new HttpResponse({ body: true })));
    comp.passwordForm.patchValue({
      newPassword: 'myPassword',
      confirmPassword: 'myPassword',
    });

    // WHEN
    comp.changePassword();

    // THEN
    expect(comp.doNotMatch()).toBe(false);
    expect(comp.error()).toBe(false);
    expect(comp.success()).toBe(true);
  });

  it('should notify of error if change password fails', () => {
    // GIVEN
    jest.spyOn(service, 'save').mockReturnValue(throwError(Error));
    comp.passwordForm.patchValue({
      newPassword: 'myPassword',
      confirmPassword: 'myPassword',
    });

    // WHEN
    comp.changePassword();

    // THEN
    expect(comp.doNotMatch()).toBe(false);
    expect(comp.success()).toBe(false);
    expect(comp.error()).toBe(true);
  });
});
