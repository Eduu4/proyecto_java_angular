import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CuentaDetailComponent } from './cuenta-detail.component';

describe('Cuenta Management Detail Component', () => {
  let comp: CuentaDetailComponent;
  let fixture: ComponentFixture<CuentaDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CuentaDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./cuenta-detail.component').then(m => m.CuentaDetailComponent),
              resolve: { cuenta: () => of({ id: 32472 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CuentaDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CuentaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load cuenta on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CuentaDetailComponent);

      // THEN
      expect(instance.cuenta()).toEqual(expect.objectContaining({ id: 32472 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
