import { ICategoria, NewCategoria } from './categoria.model';

export const sampleWithRequiredData: ICategoria = {
  id: 16672,
  nombre: 'clavicle',
  tipo: 'INGRESO',
};

export const sampleWithPartialData: ICategoria = {
  id: 25718,
  nombre: 'tough ouch while',
  tipo: 'GASTO',
};

export const sampleWithFullData: ICategoria = {
  id: 12751,
  nombre: 'what unlucky yowza',
  tipo: 'GASTO',
  color: 'teal',
};

export const sampleWithNewData: NewCategoria = {
  nombre: 'unbalance delightfully like',
  tipo: 'GASTO',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
