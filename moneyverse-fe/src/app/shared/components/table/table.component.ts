import {Component, contentChildren, input, output, TemplateRef} from '@angular/core';
import {TableColumn, TableConfig} from '../../models/table.model';
import {TableModule} from 'primeng/table';
import {NgTemplateOutlet} from '@angular/common';
import {Button} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {SvgComponent} from '../svg/svg.component';
import {IconsEnum} from '../../models/icons.model';
import {CellTemplateDirective} from '../../directives/cell-template.directive';

@Component({
  selector: 'app-table',
  imports: [
    TableModule,
    NgTemplateOutlet,
    Button,
    Ripple,
    SvgComponent
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

  protected getTemplateForField(field: keyof T): TemplateRef<any> | undefined {
    const directive = this.cellTemplates().find(dir => dir.field() === field);
    return directive?.template;
  }

  onPage = output<any>();
  onSort = output<any>();
  onRowExpand = output<any>();
  onRowCollapse = output<any>();
}
