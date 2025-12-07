import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPresupuesto } from '../presupuesto.model';
import { PresupuestoService } from '../service/presupuesto.service';

const presupuestoResolve = (route: ActivatedRouteSnapshot): Observable<null | IPresupuesto> => {
  const id = route.params.id;
  if (id) {
    return inject(PresupuestoService)
      .find(id)
      .pipe(
        mergeMap((presupuesto: HttpResponse<IPresupuesto>) => {
          if (presupuesto.body) {
            return of(presupuesto.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default presupuestoResolve;
