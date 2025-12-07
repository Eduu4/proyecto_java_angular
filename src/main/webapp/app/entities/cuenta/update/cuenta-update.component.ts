import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ICuenta } from '../cuenta.model';
import { CuentaService } from '../service/cuenta.service';
import { CuentaFormGroup, CuentaFormService } from './cuenta-form.service';

@Component({
  selector: 'jhi-cuenta-update',
  templateUrl: './cuenta-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CuentaUpdateComponent implements OnInit {
  isSaving = false;
  cuenta: ICuenta | null = null;

  usersSharedCollection: IUser[] = [];

  protected cuentaService = inject(CuentaService);
  protected cuentaFormService = inject(CuentaFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CuentaFormGroup = this.cuentaFormService.createCuentaFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cuenta }) => {
      this.cuenta = cuenta;
      if (cuenta) {
        this.updateForm(cuenta);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cuenta = this.cuentaFormService.getCuenta(this.editForm);
    if (cuenta.id !== null) {
      this.subscribeToSaveResponse(this.cuentaService.update(cuenta));
    } else {
      this.subscribeToSaveResponse(this.cuentaService.create(cuenta));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICuenta>>): void {
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

  protected updateForm(cuenta: ICuenta): void {
    this.cuenta = cuenta;
    this.cuentaFormService.resetForm(this.editForm, cuenta);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, cuenta.usuario);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.cuenta?.usuario)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
