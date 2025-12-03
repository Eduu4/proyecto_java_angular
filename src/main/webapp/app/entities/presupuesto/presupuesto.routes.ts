import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PresupuestoResolve from './route/presupuesto-routing-resolve.service';

const presupuestoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/presupuesto.component').then(m => m.PresupuestoComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/presupuesto-detail.component').then(m => m.PresupuestoDetailComponent),
    resolve: {
      presupuesto: PresupuestoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/presupuesto-update.component').then(m => m.PresupuestoUpdateComponent),
    resolve: {
      presupuesto: PresupuestoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/presupuesto-update.component').then(m => m.PresupuestoUpdateComponent),
    resolve: {
      presupuesto: PresupuestoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default presupuestoRoute;
