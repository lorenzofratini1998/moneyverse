import {Directive, input, TemplateRef} from '@angular/core';

@Directive({
  selector: '[cellTemplate]'
})
export class CellTemplateDirective<T> {
  field = input.required<keyof T>();

  constructor(public template: TemplateRef<any>) {
  }
}
