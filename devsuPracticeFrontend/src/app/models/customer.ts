export type Gender = 'MALE' | 'FEMALE' | 'OTHER';

export const GENDER_LABELS: Record<Gender, string> = {
  MALE: 'Masculino',
  FEMALE: 'Femenino',
  OTHER: 'Otro',
};

export interface Customer {
  id: number;
  name: string;
  gender: Gender;
  age: number;
  identification: string;
  address: string;
  phone: string;
  status: boolean;
}

export interface CustomerRequest {
  name: string;
  gender: Gender;
  age: number;
  identification: string;
  address: string;
  phone: string;
  password: string;
  status: boolean;
}

export interface CustomerUpdateRequest extends Omit<CustomerRequest, 'password'> {
  password?: string;
}
