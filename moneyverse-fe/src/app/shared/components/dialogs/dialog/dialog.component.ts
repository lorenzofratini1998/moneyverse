import {Component, input, signal} from '@angular/core';
import {Dialog} from 'primeng/dialog';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';

@Component({
  selector: 'app-dialog',
  imports: [Dialog],
  templateUrl: './dialog.component.html',
})
export class DialogComponent<T = unknown> {
  config = input.required<DynamicDialogConfig>();

  private _isOpen = signal(false);
  private _selectedItem = signal<T | undefined>(undefined);

  isOpen = this._isOpen.asReadonly();
  selectedItem = this._selectedItem.asReadonly();

  open(item?: T) {
    this._selectedItem.set(item);
    this._isOpen.set(true);
  }

  close() {
    this._selectedItem.set(undefined);
    this._isOpen.set(false);
  }
}
