import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICuenta } from '../cuenta.model';
import { CuentaService } from '../service/cuenta.service';

@Component({
  templateUrl: './cuenta-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CuentaDeleteDialogComponent {
  cuenta?: ICuenta;

  protected cuentaService = inject(CuentaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cuentaService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
