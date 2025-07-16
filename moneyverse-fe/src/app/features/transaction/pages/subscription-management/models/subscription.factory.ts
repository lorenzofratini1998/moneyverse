import {SubscriptionFormData, SubscriptionRequest} from "../../../transaction.model";

export class SubscriptionFactory {
  static createSubscriptionRequest(userId: string, formData: SubscriptionFormData): SubscriptionRequest {
    return {
      userId: userId,
      accountId: formData.accountId,
      categoryId: formData.categoryId,
      subscriptionName: formData.subscriptionName,
      amount: formData.amount,
      currency: formData.currency,
      recurrence: formData.recurrence,
    };
  }

  static createSubscriptionUpdateRequest(userId: string, formData: SubscriptionFormData): Partial<SubscriptionRequest> {
    const request: Partial<SubscriptionRequest> = {};
    request.userId = userId;
    request.accountId = formData.accountId;
    request.categoryId = formData.categoryId;
    request.subscriptionName = formData.subscriptionName;
    request.amount = formData.amount;
    request.currency = formData.currency;
    request.recurrence = formData.recurrence;
    request.isActive = formData.isActive;
    request.nextExecutionDate = formData.nextExecutionDate;
    return request;
  }
}
