import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CuentaResolve from './route/cuenta-routing-resolve.service';

const cuentaRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/cuenta.component').then(m => m.CuentaComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/cuenta-detail.component').then(m => m.CuentaDetailComponent),
    resolve: {
      cuenta: CuentaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/cuenta-update.component').then(m => m.CuentaUpdateComponent),
    resolve: {
      cuenta: CuentaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/cuenta-update.component').then(m => m.CuentaUpdateComponent),
    resolve: {
      cuenta: CuentaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cuentaRoute;
