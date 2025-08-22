import {Injectable, signal, WritableSignal} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DialogService<T = unknown> {
  private _isOpen = signal(false);
  private _selectedItem = signal<T | undefined>(undefined);
  private dialogs = new Map<string, { isOpen: WritableSignal<boolean>, selectedItem: WritableSignal<unknown> }>();

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
