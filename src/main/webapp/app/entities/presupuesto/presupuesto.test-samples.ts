import dayjs from 'dayjs/esm';

import { IPresupuesto, NewPresupuesto } from './presupuesto.model';

export const sampleWithRequiredData: IPresupuesto = {
  id: 19710,
  monto: 1606.94,
  periodo: 'MENSUAL',
  fechaInicio: dayjs('2025-12-02'),
  fechaFin: dayjs('2025-12-03'),
};

export const sampleWithPartialData: IPresupuesto = {
  id: 26185,
  monto: 13960.15,
  periodo: 'MENSUAL',
  fechaInicio: dayjs('2025-12-02'),
  fechaFin: dayjs('2025-12-02'),
};

export const sampleWithFullData: IPresupuesto = {
  id: 65,
  monto: 13668.33,
  periodo: 'MENSUAL',
  fechaInicio: dayjs('2025-12-02'),
  fechaFin: dayjs('2025-12-02'),
};

export const sampleWithNewData: NewPresupuesto = {
  monto: 16272.21,
  periodo: 'SEMANAL',
  fechaInicio: dayjs('2025-12-02'),
  fechaFin: dayjs('2025-12-03'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
