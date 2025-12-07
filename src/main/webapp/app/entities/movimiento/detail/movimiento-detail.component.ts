import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IMovimiento } from '../movimiento.model';

@Component({
  selector: 'jhi-movimiento-detail',
  templateUrl: './movimiento-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class MovimientoDetailComponent {
  movimiento = input<IMovimiento | null>(null);

  previousState(): void {
    window.history.back();
  }
}
