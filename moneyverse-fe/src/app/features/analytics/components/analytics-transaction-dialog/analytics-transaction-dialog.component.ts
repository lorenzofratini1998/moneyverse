import {Component, computed, input, TemplateRef, viewChild} from '@angular/core';
import {DialogComponent} from '../../../../shared/components/dialogs/dialog/dialog.component';
import {NgTemplateOutlet} from '@angular/common';

@Component({
  selector: 'app-analytics-transaction-dialog',
  imports: [
    DialogComponent,
    NgTemplateOutlet
  ],
  template: `
    <app-dialog [config]="config()">
      <div header>
        <h3>{{ config().header }}</h3>
      </div>
      @if (dialog().selectedItem(); as item) {
        <div content>
          <ng-container *ngTemplateOutlet="tableTemplate(); context: { $implicit: item }"></ng-container>
        </div>
      }
    </app-dialog>`
})
export class AnalyticsTransactionDialogComponent<T> {
  protected dialog = viewChild.required<DialogComponent<T[]>>(DialogComponent<T[]>);

  tableTemplate = input.required<TemplateRef<any>>();

  config = computed(() => ({
    header: 'View Data',
    styleClass: 'w-[90vw] lg:w-[80vw] xl:w-[65vw] lg:max-w-[1200px]',
    maximizable: true,
  }));

  open(item?: T[]) {
    this.dialog().open(item);
  }
}
