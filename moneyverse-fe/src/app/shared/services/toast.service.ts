import {Injectable} from '@angular/core';
import {MessageService} from 'primeng/api';

export type ToastSeverity = 'success' | 'info' | 'warn' | 'error';

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  constructor(private readonly messageService: MessageService) {
  }

  success(message: string) {
    this.showToast('success', message);
  }

  info(message: string) {
    this.showToast('info', message);
  }

  warn(message: string) {
    this.showToast('warn', message);
    console.warn(message);
  }

  error(message: string) {
    this.showToast('error', message);
    console.error(message);
  }

  private showToast(severity: ToastSeverity, detail: string) {
    this.messageService.add({severity, detail});
  }
}
