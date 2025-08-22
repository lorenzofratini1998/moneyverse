import {Style} from '../../shared/models/common.model';
import {BoundCriteria, DateCriteria, PageCriteria, SortCriteria} from '../../shared/models/criteria.model';

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

export interface TransactionRequestItem {
  accountId?: string,
  categoryId?: string,
  date?: Date,
  description?: string,
  amount?: number,
  currency?: string
  tags?: Tag[]
}

export interface TransactionRequest {
  userId: string,
  transactions: TransactionRequestItem[]
}

export enum TransactionCriteriaTypeEnum {
  EXPENSE = 'EXPENSE',
  INCOME = 'INCOME'
}

export interface TransactionCriteria {
  type?: TransactionCriteriaTypeEnum,
  accounts?: string[],
  categories?: string[],
  date?: DateCriteria,
  amount?: BoundCriteria,
  tags?: string[],
  budget?: string,
  subscription?: boolean,
  transfer?: boolean,
  page?: PageCriteria,
  sort?: SortCriteria
}

export enum TransactionSortAttributeEnum {
  DATE = 'date',
  AMOUNT = 'amount'
}

export interface Transfer {
  transferId: string,
  userId: string,
  date: Date,
  amount: number
  currency: string
  transactionFrom: Transaction,
  transactionTo: Transaction
}

export interface TransferRequest {
  userId?: string,
  fromAccount?: string,
  toAccount?: string,
  amount?: number,
  date?: Date,
  currency?: string
}

export interface Subscription {
  subscriptionId: string,
  userId: string,
  accountId: string,
  categoryId: string,
  amount: number,
  totalAmount: number,
  currency: string,
  subscriptionName: string,
  recurrenceRule: RecurrenceRule,
  startDate: Date,
  endDate: Date,
  nextExecutionDate: Date,
  active: boolean,
  transactions: Transaction[]
}

export interface SubscriptionRequest {
  userId?: string,
  accountId?: string,
  categoryId?: string,
  subscriptionName?: string,
  amount?: number,
  currency?: string
  recurrence?: RecurrenceRule
  isActive?: boolean
  nextExecutionDate?: Date
}

export interface RecurrenceRule {
  recurrenceRule: string,
  startDate: Date,
  endDate: Date
}

export interface RecurrenceRuleOption {
  label: RecurrenceRuleEnum,
  value: string,
  default: boolean
}

export enum RecurrenceRuleEnum {
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY',
  YEARLY = 'YEARLY'
}

export const recurrenceRuleOptions: RecurrenceRuleOption[] = [
  {label: RecurrenceRuleEnum.WEEKLY, value: 'FREQ=WEEKLY', default: false},
  {label: RecurrenceRuleEnum.MONTHLY, value: 'FREQ=MONTHLY', default: true},
  {label: RecurrenceRuleEnum.YEARLY, value: 'FREQ=YEARLY', default: false}
];
