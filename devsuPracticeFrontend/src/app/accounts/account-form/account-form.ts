import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AccountType, ACCOUNT_TYPE_LABELS } from '../../models/account';
import { Customer } from '../../models/customer';
import { showError, validationMessage } from '../../shared/forms/validation-message';

@Component({
  selector: 'app-account-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './account-form.html',
})
export class AccountForm {
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly accountTypeLabels = ACCOUNT_TYPE_LABELS;
  protected readonly accountTypes = Object.keys(ACCOUNT_TYPE_LABELS) as AccountType[];
  protected readonly customers: Customer[] = [];
  protected readonly accountId = this.route.snapshot.paramMap.get('id');
  protected readonly isEdit = this.accountId !== null;

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

  protected save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.router.navigate(['/cuentas']);
  }
}
