import {Component, effect, inject, input, output, ViewChild} from '@angular/core';
import {Color, COLORS} from '../../models/color.model';
import {NgStyle} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Popover} from 'primeng/popover';
import {ColorService} from '../../services/color.service';

@Component({
  selector: 'app-color-picker',
  imports: [
    NgStyle,
    FormsModule,
    Popover
  ],
  templateUrl: './color-picker.component.html'
})
export class ColorPickerComponent {
  private readonly colorService = inject(ColorService);
  color = input<string>(this.colorService.default().name);
  protected colors: Color[] = COLORS.map(c => this.colorService.color(c.name, true));
  protected _selectedColor: Color = this.colors.find(c => c.selected) ?? this.colors[0];
  selected = output<Color>();

  @ViewChild(Popover) op!: Popover;

  constructor() {
    effect(() => {
      this._selectedColor = this.colorService.color(this.color(), true);
    });
  }

  get selectedColor() {
    return this._selectedColor;
  }

  onShow() {
    this.colors.forEach(color => color.selected = color.name === this._selectedColor.name);
  }

  onSelect(selected: string) {
    const color = this.colorService.color(selected, true);
    this._selectedColor = color;
    this.selected.emit(color);
    this.op.hide();
  }

  toggle(event: any) {
    this.op.toggle(event);
  }

  reset() {
    this._selectedColor = this.colorService.color(this.colorService.default().name, true);
  }

}
