export type TransactionType = 'DEPOSIT' | 'WITHDRAWAL';

export const TRANSACTION_TYPE_LABELS: Record<TransactionType, string> = {
  DEPOSIT: 'Depósito',
  WITHDRAWAL: 'Retiro',
};

export interface Transaction {
  id: number;
  date: string;
  transactionType: TransactionType;
  amount: number;
  balance: number;
  accountNumber: string;
}
