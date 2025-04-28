import {Component, inject} from '@angular/core';
import {LucideAngularModule} from "lucide-angular";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AuthService} from '../../../../core/auth/auth.service';
import {SvgComponent} from '../../../../shared/components/svg/svg.component';
import {DarkModeToggleComponent} from '../../../../shared/components/dark-mode-toggle/dark-mode-toggle.component';
import {IconsEnum} from "../../../../shared/models/icons.model";

@Component({
  selector: 'app-onboarding-header',
  imports: [
    LucideAngularModule,
    ReactiveFormsModule,
    FormsModule,
    SvgComponent,
    DarkModeToggleComponent
  ],
  templateUrl: './onboarding-header.component.html'
})
export class OnboardingHeaderComponent {

  protected readonly authService = inject(AuthService);
  protected readonly Icons = IconsEnum;
}
