import {Component} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {SvgComponent} from '../../../../shared/components/svg/svg.component';

@Component({
  selector: 'app-settings',
  imports: [
    RouterOutlet,
    RouterLink,
    SvgComponent
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent {

}
