import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ICategoria } from 'app/entities/categoria/categoria.model';
import { ICuenta } from 'app/entities/cuenta/cuenta.model';
import { TipoMovimiento } from 'app/entities/enumerations/tipo-movimiento.model';

export interface IMovimiento {
  id: number;
  tipo?: keyof typeof TipoMovimiento | null;
  monto?: number | null;
  fechaMovimiento?: dayjs.Dayjs | null;
  fechaRegistro?: dayjs.Dayjs | null;
  fechaActualizacion?: dayjs.Dayjs | null;
  descripcion?: string | null;
  usuario?: Pick<IUser, 'id' | 'login'> | null;
  categoria?: Pick<ICategoria, 'id'> | null;
  cuenta?: Pick<ICuenta, 'id'> | null;
}

export type NewMovimiento = Omit<IMovimiento, 'id'> & { id: null };
