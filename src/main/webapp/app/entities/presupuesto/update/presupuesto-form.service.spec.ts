import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../presupuesto.test-samples';

import { PresupuestoFormService } from './presupuesto-form.service';

describe('Presupuesto Form Service', () => {
  let service: PresupuestoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PresupuestoFormService);
  });

  describe('Service methods', () => {
    describe('createPresupuestoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPresupuestoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            monto: expect.any(Object),
            periodo: expect.any(Object),
            fechaInicio: expect.any(Object),
            fechaFin: expect.any(Object),
            usuario: expect.any(Object),
            categoria: expect.any(Object),
          }),
        );
      });

      it('passing IPresupuesto should create a new form with FormGroup', () => {
        const formGroup = service.createPresupuestoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            monto: expect.any(Object),
            periodo: expect.any(Object),
            fechaInicio: expect.any(Object),
            fechaFin: expect.any(Object),
            usuario: expect.any(Object),
            categoria: expect.any(Object),
          }),
        );
      });
    });

    describe('getPresupuesto', () => {
      it('should return NewPresupuesto for default Presupuesto initial value', () => {
        const formGroup = service.createPresupuestoFormGroup(sampleWithNewData);

        const presupuesto = service.getPresupuesto(formGroup) as any;

        expect(presupuesto).toMatchObject(sampleWithNewData);
      });

      it('should return NewPresupuesto for empty Presupuesto initial value', () => {
        const formGroup = service.createPresupuestoFormGroup();

        const presupuesto = service.getPresupuesto(formGroup) as any;

        expect(presupuesto).toMatchObject({});
      });

      it('should return IPresupuesto', () => {
        const formGroup = service.createPresupuestoFormGroup(sampleWithRequiredData);

        const presupuesto = service.getPresupuesto(formGroup) as any;

        expect(presupuesto).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPresupuesto should not enable id FormControl', () => {
        const formGroup = service.createPresupuestoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPresupuesto should disable id FormControl', () => {
        const formGroup = service.createPresupuestoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
