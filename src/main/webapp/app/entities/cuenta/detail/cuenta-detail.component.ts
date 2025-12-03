import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ICuenta } from '../cuenta.model';

@Component({
  selector: 'jhi-cuenta-detail',
  templateUrl: './cuenta-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class CuentaDetailComponent {
  cuenta = input<ICuenta | null>(null);

  previousState(): void {
    window.history.back();
  }
}
