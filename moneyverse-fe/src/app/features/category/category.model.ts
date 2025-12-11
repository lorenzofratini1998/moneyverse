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

export interface CategoryCriteria {
  name?: string,
  parentCategories?: string[]
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
