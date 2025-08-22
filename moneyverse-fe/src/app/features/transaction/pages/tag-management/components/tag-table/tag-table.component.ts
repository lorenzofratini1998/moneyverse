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

  onDelete = output<Tag>();
  onEdit = output<Tag>();

  config = computed<TableConfig<Tag>>(() => ({
    stripedRows: true,
    paginator: true,
    rows: 5,
    rowsPerPageOptions: [5, 10, 25, 50],
    showCurrentPageReport: true,
    currentPageReportTemplate: 'Showing {first} to {last} of {totalRecords} entries',
    dataKey: 'tagId'
  }))

  columns = signal<TableColumn<Tag>[]>([
    {field: 'tagName', header: 'Name', sortable: true},
    {field: 'description', header: 'Description'},
  ])

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
      message: `Are you sure that you want to delete the tag "${tag.tagName}"?`,
      header: 'Delete tag',
      accept: () => this.onDelete.emit(tag)
    });
  }
}
