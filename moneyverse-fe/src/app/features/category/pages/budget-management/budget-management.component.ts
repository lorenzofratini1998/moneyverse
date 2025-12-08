import {Component, computed, effect, inject, signal} from '@angular/core';
import {Budget} from '../../category.model';
import {FormsModule} from '@angular/forms';
import {Button} from 'primeng/button';
import {SvgComponent} from '../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {BudgetFormDialogComponent} from './components/budget-form-dialog/budget-form-dialog.component';
import {DatePipe} from '@angular/common';
import {PreferenceStore} from '../../../../shared/stores/preference.store';
import {CurrencyPipe} from '../../../../shared/pipes/currency.pipe';
import {Card} from 'primeng/card';
import {SelectComponent} from '../../../../shared/components/forms/select/select.component';
import {BudgetDetailComponent} from './components/budget-detail/budget-detail.component';
import {BudgetStore} from './services/budget.store';
import {BudgetFormData} from './models/form.models';
import {
  BudgetTransactionsTableComponent
} from './components/budget-transactions-table/budget-transactions-table.component';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-budget-management',
  imports: [
    FormsModule,
    Button,
    SvgComponent,
    BudgetFormDialogComponent,
    Card,
    SelectComponent,
    BudgetDetailComponent,
    BudgetTransactionsTableComponent,
    TranslatePipe
  ],
  templateUrl: './budget-management.component.html',
  providers: [DatePipe, CurrencyPipe]
})
export class BudgetManagementComponent {

  protected readonly budgetStore = inject(BudgetStore);
  private readonly preferenceStore = inject(PreferenceStore);
  private readonly datePipe = inject(DatePipe);
  protected readonly icons = IconsEnum;

  protected selectedCategory = signal<string | null>(null);
  protected selectedPeriod = signal<{ startDate: Date, endDate: Date, label: string, budgetId: string } | null>(null);

  periods = computed(() => {
    const dateFormat = this.preferenceStore.userDateFormat();
    return this.budgetStore.budgets()
      .filter(budget => budget.category.categoryId === this.selectedCategory())
      .sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime())
      .map(budget => ({
        startDate: budget.startDate,
        endDate: budget.endDate,
        label: `${this.datePipe.transform(budget.startDate, dateFormat)} - ${this.datePipe.transform(budget.endDate, dateFormat)}`,
        budgetId: budget.budgetId
      }));
  })

  protected selectedBudget = computed(() => {
    const budgets = this.budgetStore.budgets();
    const budgetId = this.selectedPeriod()?.budgetId;
    return budgets.find(b => b.budgetId === budgetId) ?? null;
  });

  constructor() {
    effect(() => {
      const budgets = this.budgetStore.budgets();
      const currentCategory = this.selectedCategory();

      const categoriesWithBudgets = this.budgetStore.budgetCategories()
        .filter(category =>
          budgets.some(b => b.category.categoryId === category.categoryId)
        );

      if (!currentCategory || !categoriesWithBudgets.some(cat => cat.categoryId === currentCategory)) {
        if (categoriesWithBudgets.length > 0) {
          this.selectedCategory.set(categoriesWithBudgets[0].categoryId);
        } else {
          this.selectedCategory.set(null);
        }
      }
    });

    effect(() => {
      const periods = this.periods();
      if (periods.length > 0) {
        this.selectedPeriod.set(periods[0]);
      } else if (periods.length === 0) {
        this.selectedPeriod.set(null);
      }
    });
  }


  protected submit(formData: BudgetFormData) {
    const budgetId = formData.budgetId;
    if (budgetId) {
      this.budgetStore.updateBudget({
        budgetId,
        request: {...formData}
      })
    } else {
      this.budgetStore.createBudget({...formData})
    }
  }

  protected deleteBudget(budget: Budget) {
    this.budgetStore.deleteBudget(budget.budgetId);
  }
}
