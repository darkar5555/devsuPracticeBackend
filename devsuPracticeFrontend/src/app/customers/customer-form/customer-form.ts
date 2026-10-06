import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { apiErrorMessage } from '../../api/api-error';
import { CustomerApi } from '../../api/customer-api';
import { CustomerRequest, Gender, GENDER_LABELS } from '../../models/customer';
import { showError, validationMessage } from '../../shared/forms/validation-message';

@Component({
  selector: 'app-customer-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './customer-form.html',
})
export class CustomerForm implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly customerApi = inject(CustomerApi);

  protected readonly genderLabels = GENDER_LABELS;
  protected readonly genders = Object.keys(GENDER_LABELS) as Gender[];
  protected readonly customerId = this.readIdFromRoute();
  protected readonly isEdit = this.customerId !== null;
  protected readonly errorMessage = signal('');
  protected readonly saving = signal(false);

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

  ngOnInit() {
    if (this.customerId === null) {
      return;
    }
    this.customerApi.get(this.customerId).subscribe({
      next: (customer) => this.form.patchValue(customer),
      error: (error) => this.errorMessage.set(apiErrorMessage(error)),
    });
  }

  protected save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const request = this.toRequest();
    const call =
      this.customerId === null
        ? this.customerApi.create(request)
        : this.customerApi.update(this.customerId, {
            ...request,
            password: request.password || undefined,
          });

    this.saving.set(true);
    call.subscribe({
      next: () => this.router.navigate(['/clientes']),
      error: (error) => {
        this.errorMessage.set(apiErrorMessage(error));
        this.saving.set(false);
      },
    });
  }

  private toRequest(): CustomerRequest {
    const value = this.form.getRawValue();
    return { ...value, gender: value.gender as Gender, age: value.age as number };
  }

  private readIdFromRoute(): number | null {
    const id = this.route.snapshot.paramMap.get('id');
    return id === null ? null : Number(id);
  }
}
