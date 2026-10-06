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
    path: 'clientes/nuevo',
    title: 'Nuevo cliente',
    loadComponent: () =>
      import('./customers/customer-form/customer-form').then((m) => m.CustomerForm),
  },
  {
    path: 'clientes/:id',
    title: 'Editar cliente',
    loadComponent: () =>
      import('./customers/customer-form/customer-form').then((m) => m.CustomerForm),
  },
  {
    path: 'cuentas',
    title: 'Cuentas',
    loadComponent: () => import('./accounts/account-list/account-list').then((m) => m.AccountList),
  },
  {
    path: 'cuentas/nuevo',
    title: 'Nueva cuenta',
    loadComponent: () => import('./accounts/account-form/account-form').then((m) => m.AccountForm),
  },
  {
    path: 'cuentas/:id',
    title: 'Editar cuenta',
    loadComponent: () => import('./accounts/account-form/account-form').then((m) => m.AccountForm),
  },
  {
    path: 'movimientos',
    title: 'Movimientos',
    loadComponent: () =>
      import('./transactions/transaction-list/transaction-list').then((m) => m.TransactionList),
  },
  {
    path: 'movimientos/nuevo',
    title: 'Nuevo movimiento',
    loadComponent: () =>
      import('./transactions/transaction-form/transaction-form').then((m) => m.TransactionForm),
  },
  {
    path: 'movimientos/:id',
    title: 'Editar movimiento',
    loadComponent: () =>
      import('./transactions/transaction-form/transaction-form').then((m) => m.TransactionForm),
  },
  {
    path: 'reportes',
    title: 'Reportes',
    loadComponent: () => import('./reports/report-page/report-page').then((m) => m.ReportPage),
  },
  { path: '**', redirectTo: 'clientes' },
];
