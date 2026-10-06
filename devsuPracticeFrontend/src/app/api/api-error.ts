import { HttpErrorResponse } from '@angular/common/http';

interface ProblemDetail {
  detail?: string;
  errors?: Record<string, string>;
}

export function apiErrorMessage(error: unknown): string {
  if (!(error instanceof HttpErrorResponse)) {
    return 'Ocurrió un error inesperado.';
  }
  if (error.status === 0) {
    return 'No se pudo conectar con el servidor.';
  }
  const problem = error.error as ProblemDetail | null;
  if (problem?.errors) {
    return Object.entries(problem.errors)
      .map(([field, message]) => `${field}: ${message}`)
      .join('. ');
  }
  return problem?.detail || `Error ${error.status}`;
}
