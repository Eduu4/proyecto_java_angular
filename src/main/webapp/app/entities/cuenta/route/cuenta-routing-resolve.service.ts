import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICuenta } from '../cuenta.model';
import { CuentaService } from '../service/cuenta.service';

const cuentaResolve = (route: ActivatedRouteSnapshot): Observable<null | ICuenta> => {
  const id = route.params.id;
  if (id) {
    return inject(CuentaService)
      .find(id)
      .pipe(
        mergeMap((cuenta: HttpResponse<ICuenta>) => {
          if (cuenta.body) {
            return of(cuenta.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default cuentaResolve;
