import {Component, contentChildren, inject, input, output, TemplateRef} from '@angular/core';
import {TableColumn, TableConfig} from '../../models/table.model';
import {TableModule} from 'primeng/table';
import {NgTemplateOutlet} from '@angular/common';
import {Button} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {SvgComponent} from '../svg/svg.component';
import {IconsEnum} from '../../models/icons.model';
import {CellTemplateDirective} from '../../directives/cell-template.directive';
import {TranslatePipe} from '@ngx-translate/core';
import {TranslationService} from '../../services/translation.service';

@Component({
  selector: 'app-table',
  imports: [
    TableModule,
    NgTemplateOutlet,
    Button,
    Ripple,
    SvgComponent,
    TranslatePipe
  ],
  templateUrl: './table.component.html'
})
export class TableComponent<T> {
  config = input.required<TableConfig<T>>();
  columns = input.required<TableColumn<T>[]>();
  data = input<T[]>([])
  actions = input<TemplateRef<any> | null>(null);
  cellTemplates = contentChildren(CellTemplateDirective<T>);
  rowExpandTemplate = input<TemplateRef<any> | null>(null);

  protected readonly icons = IconsEnum;
  private readonly translateService = inject(TranslationService);

  protected getTemplateForField(field: keyof T): TemplateRef<any> | undefined {
    const directive = this.cellTemplates().find(dir => dir.field() === field);
    return directive?.template;
  }

  protected get pageReportTemplate(): string {
    return this.translateService.translate('table.pageReportDefault', {
      current: '{currentPage}',
      total: '{totalPages}'
    });
  }

  onPage = output<any>();
  onSort = output<any>();
  onRowExpand = output<any>();
  onRowCollapse = output<any>();
}
