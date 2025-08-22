import {Component} from '@angular/core';
import {FlagIconComponent} from './flag-icon.component';

@Component({
  selector: 'svg[si-it-flag]',
  standalone: true,
  template: `
    <svg:g fill-rule="evenodd" stroke-width="1pt">
      <svg:path fill="#fff" d="M0 0h640v480H0z"/>
      <svg:path fill="#009246" d="M0 0h213.3v480H0z"/>
      <svg:path fill="#ce2b37" d="M426.7 0H640v480H426.7z"/>
    </svg:g>
  `,
  host: FlagIconComponent.HOST_CONFIG
})
export class ItFlagIconComponent extends FlagIconComponent {
}
