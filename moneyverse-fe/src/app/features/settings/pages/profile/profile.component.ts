import {Component, inject, signal} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {AuthService} from '../../../../core/auth/auth.service';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {ProfileFormComponent, ProfileFormData} from './components/profile-form/profile-form.component';
import {Card} from 'primeng/card';
import {Button} from 'primeng/button';
import {SvgComponent} from '../../../../shared/components/svg/svg.component';
import {UserService} from '../../../../shared/services/user.service';
import {ToastService} from '../../../../shared/services/toast.service';
import {Budget} from '../../../category/category.model';
import {AppConfirmationService} from '../../../../shared/services/confirmation.service';
import {TranslationService} from '../../../../shared/services/translation.service';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-profile',
  imports: [
    ReactiveFormsModule,
    ProfileFormComponent,
    Card,
    Button,
    SvgComponent,
    TranslatePipe
  ],
  templateUrl: './profile.component.html'
})
export class ProfileComponent {

  protected readonly icons = IconsEnum;
  protected readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly toastService = inject(ToastService);
  private readonly confirmationService = inject(AppConfirmationService);
  private readonly translateService = inject(TranslationService);

  protected updateProfile(formData: ProfileFormData) {
    this.userService.updateUser(this.authService.user().userId, formData).subscribe({
      next: async () => {
        await this.authService.refreshToken();
        this.toastService.success(this.translateService.translate('app.message.profile.update.success'))
      },
      error: () => this.toastService.error(this.translateService.translate('app.message.profile.update.error'))
    })
  }

  protected confirmDelete() {
    this.confirmationService.confirmDelete({
      header: this.translateService.translate('app.dialog.profile.delete'),
      message: this.translateService.translate('app.dialog.profile.confirmDelete'),
      accept: () => this.deleteProfile(),
    })
  }

  private deleteProfile() {
    this.userService.deleteUser(this.authService.user().userId).subscribe({
      next: () => {
        sessionStorage.clear();
        localStorage.clear();
        void this.authService.logout();
      },
      error: () => this.toastService.error(this.translateService.translate('app.message.profile.delete.error'))
    })
  }
}
