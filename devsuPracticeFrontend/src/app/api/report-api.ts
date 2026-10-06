import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AccountStatement } from '../models/account-statement';

@Injectable({ providedIn: 'root' })
export class ReportApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/reportes`;

  accountStatement(customerId: number, from: string, to: string): Observable<AccountStatement> {
    const params = new HttpParams().set('fecha', `${from},${to}`).set('cliente', customerId);
    return this.http.get<AccountStatement>(this.baseUrl, { params });
  }
}
