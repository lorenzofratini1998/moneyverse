import {Component, input, output} from '@angular/core';
import {Button, ButtonSeverity} from 'primeng/button';
import {IconsEnum} from '../../../models/icons.model';
import {SvgComponent} from '../../svg/svg.component';

@Component({
  selector: 'app-submit-button',
  imports: [
    Button,
    SvgComponent
  ],
  template: `
    <p-button
      type="submit"
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
export class SubmitButtonComponent {
  label = input<string>('Save');
  icon = input<IconsEnum>(IconsEnum.CHECK);
  rounded = input<boolean>(true);
  disabled = input<boolean>(false);
  severity = input<ButtonSeverity>('primary');

  onSubmit = output<void>();

  click() {
    this.onSubmit.emit();
  }
}
