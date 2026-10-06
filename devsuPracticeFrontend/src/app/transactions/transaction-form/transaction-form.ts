import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AccountApi } from '../../api/account-api';
import { apiErrorMessage } from '../../api/api-error';
import { TransactionApi } from '../../api/transaction-api';
import { Account, ACCOUNT_TYPE_LABELS } from '../../models/account';
import { TransactionType, TRANSACTION_TYPE_LABELS } from '../../models/transaction';
import { showError, validationMessage } from '../../shared/forms/validation-message';

@Component({
  selector: 'app-transaction-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './transaction-form.html',
})
export class TransactionForm implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly transactionApi = inject(TransactionApi);
  private readonly accountApi = inject(AccountApi);

  protected readonly accountTypeLabels = ACCOUNT_TYPE_LABELS;
  protected readonly transactionTypeLabels = TRANSACTION_TYPE_LABELS;
  protected readonly transactionTypes = Object.keys(TRANSACTION_TYPE_LABELS) as TransactionType[];
  protected readonly accounts = signal<Account[]>([]);
  protected readonly transactionId = this.readIdFromRoute();
  protected readonly isEdit = this.transactionId !== null;
  protected readonly errorMessage = signal('');
  protected readonly saving = signal(false);

  protected readonly form = this.formBuilder.nonNullable.group({
    accountNumber: ['', Validators.required],
    transactionType: ['' as TransactionType | '', Validators.required],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
  });

  protected showError = showError;
  protected validationMessage = validationMessage;

  constructor() {
    if (this.isEdit) {
      this.form.controls.accountNumber.disable();
    }
  }

  ngOnInit() {
    this.accountApi.list().subscribe({
      next: (accounts) => this.accounts.set(accounts),
      error: (error) => this.errorMessage.set(apiErrorMessage(error)),
    });
    if (this.transactionId === null) {
      return;
    }
    this.transactionApi.get(this.transactionId).subscribe({
      next: (transaction) =>
        this.form.patchValue({
          accountNumber: transaction.accountNumber,
          transactionType: transaction.transactionType,
          amount: Math.abs(transaction.amount),
        }),
      error: (error) => this.errorMessage.set(apiErrorMessage(error)),
    });
  }

  protected save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const request = {
      transactionType: value.transactionType as TransactionType,
      amount: value.amount as number,
    };
    const call =
      this.transactionId === null
        ? this.transactionApi.create({ ...request, accountNumber: value.accountNumber })
        : this.transactionApi.update(this.transactionId, request);

    this.saving.set(true);
    call.subscribe({
      next: () => this.router.navigate(['/movimientos']),
      error: (error) => {
        this.errorMessage.set(apiErrorMessage(error));
        this.saving.set(false);
      },
    });
  }

  private readIdFromRoute(): number | null {
    const id = this.route.snapshot.paramMap.get('id');
    return id === null ? null : Number(id);
  }
}
