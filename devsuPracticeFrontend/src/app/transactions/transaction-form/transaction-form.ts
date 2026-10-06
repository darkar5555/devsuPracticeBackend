import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Account, ACCOUNT_TYPE_LABELS } from '../../models/account';
import { TransactionType, TRANSACTION_TYPE_LABELS } from '../../models/transaction';
import { showError, validationMessage } from '../../shared/forms/validation-message';

@Component({
  selector: 'app-transaction-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './transaction-form.html',
})
export class TransactionForm {
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly accountTypeLabels = ACCOUNT_TYPE_LABELS;
  protected readonly transactionTypeLabels = TRANSACTION_TYPE_LABELS;
  protected readonly transactionTypes = Object.keys(TRANSACTION_TYPE_LABELS) as TransactionType[];
  protected readonly accounts: Account[] = [];
  protected readonly transactionId = this.route.snapshot.paramMap.get('id');
  protected readonly isEdit = this.transactionId !== null;

  protected readonly form = this.formBuilder.nonNullable.group({
    accountNumber: ['', Validators.required],
    transactionType: ['' as TransactionType | '', Validators.required],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
  });

  protected showError = showError;
  protected validationMessage = validationMessage;

  protected save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.router.navigate(['/movimientos']);
  }
}
