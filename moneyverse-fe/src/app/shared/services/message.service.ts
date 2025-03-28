import {Injectable, signal} from '@angular/core';
import {ToastMessage} from '../components/toast/toast.component';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private readonly message$ = signal<ToastMessage | null>(null);
  message = this.message$.asReadonly();

  showMessage(message: ToastMessage) {
    this.message$.set(message);
  }

  clearMessage() {
    this.message$.set(null);
  }

}
