import {Component} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {SvgIconComponent} from 'angular-svg-icon';

@Component({
  selector: 'app-settings',
  imports: [
    RouterOutlet,
    RouterLink,
    SvgIconComponent
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent {

  pageTitle: string = 'Settings';

}
