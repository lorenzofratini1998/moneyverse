import {Component, inject, viewChild} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {CategoryFilterStore} from '../../services/category-filter.store';
import {CategoryFilterFormComponent} from '../category-filter-form/category-filter-form.component';
import {FilterPanelComponent} from '../../../../../../shared/components/filter-panel/filter-panel.component';

@Component({
  selector: 'app-category-filter-panel',
  imports: [
    ReactiveFormsModule,
    FilterPanelComponent,
    CategoryFilterFormComponent
  ],
  template: `
    <app-filter-panel [form]="form()"
                      [activeFiltersCount]="categoryFilterStore.activeFiltersCount()">
      <div content>
        <app-category-filter-form/>
      </div>
    </app-filter-panel>
  `
})
export class CategoryFilterPanelComponent {
  form = viewChild.required(CategoryFilterFormComponent);
  protected readonly categoryFilterStore = inject(CategoryFilterStore);
}
