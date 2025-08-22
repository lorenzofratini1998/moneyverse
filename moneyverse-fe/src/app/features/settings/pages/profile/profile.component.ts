import {Component, inject, signal} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {AuthService} from '../../../../core/auth/auth.service';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {ProfileFormComponent} from './components/profile-form/profile-form.component';
import {Card} from 'primeng/card';
import {Button} from 'primeng/button';
import {SvgComponent} from '../../../../shared/components/svg/svg.component';
import {UserService} from '../../../../shared/services/user.service';
import {ToastService} from '../../../../shared/services/toast.service';

@Component({
  selector: 'app-profile',
  imports: [
    ReactiveFormsModule,
    ProfileFormComponent,
    Card,
    Button,
    SvgComponent
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {

  protected readonly icons = IconsEnum;
  private readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly toastService = inject(ToastService);

  user = signal(this.authService.getAuthenticatedUser());

  deleteProfile() {
    this.userService.deleteUser(this.user().userId).subscribe({
      next: () => {
        sessionStorage.clear();
        localStorage.clear();
        void this.authService.logout();
      },
      error: () => this.toastService.error('Error deleting profile')
    })
  }
}
