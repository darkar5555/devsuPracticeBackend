import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { apiErrorMessage } from '../../api/api-error';
import { CustomerApi } from '../../api/customer-api';
import { Customer, GENDER_LABELS } from '../../models/customer';
import { SearchBox } from '../../shared/search-box/search-box';

@Component({
  selector: 'app-customer-list',
  imports: [RouterLink, SearchBox],
  templateUrl: './customer-list.html',
})
export class CustomerList implements OnInit {
  private readonly customerApi = inject(CustomerApi);

  protected readonly genderLabels = GENDER_LABELS;
  protected readonly searchTerm = signal('');
  protected readonly customers = signal<Customer[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');

  protected readonly filteredCustomers = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    return this.customers().filter(
      (customer) =>
        customer.name.toLowerCase().includes(term) || customer.identification.includes(term),
    );
  });

  ngOnInit() {
    this.load();
  }

  protected remove(customer: Customer) {
    if (!confirm(`¿Eliminar al cliente ${customer.name}?`)) {
      return;
    }
    this.customerApi.remove(customer.id).subscribe({
      next: () => this.load(),
      error: (error) => this.errorMessage.set(apiErrorMessage(error)),
    });
  }

  private load() {
    this.loading.set(true);
    this.customerApi.list().subscribe({
      next: (customers) => {
        this.customers.set(customers);
        this.loading.set(false);
      },
      error: (error) => {
        this.errorMessage.set(apiErrorMessage(error));
        this.loading.set(false);
      },
    });
  }
}
