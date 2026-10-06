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
