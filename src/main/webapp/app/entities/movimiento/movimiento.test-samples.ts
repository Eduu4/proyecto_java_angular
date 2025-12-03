import dayjs from 'dayjs/esm';

import { IMovimiento, NewMovimiento } from './movimiento.model';

export const sampleWithRequiredData: IMovimiento = {
  id: 29718,
  tipo: 'GASTO',
  monto: 3687.22,
  fechaMovimiento: dayjs('2025-12-02T14:32'),
  fechaRegistro: dayjs('2025-12-02T23:10'),
};

export const sampleWithPartialData: IMovimiento = {
  id: 17962,
  tipo: 'GASTO',
  monto: 2550.6,
  fechaMovimiento: dayjs('2025-12-03T00:31'),
  fechaRegistro: dayjs('2025-12-02T21:30'),
  fechaActualizacion: dayjs('2025-12-02T16:27'),
};

export const sampleWithFullData: IMovimiento = {
  id: 11194,
  tipo: 'INGRESO',
  monto: 23582.37,
  fechaMovimiento: dayjs('2025-12-02T03:14'),
  fechaRegistro: dayjs('2025-12-02T19:51'),
  fechaActualizacion: dayjs('2025-12-02T03:09'),
  descripcion: 'dereference woot',
};

export const sampleWithNewData: NewMovimiento = {
  tipo: 'GASTO',
  monto: 1202.17,
  fechaMovimiento: dayjs('2025-12-02T18:33'),
  fechaRegistro: dayjs('2025-12-02T03:02'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
