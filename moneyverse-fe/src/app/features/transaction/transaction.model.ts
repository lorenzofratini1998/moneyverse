import {Category} from '../category/category.model';
import {Account} from '../account/account.model';
import {Style} from '../../shared/models/common.model';

export interface Tag {
  tagId: string,
  userId: string,
  tagName: string,
  description: string
  style: Style
}

export interface TagRequest {
  userId?: string,
  tagName?: string,
  description?: string
  style?: Style
}

export interface TagFormData {
  tagName: string,
  description?: string,
  style: Style
}

export interface TagForm {
  tagId?: string,
  formData: TagFormData
}

export interface Transaction {
  transactionId: string,
  userId: string,
  accountId: string,
  categoryId: string,
  budgetId: string,
  date: Date,
  description: string,
  amount: number,
  normalizedAmount: number,
  currency: string,
  tags: Tag[]
  transferId: string,
  subscriptionId: string
}

export interface EnrichedTransaction extends Transaction {
  account?: Account;
  category?: Category;
}

export interface TransactionRequestItem {
  accountId: string,
  categoryId?: string,
  date: Date,
  description: string,
  amount: number,
  currency: string
  tags?: Tag[]
}

export interface TransactionRequest {
  userId: string,
  transactions: TransactionRequestItem[]
}
