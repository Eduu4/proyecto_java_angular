import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ICategoria } from 'app/entities/categoria/categoria.model';
import { PeriodoPresupuesto } from 'app/entities/enumerations/periodo-presupuesto.model';

export interface IPresupuesto {
  id: number;
  monto?: number | null;
  periodo?: keyof typeof PeriodoPresupuesto | null;
  fechaInicio?: dayjs.Dayjs | null;
  fechaFin?: dayjs.Dayjs | null;
  usuario?: Pick<IUser, 'id' | 'login'> | null;
  categoria?: Pick<ICategoria, 'id'> | null;
}

export type NewPresupuesto = Omit<IPresupuesto, 'id'> & { id: null };
