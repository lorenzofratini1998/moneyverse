import {Component} from '@angular/core';
import {SvgComponent} from '../../../shared/components/svg/svg.component';

@Component({
  selector: 'app-footer',
  imports: [
    SvgComponent
  ],
  templateUrl: './footer.component.html',
})
export class FooterComponent {

  year = new Date().getFullYear();

}
