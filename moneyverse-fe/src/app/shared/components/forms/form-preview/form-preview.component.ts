import {Component, input} from '@angular/core';
import {ChipComponent} from '../../chip/chip.component';

@Component({
  selector: 'app-form-preview',
  imports: [
    ChipComponent
  ],
  template: `
    <div class="col-span-1 flex items-center justify-center">
      <app-chip [color]="color()"
                [icon]="icon()"
                [message]="message() !== '' ? message() : 'Preview'"/>
    </div>
  `,
  styleUrl: './form-preview.component.scss'
})
export class FormPreviewComponent {
  color = input.required<string>()
  icon = input.required<string>()
  message = input.required<string>()
}
