import {inject, Injectable} from '@angular/core';
import {Confirmation, ConfirmationService} from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class AppConfirmationService {

  private readonly confirmationService = inject(ConfirmationService);

  confirmDelete(confirmation: Confirmation) {
    this.confirmationService.confirm({
      message: confirmation.message,
      header: confirmation.header,
      acceptLabel: 'app.dialog.delete',
      rejectLabel: 'app.dialog.cancel',
      accept: confirmation.accept,
      reject: confirmation.reject,
    })
  }

}
