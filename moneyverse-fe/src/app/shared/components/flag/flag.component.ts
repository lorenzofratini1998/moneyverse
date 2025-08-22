import {Component, input} from '@angular/core';
import {UsFlagIconComponent} from '../icons/flags/us-flag-icon.component';
import {ItFlagIconComponent} from '../icons/flags/it-flag-icon.component';
import {EsFlagIconComponent} from '../icons/flags/es-flag-icon.component';
import {DeFlagIconComponent} from '../icons/flags/de-flag-icon.component';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-flag',
  imports: [
    UsFlagIconComponent,
    ItFlagIconComponent,
    EsFlagIconComponent,
    DeFlagIconComponent,
    NgClass
  ],
  template: `
    @switch (code()) {
      @case ('en') {
        <svg si-us-flag [ngClass]="class()"/>
      }
      @case ('it') {
        <svg si-it-flag [ngClass]="class()"/>
      }
      @case ('es') {
        <svg si-es-flag [ngClass]="class()"/>
      }
      @case ('de') {
        <svg si-de-flag [ngClass]="class()"/>
      }
    }`
})
export class FlagComponent {
  code = input.required<string>();
  class = input<string>('');
}
