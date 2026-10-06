import { AbstractControl } from '@angular/forms';

// Validations for form and we use it to display to the user.
export function validationMessage(control: AbstractControl | null): string {
  const errors = control?.errors;
  if (!errors) {
    return '';
  }
  if (errors['required']) {
    return 'Este campo es obligatorio.';
  }
  if (errors['minlength']) {
    return `Debe tener al menos ${errors['minlength'].requiredLength} caracteres.`;
  }
  if (errors['maxlength']) {
    return `Debe tener como máximo ${errors['maxlength'].requiredLength} caracteres.`;
  }
  if (errors['min']) {
    return `El valor mínimo es ${errors['min'].min}.`;
  }
  if (errors['max']) {
    return `El valor máximo es ${errors['max'].max}.`;
  }
  return 'El valor no es válido.';
}

export function showError(control: AbstractControl | null): boolean {
  return !!control && control.invalid && (control.touched || control.dirty);
}
