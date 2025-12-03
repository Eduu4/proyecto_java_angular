import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMovimiento, NewMovimiento } from '../movimiento.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMovimiento for edit and NewMovimientoFormGroupInput for create.
 */
type MovimientoFormGroupInput = IMovimiento | PartialWithRequiredKeyOf<NewMovimiento>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMovimiento | NewMovimiento> = Omit<T, 'fechaMovimiento' | 'fechaRegistro' | 'fechaActualizacion'> & {
  fechaMovimiento?: string | null;
  fechaRegistro?: string | null;
  fechaActualizacion?: string | null;
};

type MovimientoFormRawValue = FormValueOf<IMovimiento>;

type NewMovimientoFormRawValue = FormValueOf<NewMovimiento>;

type MovimientoFormDefaults = Pick<NewMovimiento, 'id' | 'fechaMovimiento' | 'fechaRegistro' | 'fechaActualizacion'>;

type MovimientoFormGroupContent = {
  id: FormControl<MovimientoFormRawValue['id'] | NewMovimiento['id']>;
  tipo: FormControl<MovimientoFormRawValue['tipo']>;
  monto: FormControl<MovimientoFormRawValue['monto']>;
  fechaMovimiento: FormControl<MovimientoFormRawValue['fechaMovimiento']>;
  fechaRegistro: FormControl<MovimientoFormRawValue['fechaRegistro']>;
  fechaActualizacion: FormControl<MovimientoFormRawValue['fechaActualizacion']>;
  descripcion: FormControl<MovimientoFormRawValue['descripcion']>;
  usuario: FormControl<MovimientoFormRawValue['usuario']>;
  categoria: FormControl<MovimientoFormRawValue['categoria']>;
  cuenta: FormControl<MovimientoFormRawValue['cuenta']>;
};

export type MovimientoFormGroup = FormGroup<MovimientoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MovimientoFormService {
  createMovimientoFormGroup(movimiento: MovimientoFormGroupInput = { id: null }): MovimientoFormGroup {
    const movimientoRawValue = this.convertMovimientoToMovimientoRawValue({
      ...this.getFormDefaults(),
      ...movimiento,
    });
    return new FormGroup<MovimientoFormGroupContent>({
      id: new FormControl(
        { value: movimientoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tipo: new FormControl(movimientoRawValue.tipo, {
        validators: [Validators.required],
      }),
      monto: new FormControl(movimientoRawValue.monto, {
        validators: [Validators.required],
      }),
      fechaMovimiento: new FormControl(movimientoRawValue.fechaMovimiento, {
        validators: [Validators.required],
      }),
      fechaRegistro: new FormControl(movimientoRawValue.fechaRegistro, {
        validators: [Validators.required],
      }),
      fechaActualizacion: new FormControl(movimientoRawValue.fechaActualizacion),
      descripcion: new FormControl(movimientoRawValue.descripcion),
      usuario: new FormControl(movimientoRawValue.usuario),
      categoria: new FormControl(movimientoRawValue.categoria),
      cuenta: new FormControl(movimientoRawValue.cuenta),
    });
  }

  getMovimiento(form: MovimientoFormGroup): IMovimiento | NewMovimiento {
    return this.convertMovimientoRawValueToMovimiento(form.getRawValue() as MovimientoFormRawValue | NewMovimientoFormRawValue);
  }

  resetForm(form: MovimientoFormGroup, movimiento: MovimientoFormGroupInput): void {
    const movimientoRawValue = this.convertMovimientoToMovimientoRawValue({ ...this.getFormDefaults(), ...movimiento });
    form.reset(
      {
        ...movimientoRawValue,
        id: { value: movimientoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MovimientoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaMovimiento: currentTime,
      fechaRegistro: currentTime,
      fechaActualizacion: currentTime,
    };
  }

  private convertMovimientoRawValueToMovimiento(
    rawMovimiento: MovimientoFormRawValue | NewMovimientoFormRawValue,
  ): IMovimiento | NewMovimiento {
    return {
      ...rawMovimiento,
      fechaMovimiento: dayjs(rawMovimiento.fechaMovimiento, DATE_TIME_FORMAT),
      fechaRegistro: dayjs(rawMovimiento.fechaRegistro, DATE_TIME_FORMAT),
      fechaActualizacion: dayjs(rawMovimiento.fechaActualizacion, DATE_TIME_FORMAT),
    };
  }

  private convertMovimientoToMovimientoRawValue(
    movimiento: IMovimiento | (Partial<NewMovimiento> & MovimientoFormDefaults),
  ): MovimientoFormRawValue | PartialWithRequiredKeyOf<NewMovimientoFormRawValue> {
    return {
      ...movimiento,
      fechaMovimiento: movimiento.fechaMovimiento ? movimiento.fechaMovimiento.format(DATE_TIME_FORMAT) : undefined,
      fechaRegistro: movimiento.fechaRegistro ? movimiento.fechaRegistro.format(DATE_TIME_FORMAT) : undefined,
      fechaActualizacion: movimiento.fechaActualizacion ? movimiento.fechaActualizacion.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
