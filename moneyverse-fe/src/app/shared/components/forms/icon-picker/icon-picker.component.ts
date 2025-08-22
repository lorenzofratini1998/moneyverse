import {Component, forwardRef, viewChild} from '@angular/core';
import {AbstractSelectComponent} from '../AbstractSelectComponent.component';
import {IconsEnum} from '../../../models/icons.model';
import {Popover} from 'primeng/popover';
import {SvgComponent} from '../../svg/svg.component';
import {NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Message} from 'primeng/message';

const ICON_PICKER_ICONS = [
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
    Popover,
    Message
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => IconPickerComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => IconPickerComponent),
      multi: true
    }
  ],
  templateUrl: './icon-picker.component.html',
  styleUrl: './icon-picker.component.scss'
})
export class IconPickerComponent extends AbstractSelectComponent {
  protected readonly op = viewChild.required(Popover);
  protected readonly icons = Object.values(ICON_PICKER_ICONS.map(icon => icon.toString()));
  protected readonly defaultIcon = this.icons[0];

  constructor() {
    super();
    if (!this.value) {
      this.value = this.defaultIcon.toString();
    }
  }

  onIconSelect(icon: string) {
    if (this.disabled()) return;

    this.value = icon;
    this.onChange(this.value);
    this.op().hide();
  }

  toggle(event: any) {
    if (this.disabled()) return;
    this.op().toggle(event);
  }

  override onBlur(event: any) {
    super.onBlur(event);
  }

}
