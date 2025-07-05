import {Component, input, output} from '@angular/core';
import {SvgComponent} from "../svg/svg.component";
import {IconsEnum} from '../../models/icons.model';

@Component({
  selector: 'app-badge-filter',
  imports: [
    SvgComponent
  ],
  templateUrl: './badge-filter.component.html',
  styleUrl: './badge-filter.component.scss'
})
export class BadgeFilterComponent {

  protected readonly Icons = IconsEnum;
  name = input.required<string>();
  remove = output<any>();

  removeEmitter() {
    this.remove.emit({});
  }
}
