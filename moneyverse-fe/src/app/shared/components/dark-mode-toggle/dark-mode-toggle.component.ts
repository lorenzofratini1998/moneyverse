import {Component, inject} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SvgComponent} from "../svg/svg.component";
import {ThemeService} from '../../services/theme.service';
import {IconsEnum} from "../../models/icons.model";

@Component({
  selector: 'app-dark-mode-toggle',
  imports: [
    ReactiveFormsModule,
    SvgComponent,
    FormsModule
  ],
  template: `
    <label class="flex cursor-pointer gap-2 mx-2">
      <app-svg [name]="Icons.SUN"/>
      <input type="checkbox" class="toggle theme-controller"
             (change)="themeService.toggleTheme()"/>
      <app-svg [name]="Icons.MOON"/>
    </label>
  `
})
export class DarkModeToggleComponent {

  protected readonly themeService = inject(ThemeService);
  protected readonly Icons = IconsEnum;
}
