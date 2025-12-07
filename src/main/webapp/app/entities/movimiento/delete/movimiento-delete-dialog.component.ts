import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMovimiento } from '../movimiento.model';
import { MovimientoService } from '../service/movimiento.service';

@Component({
  templateUrl: './movimiento-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MovimientoDeleteDialogComponent {
  movimiento?: IMovimiento;

  protected movimientoService = inject(MovimientoService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.movimientoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
