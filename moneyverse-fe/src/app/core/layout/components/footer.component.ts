import {Component} from '@angular/core';
import {SvgComponent} from '../../../shared/components/svg/svg.component';

@Component({
  selector: 'app-footer',
  imports: [
    SvgComponent,
  ],
  template: `
    <div class="layout-footer">
      <app-svg src="/assets/images/moneyverse-logo.svg"/>
      <p>Copyright © {{ year }} - All right reserved</p>
    </div>
  `
})
export class FooterComponent {

  year = new Date().getFullYear();

}
