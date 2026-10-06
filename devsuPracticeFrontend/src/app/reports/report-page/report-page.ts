import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { apiErrorMessage } from '../../api/api-error';
import { CustomerApi } from '../../api/customer-api';
import { ReportApi } from '../../api/report-api';
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
export class ReportPage implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly customerApi = inject(CustomerApi);
  private readonly reportApi = inject(ReportApi);

  protected readonly accountTypeLabels = ACCOUNT_TYPE_LABELS;
  protected readonly transactionTypeLabels = TRANSACTION_TYPE_LABELS;
  protected readonly customers = signal<Customer[]>([]);
  protected readonly statement = signal<AccountStatement | null>(null);
  protected readonly errorMessage = signal('');
  protected readonly generating = signal(false);

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

  ngOnInit() {
    this.customerApi.list().subscribe({
      next: (customers) => this.customers.set(customers),
      error: (error) => this.errorMessage.set(apiErrorMessage(error)),
    });
  }

  protected generate() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { customerId, from, to } = this.form.getRawValue();
    this.generating.set(true);
    this.errorMessage.set('');
    this.reportApi.accountStatement(customerId as number, from, to).subscribe({
      next: (statement) => {
        this.statement.set(statement);
        this.generating.set(false);
      },
      error: (error) => {
        this.statement.set(null);
        this.errorMessage.set(apiErrorMessage(error));
        this.generating.set(false);
      },
    });
  }

  protected downloadPdf(statement: AccountStatement) {
    const bytes = Uint8Array.from(atob(statement.pdfBase64), (char) => char.charCodeAt(0));
    const url = URL.createObjectURL(new Blob([bytes], { type: 'application/pdf' }));
    const link = document.createElement('a');
    link.href = url;
    link.download = `estado-de-cuenta-${statement.customer.identification}.pdf`;
    link.click();
    URL.revokeObjectURL(url);
  }
}
