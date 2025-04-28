import {Injectable, signal} from '@angular/core';
import {Category} from './budget.model';

@Injectable({providedIn: 'root'})
export class CategoryStore {
  private readonly selectedCategory$ = signal<Category | null>(null);
  private readonly isFormOpen$ = signal(false);

  selectedCategory = this.selectedCategory$.asReadonly();
  isFormOpen = this.isFormOpen$.asReadonly();

  openForm(category?: Category) {
    this.selectedCategory$.set(category ?? null);
    this.isFormOpen$.set(true);
  }

  closeForm() {
    this.selectedCategory$.set(null);
    this.isFormOpen$.set(false);
  }

  setSelectedCategory(category: Category) {
    this.selectedCategory$.set(category);
  }
}
