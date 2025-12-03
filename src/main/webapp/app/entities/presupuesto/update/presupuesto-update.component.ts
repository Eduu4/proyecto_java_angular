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
import { PeriodoPresupuesto } from 'app/entities/enumerations/periodo-presupuesto.model';
import { PresupuestoService } from '../service/presupuesto.service';
import { IPresupuesto } from '../presupuesto.model';
import { PresupuestoFormGroup, PresupuestoFormService } from './presupuesto-form.service';

@Component({
  selector: 'jhi-presupuesto-update',
  templateUrl: './presupuesto-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PresupuestoUpdateComponent implements OnInit {
  isSaving = false;
  presupuesto: IPresupuesto | null = null;
  periodoPresupuestoValues = Object.keys(PeriodoPresupuesto);

  usersSharedCollection: IUser[] = [];
  categoriasSharedCollection: ICategoria[] = [];

  protected presupuestoService = inject(PresupuestoService);
  protected presupuestoFormService = inject(PresupuestoFormService);
  protected userService = inject(UserService);
  protected categoriaService = inject(CategoriaService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PresupuestoFormGroup = this.presupuestoFormService.createPresupuestoFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCategoria = (o1: ICategoria | null, o2: ICategoria | null): boolean => this.categoriaService.compareCategoria(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ presupuesto }) => {
      this.presupuesto = presupuesto;
      if (presupuesto) {
        this.updateForm(presupuesto);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const presupuesto = this.presupuestoFormService.getPresupuesto(this.editForm);
    if (presupuesto.id !== null) {
      this.subscribeToSaveResponse(this.presupuestoService.update(presupuesto));
    } else {
      this.subscribeToSaveResponse(this.presupuestoService.create(presupuesto));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPresupuesto>>): void {
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

  protected updateForm(presupuesto: IPresupuesto): void {
    this.presupuesto = presupuesto;
    this.presupuestoFormService.resetForm(this.editForm, presupuesto);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, presupuesto.usuario);
    this.categoriasSharedCollection = this.categoriaService.addCategoriaToCollectionIfMissing<ICategoria>(
      this.categoriasSharedCollection,
      presupuesto.categoria,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.presupuesto?.usuario)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.categoriaService
      .query()
      .pipe(map((res: HttpResponse<ICategoria[]>) => res.body ?? []))
      .pipe(
        map((categorias: ICategoria[]) =>
          this.categoriaService.addCategoriaToCollectionIfMissing<ICategoria>(categorias, this.presupuesto?.categoria),
        ),
      )
      .subscribe((categorias: ICategoria[]) => (this.categoriasSharedCollection = categorias));
  }
}
