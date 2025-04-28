import {Component, input} from '@angular/core';
import {LucideAngularModule} from 'lucide-angular';
import {SvgIconComponent} from 'angular-svg-icon';
import {ICONS} from '../../models/icons.model';

@Component({
  selector: 'app-svg',
  imports: [
    LucideAngularModule,
    SvgIconComponent
  ],
  templateUrl: './svg.component.html'
})
export class SvgComponent {

  name = input<string>('');
  src = input<string>('');
  strokeWidth = input<number>(1.5);
  class = input<string>("size-5");

  lucideIcons = ICONS;

  get currentIcon() {
    return this.lucideIcons[this.name()];
  }

}
