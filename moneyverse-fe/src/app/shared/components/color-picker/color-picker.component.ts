import {Component, computed, model, output} from '@angular/core';
import {Color, COLORS} from '../../models/color.model';
import {NgStyle} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-color-picker',
  imports: [
    NgStyle,
    FormsModule
  ],
  templateUrl: './color-picker.component.html'
})
export class ColorPickerComponent {
  readonly colors = COLORS;
  readonly defaultColor = this.colors.find(color => color.selected) ?? this.colors[0];
  initialColor = model<Color>(this.defaultColor);
  selectedColor = computed(() => this.initialColor());
  select = output<Color>();

  selectColor(selectedColor: Color) {
    this.colors.forEach(color => color.selected = false);
    selectedColor.selected = true;
    this.initialColor.set(selectedColor);
    this.select.emit(selectedColor);
  }

  reset() {
    this.initialColor.set(this.defaultColor);
  }

}
