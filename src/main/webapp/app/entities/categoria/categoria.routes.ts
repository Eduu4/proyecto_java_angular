import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CategoriaResolve from './route/categoria-routing-resolve.service';

const categoriaRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/categoria.component').then(m => m.CategoriaComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/categoria-detail.component').then(m => m.CategoriaDetailComponent),
    resolve: {
      categoria: CategoriaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/categoria-update.component').then(m => m.CategoriaUpdateComponent),
    resolve: {
      categoria: CategoriaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/categoria-update.component').then(m => m.CategoriaUpdateComponent),
    resolve: {
      categoria: CategoriaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default categoriaRoute;
