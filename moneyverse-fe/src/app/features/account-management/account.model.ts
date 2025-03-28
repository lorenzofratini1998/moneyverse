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

export interface AccountSummary {
  totalBalance: number;
  categories: AccountCategorySummary[];
}

export interface AccountCategorySummary {
  categoryName: string;
  categoryBalance: number;
}
