import {Component, input} from '@angular/core';

@Component({
  selector: 'app-label',
  imports: [],
  template: `
    <label [for]="for()"
           class="label-container"
           [class.border-radius]="'var(--content-border-radius)'"
           [class.disabled]="disabled()"
           [attr.data-component-type]="componentType()"
    >
      <span>{{ name() }}</span>
      @if (required()) {
        <span class="label-text-required">*</span>
      }
    </label>
  `,
  styleUrl: './label.component.scss'
})
export class LabelComponent {
  for = input.required<string>();
  name = input.required<string>();
  required = input<boolean>(false);
  disabled = input<boolean>(false);
  componentType = input<'select' | 'input' | 'textarea' | 'calendar' | 'multiselect'>('input');
}
