import { Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Customer, GENDER_LABELS } from '../../models/customer';
import { SearchBox } from '../../shared/search-box/search-box';

@Component({
  selector: 'app-customer-list',
  imports: [RouterLink, SearchBox],
  templateUrl: './customer-list.html',
})
export class CustomerList {
  protected readonly genderLabels = GENDER_LABELS;
  protected readonly searchTerm = signal('');
  protected readonly customers = signal<Customer[]>([]);

  protected readonly filteredCustomers = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    return this.customers().filter(
      (customer) =>
        customer.name.toLowerCase().includes(term) || customer.identification.includes(term),
    );
  });

  protected remove(customer: Customer) {
    if (confirm(`¿Eliminar al cliente ${customer.name}?`)) {
      this.customers.update((list) => list.filter((item) => item.id !== customer.id));
    }
  }
}
