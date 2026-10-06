import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AccountApi } from '../../api/account-api';
import { apiErrorMessage } from '../../api/api-error';
import { CustomerApi } from '../../api/customer-api';
import { AccountRequest, AccountType, ACCOUNT_TYPE_LABELS } from '../../models/account';
import { Customer } from '../../models/customer';
import { showError, validationMessage } from '../../shared/forms/validation-message';

@Component({
  selector: 'app-account-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './account-form.html',
})
export class AccountForm implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly accountApi = inject(AccountApi);
  private readonly customerApi = inject(CustomerApi);

  protected readonly accountTypeLabels = ACCOUNT_TYPE_LABELS;
  protected readonly accountTypes = Object.keys(ACCOUNT_TYPE_LABELS) as AccountType[];
  protected readonly customers = signal<Customer[]>([]);
  protected readonly accountId = this.readIdFromRoute();
  protected readonly isEdit = this.accountId !== null;
  protected readonly errorMessage = signal('');
  protected readonly saving = signal(false);

  protected readonly form = this.formBuilder.nonNullable.group({
    accountNumber: ['', [Validators.required, Validators.maxLength(20)]],
    accountType: ['' as AccountType | '', Validators.required],
    initialBalance: [null as number | null, [Validators.required, Validators.min(0)]],
    customerId: [null as number | null, Validators.required],
    status: [true],
  });

  protected showError = showError;
  protected validationMessage = validationMessage;

  constructor() {
    if (this.isEdit) {
      this.form.controls.customerId.disable();
    }
  }

  ngOnInit() {
    this.customerApi.list().subscribe({
      next: (customers) => this.customers.set(customers),
      error: (error) => this.errorMessage.set(apiErrorMessage(error)),
    });
    if (this.accountId === null) {
      return;
    }
    this.accountApi.get(this.accountId).subscribe({
      next: (account) =>
        this.form.patchValue({
          accountNumber: account.accountNumber,
          accountType: account.accountType,
          initialBalance: account.initialBalance,
          customerId: account.customerId,
          status: account.status,
        }),
      error: (error) => this.errorMessage.set(apiErrorMessage(error)),
    });
  }

  protected save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { customerId, ...request } = this.toRequest();
    const call =
      this.accountId === null
        ? this.accountApi.create({ ...request, customerId })
        : this.accountApi.update(this.accountId, request);

    this.saving.set(true);
    call.subscribe({
      next: () => this.router.navigate(['/cuentas']),
      error: (error) => {
        this.errorMessage.set(apiErrorMessage(error));
        this.saving.set(false);
      },
    });
  }

  private toRequest(): AccountRequest {
    const value = this.form.getRawValue();
    return {
      accountNumber: value.accountNumber,
      accountType: value.accountType as AccountType,
      initialBalance: value.initialBalance as number,
      status: value.status,
      customerId: value.customerId as number,
    };
  }

  private readIdFromRoute(): number | null {
    const id = this.route.snapshot.paramMap.get('id');
    return id === null ? null : Number(id);
  }
}
