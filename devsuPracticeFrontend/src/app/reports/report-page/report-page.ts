import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ACCOUNT_TYPE_LABELS } from '../../models/account';
import { AccountStatement } from '../../models/account-statement';
import { Customer } from '../../models/customer';
import { TRANSACTION_TYPE_LABELS } from '../../models/transaction';
import { showError, validationMessage } from '../../shared/forms/validation-message';

function dateRangeValidator(group: AbstractControl): ValidationErrors | null {
  const { from, to } = group.value;
  return from && to && from > to ? { dateRange: true } : null;
}

@Component({
  selector: 'app-report-page',
  imports: [ReactiveFormsModule, DatePipe, DecimalPipe],
  templateUrl: './report-page.html',
})
export class ReportPage {
  private readonly formBuilder = inject(FormBuilder);

  protected readonly accountTypeLabels = ACCOUNT_TYPE_LABELS;
  protected readonly transactionTypeLabels = TRANSACTION_TYPE_LABELS;
  protected readonly customers: Customer[] = [];
  protected readonly statement = signal<AccountStatement | null>(null);

  protected readonly form = this.formBuilder.nonNullable.group(
    {
      customerId: [null as number | null, Validators.required],
      from: ['', Validators.required],
      to: ['', Validators.required],
    },
    { validators: dateRangeValidator },
  );

  protected showError = showError;
  protected validationMessage = validationMessage;

  protected generate() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
  }
}
