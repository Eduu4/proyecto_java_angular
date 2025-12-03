import { IUser } from 'app/entities/user/user.model';

export interface ICuenta {
  id: number;
  nombre?: string | null;
  saldoInicial?: number | null;
  descripcion?: string | null;
  usuario?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewCuenta = Omit<ICuenta, 'id'> & { id: null };
