import {BoundCriteria} from "../../shared/models/criteria.model";

export interface Account {
  accountId: string,
  userId: string,
  accountName: string,
  balance: number,
  balanceTarget: number,
  accountCategory: string,
  accountDescription: string,
  default: boolean,
  currency: string
}

export interface AccountCategory {
  accountCategoryId: string,
  name: string,
  description: string
}

export interface AccountRequest {
  userId?: string;
  accountName?: string;
  balance?: number;
  balanceTarget?: number;
  accountCategory?: string;
  accountDescription?: string;
  currency?: string;
  isDefault?: boolean
}

export interface AccountCriteria {
  balance?: BoundCriteria,
  balanceTarget?: BoundCriteria,
  accountCategories?: string[],
  currencies?: string[],
  isDefault?: boolean
}
