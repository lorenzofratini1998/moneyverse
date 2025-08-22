import {Component} from '@angular/core';
import {FlagIconComponent} from './flag-icon.component';

@Component({
  selector: 'svg[si-de-flag]',
  standalone: true,
  template: `
    <svg:path fill="#fc0" d="M0 320h640v160H0z"/>
    <svg:path fill="#000001" d="M0 0h640v160H0z"/>
    <svg:path fill="red" d="M0 160h640v160H0z"/>
  `,
  host: FlagIconComponent.HOST_CONFIG
})
export class DeFlagIconComponent extends FlagIconComponent {
}
