import {Component} from '@angular/core';
import {FlagIconComponent} from './flag-icon.component';

@Component({
  selector: 'svg[si-us-flag]',
  standalone: true,
  template: `
    <svg:path fill="#bd3d44" d="M0 0h640v480H0"/>
    <svg:path
      stroke="#fff"
      stroke-width="37"
      d="M0 55.3h640M0 129h640M0 203h640M0 277h640M0 351h640M0 425h640"
    />
    <svg:path fill="#192f5d" d="M0 0h364.8v258.5H0"/>
    <svg:marker id="us-a" markerHeight="30" markerWidth="30">
      <svg:path fill="#fff" d="m14 0 9 27L0 10h28L5 27z"/>
    </svg:marker>
    <svg:path
      fill="none"
      marker-mid="url(#us-a)"
      d="m0 0 16 11h61 61 61 61 60L47 37h61 61 60 61L16 63h61 61 61 61 60L47 89h61 61 60 61L16 115h61 61 61 61 60L47 141h61 61 60 61L16 166h61 61 61 61 60L47 192h61 61 60 61L16 218h61 61 61 61 60z"
    />
  `,
  host: FlagIconComponent.HOST_CONFIG
})
export class UsFlagIconComponent extends FlagIconComponent {
}
