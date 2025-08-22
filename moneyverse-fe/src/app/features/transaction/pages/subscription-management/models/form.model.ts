import {RecurrenceRule} from '../../../transaction.model';

export interface SubscriptionFormData {
  subscriptionId?: string,
  accountId: string,
  categoryId?: string,
  amount: number,
  subscriptionName: string,
  currency: string
  recurrence: RecurrenceRule
  isActive: boolean,
  nextExecutionDate: Date
}
