import {Component, effect, inject} from '@angular/core';
import {MessageService} from '../../services/message.service';
import {NgClass} from '@angular/common';
import {CircleCheckIcon, CircleXIcon, InfoIcon, LucideAngularModule, TriangleAlertIcon, XIcon} from 'lucide-angular';

export enum ToastEnum {
  SUCCESS = 'success',
  ERROR = 'error',
  INFO = 'info',
  WARNING = 'warning'
}

export interface ToastMessage {
  type: ToastEnum;
  message: string;
  duration?: number;
}

@Component({
  selector: 'app-toast',
  imports: [
    NgClass,
    LucideAngularModule
  ],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.scss'
})
export class ToastComponent {

  private readonly messageService = inject(MessageService);
  message = this.messageService.message;
  private timeout: any = null;

  constructor() {
    effect(() => {
      if (this.message()) {
        if (this.timeout) {
          clearTimeout(this.timeout);
        }
        this.timeout = setTimeout(() => {
          this.messageService.clearMessage();
        }, 2000);
      }
    });
  }

  getToastClass(toast: ToastMessage): string {
    switch (toast.type) {
      case 'success':
        return 'alert-success';
      case 'error':
        return 'alert-error';
      case 'info':
        return 'alert-info';
      case 'warning':
        return 'alert-warning';
      default:
        return 'alert-info';
    }
  }

  closeMessage() {
    if (this.timeout) {
      clearTimeout(this.timeout);
    }
    this.messageService.clearMessage();
  }

  protected readonly CircleCheckIcon = CircleCheckIcon;
  protected readonly CircleXIcon = CircleXIcon;
  protected readonly TriangleAlertIcon = TriangleAlertIcon;
  protected readonly InfoIcon = InfoIcon;
  protected readonly XIcon = XIcon;
}
