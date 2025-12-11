import {Component} from '@angular/core';
import {IconComponent} from './icon.component';

@Component({
  selector: 'svg[si-check]',
  standalone: true,
  template: `
    <svg:path d="M20 6 9 17l-5-5"/>
  `,
  host: IconComponent.HOST_CONFIG
})
export class CheckIconComponent extends IconComponent {

}
