import {Component, computed, effect, inject, signal, ViewChild} from '@angular/core';
import {AuthService} from '../../../../core/auth/auth.service';
import {CategoryService} from '../../category.service';
import {ConfirmationService, MessageService} from 'primeng/api';
import {CategoryStore} from '../../category.store';
import {Budget, BudgetForm, BudgetFormData, Category} from '../../category.model';
import {Select} from 'primeng/select';
import {FormsModule} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';
import {Button, ButtonDirective} from 'primeng/button';
import {SvgComponent} from '../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {BudgetFormDialogComponent} from './components/budget-form-dialog/budget-form-dialog.component';
import {ToastEnum} from '../../../../shared/components/toast/toast.component';
import {Toast} from 'primeng/toast';
import {DatePipe} from '@angular/common';
import {PreferenceStore} from '../../../../shared/stores/preference.store';
import {Knob} from 'primeng/knob';
import {CurrencyPipe} from '../../../../shared/pipes/currency.pipe';
import {ColorService} from '../../../../shared/services/color.service';
import {Card} from 'primeng/card';
import {ConfirmDialog} from 'primeng/confirmdialog';

@Component({
  selector: 'app-budget-management',
  imports: [
    Select,
    FormsModule,
    FloatLabel,
    Button,
    SvgComponent,
    BudgetFormDialogComponent,
    Toast,
    Knob,
    Card,
    ButtonDirective,
    ConfirmDialog
  ],
  templateUrl: './budget-management.component.html',
  styleUrl: './budget-management.component.scss',
  providers: [ConfirmationService, MessageService, DatePipe, CurrencyPipe]
})
export class BudgetManagementComponent {

  protected readonly categoryStore = inject(CategoryStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly authService = inject(AuthService);
  protected readonly categoryService = inject(CategoryService);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);
  private readonly colorService = inject(ColorService);
  private readonly datePipe = inject(DatePipe);
  protected readonly currencyPipe = inject(CurrencyPipe);
  protected readonly Icons = IconsEnum;

  protected selectedCategory$ = signal<Category | null>(null);
  protected selectedPeriod$ = signal<{ startDate: Date, endDate: Date, label: string, budgetId: string } | null>(null);
  protected selectedBudget$ = signal<Budget | null>(null);
  protected readonly IconsEnum = IconsEnum;

  @ViewChild(BudgetFormDialogComponent) budgetForm!: BudgetFormDialogComponent

  protected budgets = signal<Budget[]>([]);

  periods = computed(() => {
    const dateFormat = this.preferenceStore.userDateFormat();
    return this.budgets()
      .filter(budget => budget.category.categoryId === this.selectedCategory$()?.categoryId)
      .sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime())
      .map(budget => ({
        startDate: budget.startDate,
        endDate: budget.endDate,
        label: `${this.datePipe.transform(budget.startDate, dateFormat)} - ${this.datePipe.transform(budget.endDate, dateFormat)}`,
        budgetId: budget.budgetId
      }));
  })

  constructor() {
    this.loadBudgets();
    effect(() => {
      const categories = this.categoryStore.categories();
      const budgets = this.budgets();

      if (categories.length > 0 && budgets.length > 0 && !this.selectedCategory$()) {
        const categoryWithBudget = categories.find(category =>
          budgets.some(b => b.category.categoryId === category.categoryId)
        );
        if (categoryWithBudget) {
          this.selectedCategory$.set(categoryWithBudget);
        }
      }
    });

    effect(() => {
      const periods = this.periods();
      if (periods.length > 0) {
        this.selectedPeriod$.set(periods[0]);
      } else if (periods.length === 0) {
        this.selectedPeriod$.set(null);
      }
    });

    effect(() => {
      const budgets = this.budgets();
      const budgetId = this.selectedPeriod$()?.budgetId;
      this.selectedBudget$.set(budgets.find(b => b.budgetId === budgetId) ?? null);
    });
  }

  protected get selectedCategory(): Category | null {
    return this.selectedCategory$();
  }

  protected set selectedCategory(category: Category) {
    this.selectedCategory$.set(category);
    const newPeriods = this.periods();
    this.selectedPeriod$.set(newPeriods.length > 0 ? newPeriods[0] : null);
  }

  protected get selectedPeriod() {
    return this.selectedPeriod$();
  }

  protected set selectedPeriod(period: { startDate: Date, endDate: Date, label: string, budgetId: string } | null) {
    this.selectedPeriod$.set(period);
  }

  protected get selectedBudget() {
    return this.selectedBudget$();
  }

  protected getKnobValueColor(amount: number, limit: number) {
    return this.getKnobColor(amount, limit).text;
  }

  protected getKnobRangeColor(amount: number, limit: number) {
    return this.getKnobColor(amount, limit).background;
  }

  private getKnobColor(amount: number, limit: number) {
    if (amount / limit <= 0.5) {
      return this.colorService.color('green')
    } else if (amount / limit <= 0.75) {
      return this.colorService.color('yellow')
    } else {
      return this.colorService.color('red')
    }
  }

  onSave(budgetForm: BudgetForm) {
    if (budgetForm.budgetId) {
      this.updateBudget(budgetForm.budgetId, budgetForm.formData);
    } else {
      this.createBudget(budgetForm.formData);
    }
  }

  private updateBudget(budgetId: string, budgetForm: BudgetFormData) {
    this.categoryService.updateBudget(budgetId, {
      startDate: budgetForm.startDate,
      endDate: budgetForm.endDate,
      budgetLimit: budgetForm.budgetLimit,
      amount: budgetForm.amount,
      currency: budgetForm.currency
    }).subscribe({
      next: (budget) => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: 'Budget updated successfully.'
        });
        this.budgetForm.reset();
        this.loadBudgets();
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Error during the budget update.'
        });
      }
    })
  }

  private createBudget(budgetForm: BudgetFormData) {
    this.categoryService.createBudget({
      startDate: budgetForm.startDate,
      endDate: budgetForm.endDate,
      budgetLimit: budgetForm.budgetLimit,
      categoryId: budgetForm.category,
      currency: budgetForm.currency
    }).subscribe({
      next: () => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: 'Budget created successfully.'
        });
        this.budgetForm.reset();
        this.loadBudgets();
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Error during the budget creation.'
        });
      }
    })
  }

  onDelete(event: Event, budget: Budget) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: `Are you sure you want to delete the budget (${this.datePipe.transform(budget.startDate, this.preferenceStore.userDateFormat())} - ${this.datePipe.transform(budget.endDate, this.preferenceStore.userDateFormat())}) of the category ${budget.category.categoryName.toUpperCase()}?`,
      header: 'Delete budget',
      rejectLabel: 'Cancel',
      rejectButtonProps: {
        label: 'Cancel',
        severity: 'secondary',
        outlined: true,
      },
      acceptButtonProps: {
        label: 'Delete',
        severity: 'danger',
      },
      accept: () => {
        this.deleteBudget(budget.budgetId);
      },
    })
  }

  private deleteBudget(budgetId: string) {
    this.categoryService.deleteBudget(budgetId).subscribe({
      next: () => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: 'Budget deleted successfully.'
        });
        this.loadBudgets();
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Error during the budget deletion.'
        });
      }
    })
  }

  loadBudgets() {
    const userId = this.authService.getAuthenticatedUser().userId;
    this.categoryService.getBudgetsByUser(userId).subscribe({
      next: (budgets) => this.budgets.set(budgets),
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Failed to load budgets.'
        });
      }
    });
  }
}
