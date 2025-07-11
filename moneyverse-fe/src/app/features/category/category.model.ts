import {Style} from '../../shared/models/common.model';

export interface Category {
  categoryId: string,
  userId: string,
  categoryName: string,
  description: string,
  parentCategory?: string,
  children?: Category[],
  style: Style
}

export interface CategoryRequest {
  userId?: string,
  categoryName?: string,
  description?: string,
  parentId?: string,
  style?: Style
}

export interface CategoryFormData {
  categoryName: string,
  parentCategory?: string,
  description?: string,
  style: Style
}

export interface CategoryForm {
  categoryId?: string,
  formData: CategoryFormData
}

export interface Budget {
  budgetId: string,
  category: Category,
  startDate: Date,
  endDate: Date,
  amount: number,
  budgetLimit: number,
  currency: string
}

export interface BudgetRequest {
  categoryId?: string,
  startDate: Date,
  endDate: Date,
  amount?: number,
  budgetLimit: number,
  currency: string
}

export interface BudgetFormData {
  startDate: Date,
  endDate: Date,
  category: string,
  budgetLimit: number,
  amount?: number,
  currency: string
}

export interface BudgetForm {
  budgetId?: string,
  formData: BudgetFormData
}
