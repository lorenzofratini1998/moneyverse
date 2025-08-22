import {Component, inject} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LayoutService} from '../../../core/layout/layout.service';

@Component({
  selector: 'app-dark-mode-button',
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  template: `
    <button type="button" class="layout-topbar-action" (click)="layoutService.toggleTheme()">
      @if (layoutService.theme() === 'dark') {
        <i class="pi pi-moon"></i>
      } @else {
        <i class="pi pi-sun"></i>
      }
    </button>
  `
})
export class DarkModeButtonComponent {

  protected readonly layoutService = inject(LayoutService);
}
