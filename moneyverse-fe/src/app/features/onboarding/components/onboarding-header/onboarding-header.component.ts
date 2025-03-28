import {Component, inject} from '@angular/core';
import {LogOutIcon, LucideAngularModule, MoonIcon, SunIcon} from "lucide-angular";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SvgIconComponent} from "angular-svg-icon";
import {AuthService} from '../../../../core/auth/auth.service';
import {ThemeService} from '../../../../shared/services/theme.service';

@Component({
  selector: 'app-onboarding-header',
  imports: [
    LucideAngularModule,
    ReactiveFormsModule,
    SvgIconComponent,
    FormsModule
  ],
  templateUrl: './onboarding-header.component.html'
})
export class OnboardingHeaderComponent {

  protected readonly LogOutIcon = LogOutIcon;

  protected readonly authService = inject(AuthService);
  protected readonly themeService = inject(ThemeService);

  protected readonly SunIcon = SunIcon;
  protected readonly MoonIcon = MoonIcon;
}
