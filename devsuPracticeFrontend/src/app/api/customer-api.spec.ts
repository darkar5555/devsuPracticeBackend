import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Customer, CustomerRequest } from '../models/customer';
import { CustomerApi } from './customer-api';

const customer: Customer = {
  id: 1,
  name: 'Jose Lema',
  gender: 'MALE',
  age: 35,
  identification: '1710000001',
  address: 'Otavalo sn y principal',
  phone: '098254785',
  status: true,
};

describe('CustomerApi', () => {
  let api: CustomerApi;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    api = TestBed.inject(CustomerApi);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lists customers from /api/clientes', () => {
    let result: Customer[] = [];
    api.list().subscribe((customers) => (result = customers));

    const request = http.expectOne('/api/clientes');
    expect(request.request.method).toBe('GET');
    request.flush([customer]);

    expect(result).toEqual([customer]);
  });

  it('posts the new customer', () => {
    const body: CustomerRequest = { ...customer, password: '1234' };
    api.create(body).subscribe();

    const request = http.expectOne('/api/clientes');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(body);
    request.flush(customer);
  });

  it('deletes by id', () => {
    api.remove(1).subscribe();

    const request = http.expectOne('/api/clientes/1');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });
});
