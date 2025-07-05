import {Component, inject, output} from '@angular/core';
import {CategoryService} from '../../../../category.service';
import {toSignal} from '@angular/core/rxjs-interop';
import {LucideAngularModule} from 'lucide-angular';
import {NgStyle} from '@angular/common';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';

@Component({
  selector: 'app-default-category-modal',
  imports: [
    LucideAngularModule,
    NgStyle,
    SvgComponent
  ],
  templateUrl: './default-category-modal.component.html'
})
export class DefaultCategoryModalComponent {
  private readonly categoryService = inject(CategoryService);
  confirm = output<any>();
  cancel = output<any>();

  defaultCategories$ = toSignal(this.categoryService.getDefaultCategories(), {initialValue: []});

  onClose(): void {
    this.cancel.emit({});
  }

  onConfirm(): void {
    this.confirm.emit({});
  }
}
