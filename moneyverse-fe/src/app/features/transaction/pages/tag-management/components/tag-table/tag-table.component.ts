import {Component, computed, inject, output, signal} from '@angular/core';
import {TableModule} from 'primeng/table';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {TagStore} from '../../services/tag.store';
import {Tag} from '../../../../transaction.model';
import {ChipComponent} from '../../../../../../shared/components/chip/chip.component';
import {TableAction, TableColumn, TableConfig} from '../../../../../../shared/models/table.model';
import {AppConfirmationService} from '../../../../../../shared/services/confirmation.service';
import {TableComponent} from '../../../../../../shared/components/table/table.component';
import {CellTemplateDirective} from '../../../../../../shared/directives/cell-template.directive';
import {TableActionsComponent} from '../../../../../../shared/components/table-actions/table-actions.component';
import {TranslationService} from '../../../../../../shared/services/translation.service';

@Component({
  selector: 'app-tag-table',
  imports: [
    TableModule,
    ChipComponent,
    TableComponent,
    CellTemplateDirective,
    TableActionsComponent
  ],
  templateUrl: './tag-table.component.html'
})
export class TagTableComponent {
  protected readonly tagStore = inject(TagStore);
  private readonly confirmationService = inject(AppConfirmationService);
  private readonly translateService = inject(TranslationService);

  onDelete = output<Tag>();
  onEdit = output<Tag>();

  config = computed<TableConfig<Tag>>(() => {
    this.translateService.lang();
    return {
      stripedRows: true,
      paginator: true,
      rows: 5,
      rowsPerPageOptions: [5, 10, 25, 50],
      showCurrentPageReport: true,
      currentPageReportTemplate: this.translateService.translate('app.table.pageReport', {
        first: '{first}',
        last: '{last}',
        totalRecords: '{totalRecords}'
      }),
      dataKey: 'tagId'
    }
  })

  columns = computed<TableColumn<Tag>[]>(() => {
    this.translateService.lang();
    return [
      {field: 'tagName', header: this.translateService.translate('app.name'), sortable: true},
      {field: 'description', header: this.translateService.translate('app.description')},
    ]
  })

  actions = computed<TableAction<Tag>[]>(() => [
    {
      icon: IconsEnum.PENCIL,
      severity: 'secondary',
      click: (tag: Tag) => this.onEdit.emit(tag),
    },
    {
      icon: IconsEnum.TRASH,
      severity: 'danger',
      click: (tag: Tag) => this.confirmDelete(tag),
    }
  ])

  confirmDelete(tag: Tag) {
    this.confirmationService.confirmDelete({
      message: this.translateService.translate('app.dialog.tag.confirmDelete', {field: tag.tagName}),
      header: this.translateService.translate('app.dialog.tag.delete'),
      accept: () => this.onDelete.emit(tag)
    });
  }
}
