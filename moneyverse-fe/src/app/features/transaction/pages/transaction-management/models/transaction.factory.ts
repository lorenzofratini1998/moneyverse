import {
  TransactionFormData,
  TransactionRequest,
  TransactionRequestItem,
  TransferFormData, TransferRequest
} from '../../../transaction.model';

export class TransactionFactory {
  static createTransactionRequest(userId: string, formData: TransactionFormData): TransactionRequest {
    return {
      userId: userId,
      transactions: [
        {
          accountId: formData.account,
          categoryId: formData.category,
          amount: formData.amount,
          date: formData.date,
          description: formData.description,
          currency: formData.currency,
          tags: formData.tags
        }
      ]
    };
  }

  static createTransactionUpdateRequest(formData: TransactionFormData): Partial<TransactionRequestItem> {
    const request: Partial<TransactionRequestItem> = {};
    request.accountId = formData.account;
    request.categoryId = formData.category;
    request.amount = formData.amount;
    request.date = formData.date;
    request.description = formData.description;
    request.currency = formData.currency;
    request.tags = formData.tags;
    return request;
  }

  static createTransferRequest(userId: string, formData: TransferFormData): TransferRequest {
    return {
      userId: userId,
      date: formData.date,
      amount: formData.amount,
      currency: formData.currency,
      fromAccount: formData.fromAccount,
      toAccount: formData.toAccount
    };
  }

  static createTransferUpdateRequest(formData: TransferFormData): Partial<TransferRequest> {
    const request: Partial<TransferRequest> = {};
    request.amount = formData.amount;
    request.currency = formData.currency;
    request.fromAccount = formData.fromAccount;
    request.toAccount = formData.toAccount;
    return request;
  }
}
