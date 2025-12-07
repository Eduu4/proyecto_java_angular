import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'angularv3App.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'categoria',
    data: { pageTitle: 'angularv3App.categoria.home.title' },
    loadChildren: () => import('./categoria/categoria.routes'),
  },
  {
    path: 'cuenta',
    data: { pageTitle: 'angularv3App.cuenta.home.title' },
    loadChildren: () => import('./cuenta/cuenta.routes'),
  },
  {
    path: 'movimiento',
    data: { pageTitle: 'angularv3App.movimiento.home.title' },
    loadChildren: () => import('./movimiento/movimiento.routes'),
  },
  {
    path: 'presupuesto',
    data: { pageTitle: 'angularv3App.presupuesto.home.title' },
    loadChildren: () => import('./presupuesto/presupuesto.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
