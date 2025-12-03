import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPresupuesto, NewPresupuesto } from '../presupuesto.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPresupuesto for edit and NewPresupuestoFormGroupInput for create.
 */
type PresupuestoFormGroupInput = IPresupuesto | PartialWithRequiredKeyOf<NewPresupuesto>;

type PresupuestoFormDefaults = Pick<NewPresupuesto, 'id'>;

type PresupuestoFormGroupContent = {
  id: FormControl<IPresupuesto['id'] | NewPresupuesto['id']>;
  monto: FormControl<IPresupuesto['monto']>;
  periodo: FormControl<IPresupuesto['periodo']>;
  fechaInicio: FormControl<IPresupuesto['fechaInicio']>;
  fechaFin: FormControl<IPresupuesto['fechaFin']>;
  usuario: FormControl<IPresupuesto['usuario']>;
  categoria: FormControl<IPresupuesto['categoria']>;
};

export type PresupuestoFormGroup = FormGroup<PresupuestoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PresupuestoFormService {
  createPresupuestoFormGroup(presupuesto: PresupuestoFormGroupInput = { id: null }): PresupuestoFormGroup {
    const presupuestoRawValue = {
      ...this.getFormDefaults(),
      ...presupuesto,
    };
    return new FormGroup<PresupuestoFormGroupContent>({
      id: new FormControl(
        { value: presupuestoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      monto: new FormControl(presupuestoRawValue.monto, {
        validators: [Validators.required],
      }),
      periodo: new FormControl(presupuestoRawValue.periodo, {
        validators: [Validators.required],
      }),
      fechaInicio: new FormControl(presupuestoRawValue.fechaInicio, {
        validators: [Validators.required],
      }),
      fechaFin: new FormControl(presupuestoRawValue.fechaFin, {
        validators: [Validators.required],
      }),
      usuario: new FormControl(presupuestoRawValue.usuario),
      categoria: new FormControl(presupuestoRawValue.categoria),
    });
  }

  getPresupuesto(form: PresupuestoFormGroup): IPresupuesto | NewPresupuesto {
    return form.getRawValue() as IPresupuesto | NewPresupuesto;
  }

  resetForm(form: PresupuestoFormGroup, presupuesto: PresupuestoFormGroupInput): void {
    const presupuestoRawValue = { ...this.getFormDefaults(), ...presupuesto };
    form.reset(
      {
        ...presupuestoRawValue,
        id: { value: presupuestoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PresupuestoFormDefaults {
    return {
      id: null,
    };
  }
}
