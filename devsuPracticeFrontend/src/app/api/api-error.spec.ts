import { HttpErrorResponse } from '@angular/common/http';
import { apiErrorMessage } from './api-error';

describe('apiErrorMessage', () => {
  it('uses the detail of a problem response', () => {
    const error = new HttpErrorResponse({ status: 422, error: { detail: 'Saldo no disponible' } });
    expect(apiErrorMessage(error)).toBe('Saldo no disponible');
  });

  it('lists field errors of a validation response', () => {
    const error = new HttpErrorResponse({
      status: 400,
      error: { detail: 'Validation failed', errors: { name: 'must not be blank' } },
    });
    expect(apiErrorMessage(error)).toBe('name: must not be blank');
  });

  it('explains a connection failure', () => {
    expect(apiErrorMessage(new HttpErrorResponse({ status: 0 }))).toBe(
      'No se pudo conectar con el servidor.',
    );
  });
});
