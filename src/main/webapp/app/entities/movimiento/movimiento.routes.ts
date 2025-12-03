import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MovimientoResolve from './route/movimiento-routing-resolve.service';

const movimientoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/movimiento.component').then(m => m.MovimientoComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/movimiento-detail.component').then(m => m.MovimientoDetailComponent),
    resolve: {
      movimiento: MovimientoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/movimiento-update.component').then(m => m.MovimientoUpdateComponent),
    resolve: {
      movimiento: MovimientoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/movimiento-update.component').then(m => m.MovimientoUpdateComponent),
    resolve: {
      movimiento: MovimientoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default movimientoRoute;
