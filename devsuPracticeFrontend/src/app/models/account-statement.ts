import { AccountType } from './account';
import { TransactionType } from './transaction';

export interface StatementMovement {
  date: string;
  transactionType: TransactionType;
  amount: number;
  balance: number;
}

export interface StatementAccount {
  accountNumber: string;
  accountType: AccountType;
  initialBalance: number;
  status: boolean;
  currentBalance: number;
  totalCredits: number;
  totalDebits: number;
  transactions: StatementMovement[];
}

export interface AccountStatement {
  customer: {
    id: number;
    name: string;
    identification: string;
  };
  from: string;
  to: string;
  accounts: StatementAccount[];
  pdfBase64: string;
}
