import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICategoria, NewCategoria } from '../categoria.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICategoria for edit and NewCategoriaFormGroupInput for create.
 */
type CategoriaFormGroupInput = ICategoria | PartialWithRequiredKeyOf<NewCategoria>;

type CategoriaFormDefaults = Pick<NewCategoria, 'id'>;

type CategoriaFormGroupContent = {
  id: FormControl<ICategoria['id'] | NewCategoria['id']>;
  nombre: FormControl<ICategoria['nombre']>;
  tipo: FormControl<ICategoria['tipo']>;
  color: FormControl<ICategoria['color']>;
  usuario: FormControl<ICategoria['usuario']>;
};

export type CategoriaFormGroup = FormGroup<CategoriaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CategoriaFormService {
  createCategoriaFormGroup(categoria: CategoriaFormGroupInput = { id: null }): CategoriaFormGroup {
    const categoriaRawValue = {
      ...this.getFormDefaults(),
      ...categoria,
    };
    return new FormGroup<CategoriaFormGroupContent>({
      id: new FormControl(
        { value: categoriaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(categoriaRawValue.nombre, {
        validators: [Validators.required],
      }),
      tipo: new FormControl(categoriaRawValue.tipo, {
        validators: [Validators.required],
      }),
      color: new FormControl(categoriaRawValue.color),
      usuario: new FormControl(categoriaRawValue.usuario),
    });
  }

  getCategoria(form: CategoriaFormGroup): ICategoria | NewCategoria {
    return form.getRawValue() as ICategoria | NewCategoria;
  }

  resetForm(form: CategoriaFormGroup, categoria: CategoriaFormGroupInput): void {
    const categoriaRawValue = { ...this.getFormDefaults(), ...categoria };
    form.reset(
      {
        ...categoriaRawValue,
        id: { value: categoriaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CategoriaFormDefaults {
    return {
      id: null,
    };
  }
}
