import {Component, computed, model, output} from '@angular/core';
import {IconsEnum} from '../../models/icons.model';
import {SvgComponent} from '../svg/svg.component';

export const ICON_PICKER_ICONS = [
  IconsEnum.CIRCLE_DOLLAR_SIGN,
  IconsEnum.BOOK,
  IconsEnum.DRAMA,
  IconsEnum.UTENSILS_CROSSED
];

@Component({
  selector: 'app-icon-picker',
  imports: [
    SvgComponent
  ],
  templateUrl: './icon-picker.component.html',
  styleUrl: './icon-picker.component.scss'
})
export class IconPickerComponent {
  protected readonly icons = ICON_PICKER_ICONS;
  readonly defaultIcon = ICON_PICKER_ICONS[0];
  initialIcon = model<IconsEnum>(this.defaultIcon);
  selectedIcon = computed(() => this.initialIcon());
  select = output<IconsEnum>();

  selectIcon(icon: IconsEnum) {
    this.initialIcon.set(icon);
    this.select.emit(icon);
  }

  reset() {
    this.initialIcon.set(this.defaultIcon);
  }
}
