import {Component, inject, signal, ViewChild} from '@angular/core';
import {AuthService} from '../../../../core/auth/auth.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UserService} from '../../../../shared/services/user.service';
import {MessageService} from '../../../../shared/services/message.service';
import {ToastComponent, ToastEnum} from '../../../../shared/components/toast/toast.component';
import {ProfileFormComponent} from './components/profile-form/profile-form.component';
import {UserUpdateRequestDto} from '../../../../core/auth/models/user.model';
import {ConfirmDialogComponent} from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {LucideAngularModule, Trash2Icon} from 'lucide-angular';

@Component({
  selector: 'app-profile',
  imports: [
    FormsModule,
    ToastComponent,
    ReactiveFormsModule,
    ProfileFormComponent,
    ConfirmDialogComponent,
    LucideAngularModule
  ],
  templateUrl: './profile.component.html'
})
export class ProfileComponent {
  private readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly messageService = inject(MessageService);
  @ViewChild('confirmDialog') confirmDialog!: ConfirmDialogComponent;

  user = signal(this.authService.getAuthenticatedUser());

  submit(form: any) {
    const request: Partial<UserUpdateRequestDto> = {};
    if (this.user().firstName.trim() !== form.firstName.trim()) {
      request.firstName = form.firstName.trim();
    }
    if (this.user().lastName.trim() !== form.lastName.trim()) {
      request.lastName = form.lastName.trim();
    }
    if (this.user().email.trim() !== form.email.trim()) {
      request.email = form.email.trim();
    }
    if (Object.keys(request).length === 0) {
      return;
    }
    this.userService.updateUser(this.user().userId, request).subscribe({
      next: () => {
        window.location.reload();
      },
      error: () => this.messageService.showMessage({
        type: ToastEnum.ERROR,
        message: 'Error saving preferences'
      })
    })
  }

  openConfirmDialog(): void {
    this.confirmDialog.show();
    this.confirmDialog.confirm.subscribe(result => {
      if (result) {
        this.deleteProfile();
      }
    });
  }

  deleteProfile() {
    this.userService.deleteUser(this.user().userId).subscribe({
      next: () => {
        void this.authService.logout();
        sessionStorage.clear();
        localStorage.clear();
      },
      error: () => this.messageService.showMessage({
        type: ToastEnum.ERROR,
        message: 'Error deleting profile'
      })
    })
  }

  protected readonly Trash2Icon = Trash2Icon;
}
