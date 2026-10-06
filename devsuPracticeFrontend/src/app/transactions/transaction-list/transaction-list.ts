import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Transaction, TRANSACTION_TYPE_LABELS } from '../../models/transaction';
import { SearchBox } from '../../shared/search-box/search-box';

@Component({
  selector: 'app-transaction-list',
  imports: [RouterLink, DatePipe, DecimalPipe, SearchBox],
  templateUrl: './transaction-list.html',
})
export class TransactionList {
  protected readonly transactionTypeLabels = TRANSACTION_TYPE_LABELS;
  protected readonly searchTerm = signal('');
  protected readonly transactions = signal<Transaction[]>([]);

  protected readonly filteredTransactions = computed(() => {
    const term = this.searchTerm().trim();
    return this.transactions().filter((transaction) => transaction.accountNumber.includes(term));
  });

  protected remove(transaction: Transaction) {
    if (confirm(`¿Eliminar el movimiento de la cuenta ${transaction.accountNumber}?`)) {
      this.transactions.update((list) => list.filter((item) => item.id !== transaction.id));
    }
  }
}
