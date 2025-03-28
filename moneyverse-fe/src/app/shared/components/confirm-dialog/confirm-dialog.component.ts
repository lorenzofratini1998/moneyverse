import {Component, ElementRef, input, output, ViewChild} from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  imports: [],
  templateUrl: './confirm-dialog.component.html',
  styleUrl: './confirm-dialog.component.scss'
})
export class ConfirmDialogComponent {
  title = input.required<string>();
  message = input.required<string>();
  confirm = output<boolean>();

  @ViewChild('dialog') dialog!: ElementRef<HTMLDialogElement>;

  show(): void {
    this.dialog.nativeElement.showModal();
  }

  hide(): void {
    this.dialog.nativeElement.close();
  }

  onConfirm(): void {
    this.confirm.emit(true);
    this.hide();
  }

  onCancel(): void {
    this.confirm.emit(false);
    this.hide();
  }
}
