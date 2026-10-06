import { DecimalPipe } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Account, ACCOUNT_TYPE_LABELS } from '../../models/account';
import { SearchBox } from '../../shared/search-box/search-box';

@Component({
  selector: 'app-account-list',
  imports: [RouterLink, DecimalPipe, SearchBox],
  templateUrl: './account-list.html',
})
export class AccountList {
  protected readonly accountTypeLabels = ACCOUNT_TYPE_LABELS;
  protected readonly searchTerm = signal('');
  protected readonly accounts = signal<Account[]>([]);

  protected readonly filteredAccounts = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    return this.accounts().filter(
      (account) =>
        account.accountNumber.includes(term) || account.customerName.toLowerCase().includes(term),
    );
  });

  protected remove(account: Account) {
    if (confirm(`¿Eliminar la cuenta ${account.accountNumber}?`)) {
      this.accounts.update((list) => list.filter((item) => item.id !== account.id));
    }
  }
}
