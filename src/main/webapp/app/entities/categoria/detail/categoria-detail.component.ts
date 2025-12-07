import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ICategoria } from '../categoria.model';

@Component({
  selector: 'jhi-categoria-detail',
  templateUrl: './categoria-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class CategoriaDetailComponent {
  categoria = input<ICategoria | null>(null);

  previousState(): void {
    window.history.back();
  }
}
