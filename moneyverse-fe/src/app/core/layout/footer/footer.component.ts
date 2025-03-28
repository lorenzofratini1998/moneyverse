import {Component} from '@angular/core';
import {SvgIconComponent} from 'angular-svg-icon';

@Component({
  selector: 'app-footer',
  imports: [
    SvgIconComponent
  ],
  templateUrl: './footer.component.html',
})
export class FooterComponent {

  year = new Date().getFullYear();

}
