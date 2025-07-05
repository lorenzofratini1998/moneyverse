import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ToastEnum} from '../../shared/components/toast/toast.component';
import {MessageService} from '../../shared/services/message.service';

@Component({
  selector: 'app-overview',
  imports: [
    FormsModule
  ],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss'
})
export class OverviewComponent {

  private readonly messageService = inject(MessageService);
  date: any;

  showMessage(): void {
    this.messageService.showMessage({
      type: ToastEnum.SUCCESS,
      message: 'Profile deleted successfully'
    });
  }
}
