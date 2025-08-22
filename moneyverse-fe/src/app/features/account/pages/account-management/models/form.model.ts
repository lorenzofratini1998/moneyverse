import {BoundCriteria} from '../../../../../shared/models/criteria.model';

export interface AccountFormData {
  accountId?: string,
  accountName: string,
  accountDescription?: string,
  accountCategory: string,
  currency: string,
  balance: number,
  balanceTarget?: number
  isDefault?: boolean
}

export interface AccountFilterFormData {
  accountCategories?: string[];
  currencies?: string[];
  balance?: BoundCriteria,
  balanceTarget?: BoundCriteria,
  isDefault?: boolean;
}
