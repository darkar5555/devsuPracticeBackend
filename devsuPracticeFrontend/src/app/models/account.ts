export type AccountType = 'SAVINGS' | 'CHECKING';

export const ACCOUNT_TYPE_LABELS: Record<AccountType, string> = {
  SAVINGS: 'Ahorros',
  CHECKING: 'Corriente',
};

export interface Account {
  id: number;
  accountNumber: string;
  accountType: AccountType;
  initialBalance: number;
  status: boolean;
  customerId: number;
  customerName: string;
}
