import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../movimiento.test-samples';

import { MovimientoFormService } from './movimiento-form.service';

describe('Movimiento Form Service', () => {
  let service: MovimientoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MovimientoFormService);
  });

  describe('Service methods', () => {
    describe('createMovimientoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMovimientoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipo: expect.any(Object),
            monto: expect.any(Object),
            fechaMovimiento: expect.any(Object),
            fechaRegistro: expect.any(Object),
            fechaActualizacion: expect.any(Object),
            descripcion: expect.any(Object),
            usuario: expect.any(Object),
            categoria: expect.any(Object),
            cuenta: expect.any(Object),
          }),
        );
      });

      it('passing IMovimiento should create a new form with FormGroup', () => {
        const formGroup = service.createMovimientoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipo: expect.any(Object),
            monto: expect.any(Object),
            fechaMovimiento: expect.any(Object),
            fechaRegistro: expect.any(Object),
            fechaActualizacion: expect.any(Object),
            descripcion: expect.any(Object),
            usuario: expect.any(Object),
            categoria: expect.any(Object),
            cuenta: expect.any(Object),
          }),
        );
      });
    });

    describe('getMovimiento', () => {
      it('should return NewMovimiento for default Movimiento initial value', () => {
        const formGroup = service.createMovimientoFormGroup(sampleWithNewData);

        const movimiento = service.getMovimiento(formGroup) as any;

        expect(movimiento).toMatchObject(sampleWithNewData);
      });

      it('should return NewMovimiento for empty Movimiento initial value', () => {
        const formGroup = service.createMovimientoFormGroup();

        const movimiento = service.getMovimiento(formGroup) as any;

        expect(movimiento).toMatchObject({});
      });

      it('should return IMovimiento', () => {
        const formGroup = service.createMovimientoFormGroup(sampleWithRequiredData);

        const movimiento = service.getMovimiento(formGroup) as any;

        expect(movimiento).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMovimiento should not enable id FormControl', () => {
        const formGroup = service.createMovimientoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMovimiento should disable id FormControl', () => {
        const formGroup = service.createMovimientoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
