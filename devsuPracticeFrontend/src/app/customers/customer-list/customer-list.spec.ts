import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CustomerApi } from '../../api/customer-api';
import { Customer } from '../../models/customer';
import { CustomerList } from './customer-list';

const customers: Customer[] = [
  {
    id: 1,
    name: 'Jose Lema',
    gender: 'MALE',
    age: 35,
    identification: '1710000001',
    address: 'Otavalo',
    phone: '098254785',
    status: true,
  },
  {
    id: 2,
    name: 'Marianela Montalvo',
    gender: 'FEMALE',
    age: 29,
    identification: '1710000002',
    address: 'Amazonas',
    phone: '097548965',
    status: true,
  },
];

describe('CustomerList', () => {
  function setup(api: Partial<CustomerApi>) {
    TestBed.configureTestingModule({
      imports: [CustomerList],
      providers: [provideRouter([]), { provide: CustomerApi, useValue: api }],
    });
    const fixture = TestBed.createComponent(CustomerList);
    fixture.detectChanges();
    return fixture;
  }

  it('renders one row per customer returned by the API', () => {
    const fixture = setup({ list: () => of(customers) });

    const rows = fixture.nativeElement.querySelectorAll('tbody tr');
    expect(rows.length).toBe(2);
    expect(rows[1].textContent).toContain('Marianela Montalvo');
  });

  it('filters by the search term', () => {
    const fixture = setup({ list: () => of(customers) });

    const input = fixture.nativeElement.querySelector('input[type="search"]') as HTMLInputElement;
    input.value = 'mari';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const rows = fixture.nativeElement.querySelectorAll('tbody tr');
    expect(rows.length).toBe(1);
    expect(rows[0].textContent).toContain('Marianela Montalvo');
  });

  it('shows the error when the API is unreachable', () => {
    const fixture = setup({ list: () => throwError(() => new HttpErrorResponse({ status: 0 })) });

    expect(fixture.nativeElement.querySelector('.alert-error').textContent).toContain(
      'No se pudo conectar con el servidor.',
    );
  });
});
