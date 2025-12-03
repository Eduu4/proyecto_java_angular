import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IPresupuesto } from '../presupuesto.model';

@Component({
  selector: 'jhi-presupuesto-detail',
  templateUrl: './presupuesto-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class PresupuestoDetailComponent {
  presupuesto = input<IPresupuesto | null>(null);

  previousState(): void {
    window.history.back();
  }
}
