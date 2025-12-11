import {Directive, input} from '@angular/core';

@Directive()
export abstract class FlagIconComponent {
  protected readonly xmlns = 'http://www.w3.org/2000/svg';
  readonly viewBox = input<string>('0 0 640 480');

  protected static readonly HOST_CONFIG = {
    '[attr.xmlns]': 'xmlns',
    '[attr.viewBox]': 'viewBox()'
  }
}
