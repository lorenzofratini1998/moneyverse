import {Component, forwardRef, inject, viewChild} from '@angular/core';
import {LayoutService} from '../../../../core/layout/layout.service';
import {AbstractSelectComponent} from '../AbstractSelectComponent.component';
import {Popover} from 'primeng/popover';
import {NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Message} from 'primeng/message';

interface ColorOption {
  name: string;
  selected: boolean;
}

@Component({
  selector: 'app-color-picker',
  imports: [
    Popover,
    Message
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ColorPickerComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ColorPickerComponent),
      multi: true
    }
  ],
  templateUrl: './color-picker.component.html',
  styleUrl: './color-picker.component.scss'
})
export class ColorPickerComponent extends AbstractSelectComponent {

  protected readonly layoutService = inject(LayoutService);
  protected readonly op = viewChild.required(Popover);

  protected readonly colors: ColorOption[] = [
    {name: 'red', selected: false},
    {name: 'orange', selected: false},
    {name: 'amber', selected: false},
    {name: 'yellow', selected: false},
    {name: 'lime', selected: false},
    {name: 'green', selected: false},
    {name: 'emerald', selected: false},
    {name: 'teal', selected: false},
    {name: 'cyan', selected: false},
    {name: 'sky', selected: false},
    {name: 'blue', selected: false},
    {name: 'indigo', selected: false},
    {name: 'violet', selected: false},
    {name: 'purple', selected: false},
    {name: 'fuchsia', selected: false},
    {name: 'pink', selected: false},
    {name: 'rose', selected: false}
  ];

  constructor() {
    super();

    if (!this.value) {
      this.value = 'red';
    }
  }

  override writeValue(obj: any): void {
    super.writeValue(obj);
    this.updateColorSelection();
  }

  onShow() {
    this.updateColorSelection();
  }

  onColorSelect(colorName: string) {
    if (this.disabled()) return;

    this.value = colorName;
    this.onChange(this.value);
    this.updateColorSelection();
    this.op().hide();
  }

  toggle(event: any) {
    if (this.disabled()) return;
    this.op().toggle(event);
  }

  override onBlur(event: any) {
    super.onBlur(event);
  }

  private updateColorSelection() {
    this.colors.forEach(color => {
      color.selected = color.name === this.value;
    });
  }

}
