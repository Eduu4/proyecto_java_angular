import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MovimientoDetailComponent } from './movimiento-detail.component';

describe('Movimiento Management Detail Component', () => {
  let comp: MovimientoDetailComponent;
  let fixture: ComponentFixture<MovimientoDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovimientoDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./movimiento-detail.component').then(m => m.MovimientoDetailComponent),
              resolve: { movimiento: () => of({ id: 30126 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MovimientoDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MovimientoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load movimiento on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MovimientoDetailComponent);

      // THEN
      expect(instance.movimiento()).toEqual(expect.objectContaining({ id: 30126 }));
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
