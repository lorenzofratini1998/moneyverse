import {Component, input, output} from '@angular/core';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrl: './dialog.component.scss'
})
export class DialogComponent {
  show = input.required<boolean>();
  close = output<void>();

  onBackdropClick(event: MouseEvent) {
    this.close.emit();
  }
}
