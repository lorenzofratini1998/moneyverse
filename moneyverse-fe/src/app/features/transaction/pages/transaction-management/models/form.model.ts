import {Tag, TransactionCriteriaTypeEnum} from '../../../transaction.model';
import {BoundCriteria, DateCriteria} from '../../../../../shared/models/criteria.model';

export interface TransactionFormData {
  transactionId?: string,
  accountId: string,
  categoryId?: string,
  date: Date,
  description: string,
  amount: number,
  currency: string,
  tags?: Tag[]
}

export interface TransferFormData {
  transferId?: string,
  fromAccount: string,
  toAccount: string,
  amount: number,
  date: Date,
  currency: string
}

export function isTransferFormData(data: any): data is TransferFormData {
  return 'fromAccount' in data && 'toAccount' in data;
}

export interface TransactionFilterFormData {
  type?: TransactionCriteriaTypeEnum,
  accounts?: string[],
  categories?: string[],
  date?: DateCriteria,
  amount?: BoundCriteria,
  tags?: string[],
  budget?: string,
  subscription?: boolean,
  transfer?: boolean
}
