import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ICategoria } from 'app/entities/categoria/categoria.model';
import { CategoriaService } from 'app/entities/categoria/service/categoria.service';
import { ICuenta } from 'app/entities/cuenta/cuenta.model';
import { CuentaService } from 'app/entities/cuenta/service/cuenta.service';
import { TipoMovimiento } from 'app/entities/enumerations/tipo-movimiento.model';
import { MovimientoService } from '../service/movimiento.service';
import { IMovimiento } from '../movimiento.model';
import { MovimientoFormGroup, MovimientoFormService } from './movimiento-form.service';

@Component({
  selector: 'jhi-movimiento-update',
  templateUrl: './movimiento-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MovimientoUpdateComponent implements OnInit {
  isSaving = false;
  movimiento: IMovimiento | null = null;
  tipoMovimientoValues = Object.keys(TipoMovimiento);

  usersSharedCollection: IUser[] = [];
  categoriasSharedCollection: ICategoria[] = [];
  cuentasSharedCollection: ICuenta[] = [];

  protected movimientoService = inject(MovimientoService);
  protected movimientoFormService = inject(MovimientoFormService);
  protected userService = inject(UserService);
  protected categoriaService = inject(CategoriaService);
  protected cuentaService = inject(CuentaService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MovimientoFormGroup = this.movimientoFormService.createMovimientoFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCategoria = (o1: ICategoria | null, o2: ICategoria | null): boolean => this.categoriaService.compareCategoria(o1, o2);

  compareCuenta = (o1: ICuenta | null, o2: ICuenta | null): boolean => this.cuentaService.compareCuenta(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ movimiento }) => {
      this.movimiento = movimiento;
      if (movimiento) {
        this.updateForm(movimiento);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const movimiento = this.movimientoFormService.getMovimiento(this.editForm);
    if (movimiento.id !== null) {
      this.subscribeToSaveResponse(this.movimientoService.update(movimiento));
    } else {
      this.subscribeToSaveResponse(this.movimientoService.create(movimiento));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMovimiento>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(movimiento: IMovimiento): void {
    this.movimiento = movimiento;
    this.movimientoFormService.resetForm(this.editForm, movimiento);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, movimiento.usuario);
    this.categoriasSharedCollection = this.categoriaService.addCategoriaToCollectionIfMissing<ICategoria>(
      this.categoriasSharedCollection,
      movimiento.categoria,
    );
    this.cuentasSharedCollection = this.cuentaService.addCuentaToCollectionIfMissing<ICuenta>(
      this.cuentasSharedCollection,
      movimiento.cuenta,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.movimiento?.usuario)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.categoriaService
      .query()
      .pipe(map((res: HttpResponse<ICategoria[]>) => res.body ?? []))
      .pipe(
        map((categorias: ICategoria[]) =>
          this.categoriaService.addCategoriaToCollectionIfMissing<ICategoria>(categorias, this.movimiento?.categoria),
        ),
      )
      .subscribe((categorias: ICategoria[]) => (this.categoriasSharedCollection = categorias));

    this.cuentaService
      .query()
      .pipe(map((res: HttpResponse<ICuenta[]>) => res.body ?? []))
      .pipe(map((cuentas: ICuenta[]) => this.cuentaService.addCuentaToCollectionIfMissing<ICuenta>(cuentas, this.movimiento?.cuenta)))
      .subscribe((cuentas: ICuenta[]) => (this.cuentasSharedCollection = cuentas));
  }
}
