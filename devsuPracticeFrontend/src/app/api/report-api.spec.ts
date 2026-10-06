import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ReportApi } from './report-api';

describe('ReportApi', () => {
  it('requests the statement with the date range and customer params', () => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    const api = TestBed.inject(ReportApi);
    const http = TestBed.inject(HttpTestingController);

    api.accountStatement(2, '2022-02-01', '2022-02-28').subscribe();

    const request = http.expectOne((req) => req.url === '/api/reportes');
    expect(request.request.params.get('fecha')).toBe('2022-02-01,2022-02-28');
    expect(request.request.params.get('cliente')).toBe('2');
    request.flush({});
    http.verify();
  });
});
