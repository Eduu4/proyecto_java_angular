import { Routes } from '@angular/router';

import { Authority } from 'app/config/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { errorRoute } from './layouts/error/error.route';

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./home/home.component'),
    title: 'home.title',
  },
  {
    path: 'proceso-principal',
    loadComponent: () => import('./app/proceso-principal/proceso-principal.component').then(m => m.ProcesoPrincipalComponent),
    title: 'Gestión de Movimientos',
  },
  {
    path: 'whatsapp-registro',
    loadComponent: () => import('./app/whatsapp-registro/whatsapp-registro.component').then(m => m.WhatsappRegistroComponent),
    title: 'Registrar por WhatsApp',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'whatsapp-test',
    loadComponent: () => import('./app/whatsapp-test/whatsapp-test.component').then(m => m.WhatsappTestComponent),
    title: 'Prueba de Webhook WhatsApp',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar.component'),
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login.component'),
    title: 'login.title',
  },
  {
    path: '',
    loadChildren: () => import(`./entities/entity.routes`),
  },
  ...errorRoute,
];

export default routes;
