import { FormControl, Validators } from '@angular/forms';
import { showError, validationMessage } from './validation-message';

describe('validationMessage', () => {
  it('returns an empty string for a valid control', () => {
    expect(validationMessage(new FormControl('ok'))).toBe('');
  });

  it('describes a required field', () => {
    expect(validationMessage(new FormControl('', Validators.required))).toBe(
      'Este campo es obligatorio.',
    );
  });

  it('includes the limit for a minimum value', () => {
    expect(validationMessage(new FormControl(0, Validators.min(0.01)))).toBe(
      'El valor mínimo es 0.01.',
    );
  });
});

describe('showError', () => {
  it('only shows errors once the control was touched or changed', () => {
    const control = new FormControl('', Validators.required);
    expect(showError(control)).toBe(false);
    control.markAsTouched();
    expect(showError(control)).toBe(true);
  });
});
