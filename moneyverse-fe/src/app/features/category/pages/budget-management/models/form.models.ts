export interface BudgetFormData {
  budgetId?: string,
  startDate: Date,
  endDate: Date,
  categoryId: string,
  budgetLimit: number,
  amount?: number,
  currency: string
}
