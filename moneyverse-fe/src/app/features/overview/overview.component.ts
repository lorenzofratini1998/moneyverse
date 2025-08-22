import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Button} from 'primeng/button';
import {ToastService} from '../../shared/services/toast.service';
import {LogoIconComponent} from '../../shared/components/icons/logo-icon.component';
import {ItFlagIconComponent} from '../../shared/components/icons/flags/it-flag-icon.component';

@Component({
  selector: 'app-overview',
  imports: [
    FormsModule,
    Button,
    LogoIconComponent,
    ItFlagIconComponent
  ],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss'
})
export class OverviewComponent {

  private readonly toastService = inject(ToastService);
  date: any;

  showMessage(): void {
    this.toastService.error('Hello World!');
  }
}
