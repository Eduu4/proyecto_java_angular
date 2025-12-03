import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PresupuestoDetailComponent } from './presupuesto-detail.component';

describe('Presupuesto Management Detail Component', () => {
  let comp: PresupuestoDetailComponent;
  let fixture: ComponentFixture<PresupuestoDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PresupuestoDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./presupuesto-detail.component').then(m => m.PresupuestoDetailComponent),
              resolve: { presupuesto: () => of({ id: 6207 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PresupuestoDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PresupuestoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load presupuesto on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PresupuestoDetailComponent);

      // THEN
      expect(instance.presupuesto()).toEqual(expect.objectContaining({ id: 6207 }));
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
