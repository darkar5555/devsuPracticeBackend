import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AccountApi } from '../../api/account-api';
import { apiErrorMessage } from '../../api/api-error';
import { Account, ACCOUNT_TYPE_LABELS } from '../../models/account';
import { SearchBox } from '../../shared/search-box/search-box';

@Component({
  selector: 'app-account-list',
  imports: [RouterLink, DecimalPipe, SearchBox],
  templateUrl: './account-list.html',
})
export class AccountList implements OnInit {
  private readonly accountApi = inject(AccountApi);

  protected readonly accountTypeLabels = ACCOUNT_TYPE_LABELS;
  protected readonly searchTerm = signal('');
  protected readonly accounts = signal<Account[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');

  protected readonly filteredAccounts = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    return this.accounts().filter(
      (account) =>
        account.accountNumber.includes(term) || account.customerName.toLowerCase().includes(term),
    );
  });

  ngOnInit() {
    this.load();
  }

  protected remove(account: Account) {
    if (!confirm(`¿Eliminar la cuenta ${account.accountNumber}?`)) {
      return;
    }
    this.accountApi.remove(account.id).subscribe({
      next: () => this.load(),
      error: (error) => this.errorMessage.set(apiErrorMessage(error)),
    });
  }

  private load() {
    this.loading.set(true);
    this.accountApi.list().subscribe({
      next: (accounts) => {
        this.accounts.set(accounts);
        this.loading.set(false);
      },
      error: (error) => {
        this.errorMessage.set(apiErrorMessage(error));
        this.loading.set(false);
      },
    });
  }
}
