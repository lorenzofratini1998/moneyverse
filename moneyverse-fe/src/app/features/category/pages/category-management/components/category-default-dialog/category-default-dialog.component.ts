import {Component, effect, inject, input, output} from '@angular/core';
import {CategoryService} from '../../../../category.service';
import {toSignal} from '@angular/core/rxjs-interop';
import {LucideAngularModule} from 'lucide-angular';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {ColorService} from '../../../../../../shared/services/color.service';
import {Dialog} from 'primeng/dialog';
import {Chip} from 'primeng/chip';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-category-default-dialog',
  imports: [
    LucideAngularModule,
    SvgComponent,
    Dialog,
    Chip,
    Button
  ],
  templateUrl: './category-default-dialog.component.html'
})
export class CategoryDefaultDialogComponent {
  private readonly categoryService = inject(CategoryService);
  protected readonly colorService = inject(ColorService);

  isOpen = input<boolean>(false);
  protected _isOpen = false;

  submitted = output<any>();

  defaultCategories$ = toSignal(this.categoryService.getDefaultCategories(), {initialValue: []});

  constructor() {
    effect(() => {
      if (this.isOpen() !== this._isOpen) {
        this._isOpen = this.isOpen();
      }
    });
  }

  open() {
    this._isOpen = true;
  }

  close() {
    this._isOpen = false;
  }

  onClose(): void {
    this.close();
  }

  onConfirm(): void {
    this.submitted.emit({});
    this.close();
  }
}
