import {Component, effect, input, output, ViewChild} from '@angular/core';
import {IconsEnum} from '../../models/icons.model';
import {SvgComponent} from '../svg/svg.component';
import {Popover} from 'primeng/popover';

export const ICON_PICKER_ICONS = [
  IconsEnum.BUS,
  IconsEnum.CIRCLE_DOLLAR_SIGN,
  IconsEnum.AMBULANCE,
  IconsEnum.APPLE,
  IconsEnum.AWARD,
  IconsEnum.BABY,
  IconsEnum.BATTERY,
  IconsEnum.LIGHTBULB,
  IconsEnum.BED_SINGLE,
  IconsEnum.BEER,
  IconsEnum.BLUETOOTH,
  IconsEnum.BOOK,
  IconsEnum.BRIEFCASE,
  IconsEnum.HOTEL,
  IconsEnum.CREDIT_CARD,
  IconsEnum.CAMERA,
  IconsEnum.UTENSILS_CROSSED,
  IconsEnum.COOKING_POT,
  IconsEnum.COOKIE,
  IconsEnum.DICES,
  IconsEnum.DRAMA,
  IconsEnum.DOG,
  IconsEnum.DRILL,
  IconsEnum.DUMBBELL,
  IconsEnum.GAMEPAD,
  IconsEnum.GRADUATION_CAP,
  IconsEnum.HOUSE,
  IconsEnum.HAND_HELPING,
  IconsEnum.ICE_CREAM_CONE,
  IconsEnum.PHONE,
  IconsEnum.PILL,
  IconsEnum.PIZZA,
  IconsEnum.PRINTER,
  IconsEnum.PUZZLE,
  IconsEnum.SHOPPING_CART,
  IconsEnum.SHIELD,
  IconsEnum.TICKET,
  IconsEnum.TREES,
  IconsEnum.CPU,
  IconsEnum.SMARTPHONE,
  IconsEnum.CAR,
  IconsEnum.SHIRT
];

@Component({
  selector: 'app-icon-picker',
  imports: [
    SvgComponent,
    Popover
  ],
  templateUrl: './icon-picker.component.html',
  styleUrl: './icon-picker.component.scss'
})
export class IconPickerComponent {
  protected readonly icons = Object.values(ICON_PICKER_ICONS);
  private readonly defaultIcon = ICON_PICKER_ICONS[0];
  icon = input<string>(this.defaultIcon.toString());
  _selectedIcon: IconsEnum = this.defaultIcon;
  selected = output<IconsEnum>();

  @ViewChild(Popover) op!: Popover;

  constructor() {
    effect(() => {
      this._selectedIcon = this.toIconsEnum(this.icon()) ?? this.defaultIcon;
    });
  }

  private toIconsEnum(iconString: string): IconsEnum | null {
    const found = Object.entries(IconsEnum).find(
      ([, value]) => value === iconString
    );
    return found ? IconsEnum[found[0] as keyof typeof IconsEnum] : null;
  }

  get selectedIcon() {
    return this._selectedIcon;
  }

  get default() {
    return this.defaultIcon.toString();
  }

  onSelect(icon: IconsEnum) {
    this._selectedIcon = icon;
    this.selected.emit(icon);
    this.op.hide();
  }

  toggle(event: any) {
    this.op.toggle(event);
  }

  reset() {
    this._selectedIcon = this.defaultIcon;
  }
}
