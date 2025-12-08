import {Component} from '@angular/core';
import {SvgComponent} from '../../../shared/components/svg/svg.component';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-footer',
  imports: [
    SvgComponent,
    TranslatePipe,
  ],
  template: `
    <div class="layout-footer">
      <app-svg src="/assets/images/moneyverse-logo.svg"/>
      <p>Copyright © {{ year }} - {{ 'app.footer' | translate}}</p>
    </div>
  `
})
export class FooterComponent {

  year = new Date().getFullYear();

}
