import {TemplateRef} from '@angular/core';

export interface TableConfig<T = any> {
  alwaysShowPaginator?: boolean,
  currentPageReportTemplate?: string,
  customSort?: boolean,
  dataKey?: keyof T | string,
  emptyMessage?: string,
  lazy?: boolean,
  paginator?: boolean,
  rows?: number,
  rowsPerPageOptions?: number[],
  scrollable?: boolean,
  scrollHeight?: string,
  showCurrentPageReport?: boolean,
  sortField?: string,
  sortOrder?: number,
  stripedRows?: boolean,
  styleClass?: string,
  tableStyle?: { [klass: string]: any },
  totalRecords?: number
}

export interface TableColumn<T> {
  cellTemplate?: TemplateRef<any>,
  class?: string,
  field: keyof T,
  header: string,
  sortable?: boolean
}

export interface TableAction<T> {
  icon: string;
  severity?: 'secondary' | 'danger' | 'success' | 'info';
  tooltip?: string;
  visible?: (row: T) => boolean;
  click: (row: T, event?: Event) => void;
}
