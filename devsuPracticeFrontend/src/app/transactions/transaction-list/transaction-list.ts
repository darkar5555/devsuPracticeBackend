import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { apiErrorMessage } from '../../api/api-error';
import { TransactionApi } from '../../api/transaction-api';
import { Transaction, TRANSACTION_TYPE_LABELS } from '../../models/transaction';
import { SearchBox } from '../../shared/search-box/search-box';

@Component({
  selector: 'app-transaction-list',
  imports: [RouterLink, DatePipe, DecimalPipe, SearchBox],
  templateUrl: './transaction-list.html',
})
export class TransactionList implements OnInit {
  private readonly transactionApi = inject(TransactionApi);

  protected readonly transactionTypeLabels = TRANSACTION_TYPE_LABELS;
  protected readonly searchTerm = signal('');
  protected readonly transactions = signal<Transaction[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');

  protected readonly filteredTransactions = computed(() => {
    const term = this.searchTerm().trim();
    return this.transactions().filter((transaction) => transaction.accountNumber.includes(term));
  });

  ngOnInit() {
    this.load();
  }

  protected remove(transaction: Transaction) {
    if (!confirm(`¿Eliminar el movimiento de la cuenta ${transaction.accountNumber}?`)) {
      return;
    }
    this.transactionApi.remove(transaction.id).subscribe({
      next: () => this.load(),
      error: (error) => this.errorMessage.set(apiErrorMessage(error)),
    });
  }

  private load() {
    this.loading.set(true);
    this.transactionApi.list().subscribe({
      next: (transactions) => {
        this.transactions.set(transactions);
        this.loading.set(false);
      },
      error: (error) => {
        this.errorMessage.set(apiErrorMessage(error));
        this.loading.set(false);
      },
    });
  }
}
