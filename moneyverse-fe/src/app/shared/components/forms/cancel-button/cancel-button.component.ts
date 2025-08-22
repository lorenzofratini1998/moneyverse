import {Component, input, output} from '@angular/core';
import {IconsEnum} from '../../../models/icons.model';
import {Button, ButtonSeverity} from 'primeng/button';
import {SvgComponent} from '../../svg/svg.component';

@Component({
  selector: 'app-cancel-button',
  imports: [
    Button,
    SvgComponent
  ],
  template: `
    <p-button
      (onClick)="click()"
      [rounded]="true"
      [disabled]="disabled()"
      [severity]="severity()"
    >
      <app-svg [name]="icon()"/>
      <span>{{ label() }}</span>
    </p-button>
  `
})
export class CancelButtonComponent {
  label = input<string>('Cancel');
  icon = input<IconsEnum>(IconsEnum.X);
  rounded = input<boolean>(true);
  disabled = input<boolean>(false);
  severity = input<ButtonSeverity>('secondary');

  onCancel = output<void>();

  click() {
    this.onCancel.emit();
  }
}
