import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'clientes', pathMatch: 'full' },
  {
    path: 'clientes',
    title: 'Clientes',
    loadComponent: () =>
      import('./customers/customer-list/customer-list').then((m) => m.CustomerList),
  },
  {
    path: 'cuentas',
    title: 'Cuentas',
    loadComponent: () => import('./accounts/account-list/account-list').then((m) => m.AccountList),
  },
  {
    path: 'movimientos',
    title: 'Movimientos',
    loadComponent: () =>
      import('./transactions/transaction-list/transaction-list').then((m) => m.TransactionList),
  },
  {
    path: 'reportes',
    title: 'Reportes',
    loadComponent: () => import('./reports/report-page/report-page').then((m) => m.ReportPage),
  },
  { path: '**', redirectTo: 'clientes' },
];
