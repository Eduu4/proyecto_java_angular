import { ICuenta, NewCuenta } from './cuenta.model';

export const sampleWithRequiredData: ICuenta = {
  id: 8358,
  nombre: 'puppet',
  saldoInicial: 10057.69,
};

export const sampleWithPartialData: ICuenta = {
  id: 3287,
  nombre: 'promise soap',
  saldoInicial: 26235.44,
  descripcion: 'whoever aside',
};

export const sampleWithFullData: ICuenta = {
  id: 12978,
  nombre: 'yet',
  saldoInicial: 10814.42,
  descripcion: 'without and ironclad',
};

export const sampleWithNewData: NewCuenta = {
  nombre: 'quart hmph thunderbolt',
  saldoInicial: 8420.99,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
