import {Component, input} from '@angular/core';
import {ChipComponent} from "../../../../../../shared/components/chip/chip.component";
import {Category} from '../../../../category.model';

@Component({
  selector: 'app-category-default-grid',
  imports: [
    ChipComponent
  ],
  template: `
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      @for (category of defaultCategories(); track category.categoryId) {
        <app-chip [color]="category.style.color"
                  [icon]="category.style.icon"
                  [message]="category.categoryName"/>
      }
    </div>
  `
})
export class CategoryDefaultGridComponent {
  defaultCategories = input.required<Category[]>();
}
