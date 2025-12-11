import {Component, input} from '@angular/core';
import {TableAction} from '../../models/table.model';
import {ButtonDirective} from 'primeng/button';
import {Tooltip} from 'primeng/tooltip';
import {SvgComponent} from '../svg/svg.component';

@Component({
  selector: 'app-table-actions',
  imports: [
    ButtonDirective,
    Tooltip,
    SvgComponent
  ],
  template: `
    @for (action of actions(); track $index) {
      @if (!action.visible || action.visible(row())) {
        <button
          pButton
          type="button"
          text
          rounded
          [severity]="action.severity || 'secondary'"
          (click)="action.click(row(), $event)"
          [pTooltip]="action.tooltip"
        >
          <app-svg [name]="action.icon"></app-svg>
        </button>
      }
    }
  `,
})
export class TableActionsComponent<T> {
  actions = input<TableAction<T>[]>([]);
  row = input.required<T>();
}
