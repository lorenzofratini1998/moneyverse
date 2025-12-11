import {Component, input} from '@angular/core';
import {Button} from 'primeng/button';
import {SvgComponent} from '../svg/svg.component';

export interface ManagementAction {
  icon: string;
  label?: string;
  severity?: 'primary' | 'secondary' | 'success' | 'info' | 'warn' | 'help' | 'danger';
  variant?: 'text' | 'outlined';
  rounded?: boolean;
  condition?: () => boolean;
  action: () => void;
}

export interface ManagementConfig {
  title: string;
  actions: ManagementAction[];
}

@Component({
  selector: 'app-management',
  imports: [
    Button,
    SvgComponent
  ],
  template: `
    <div>
      <header class="mb-8 flex justify-between items-center">
        <h1>{{ config().title }}</h1>
        <div class="flex gap-3">
          @for (action of config().actions; track action.label || action.icon) {
            @if (!action.condition || action.condition()) {
              <p-button
                (onClick)="action.action()"
                [rounded]="action.rounded ?? true"
                [variant]="action.variant"
                [severity]="action.severity || 'primary'">
                <app-svg [name]="action.icon"/>
                @if (action.label) {
                  <span>{{ action.label }}</span>
                }
              </p-button>
            }
          }
        </div>
      </header>

      <div class="management-content">
        <ng-content></ng-content>
      </div>
    </div>
  `
})
export class ManagementComponent {
  config = input.required<ManagementConfig>();
}
