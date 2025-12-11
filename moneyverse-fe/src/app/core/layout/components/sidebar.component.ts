import {Component, ElementRef} from '@angular/core';
import {MenuComponent} from './menu.component';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [MenuComponent],
  template: `
    <div class="layout-sidebar">
      <app-menu></app-menu>
    </div>`
})
export class AppSidebar {
  constructor(public el: ElementRef) {
  }
}
