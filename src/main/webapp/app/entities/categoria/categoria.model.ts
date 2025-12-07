import { IUser } from 'app/entities/user/user.model';
import { TipoCategoria } from 'app/entities/enumerations/tipo-categoria.model';

export interface ICategoria {
  id: number;
  nombre?: string | null;
  tipo?: keyof typeof TipoCategoria | null;
  color?: string | null;
  usuario?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewCategoria = Omit<ICategoria, 'id'> & { id: null };
