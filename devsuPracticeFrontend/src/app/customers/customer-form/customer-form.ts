import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Gender, GENDER_LABELS } from '../../models/customer';
import { showError, validationMessage } from '../../shared/forms/validation-message';

@Component({
  selector: 'app-customer-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './customer-form.html',
})
export class CustomerForm {
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly genderLabels = GENDER_LABELS;
  protected readonly genders = Object.keys(GENDER_LABELS) as Gender[];
  protected readonly customerId = this.route.snapshot.paramMap.get('id');
  protected readonly isEdit = this.customerId !== null;

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    gender: ['' as Gender | '', Validators.required],
    age: [null as number | null, [Validators.required, Validators.min(0), Validators.max(150)]],
    identification: ['', [Validators.required, Validators.maxLength(20)]],
    address: ['', [Validators.required, Validators.maxLength(200)]],
    phone: ['', [Validators.required, Validators.maxLength(20)]],
    password: [
      '',
      this.isEdit ? [Validators.minLength(4)] : [Validators.required, Validators.minLength(4)],
    ],
    status: [true],
  });

  protected showError = showError;
  protected validationMessage = validationMessage;

  protected save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.router.navigate(['/clientes']);
  }
}
