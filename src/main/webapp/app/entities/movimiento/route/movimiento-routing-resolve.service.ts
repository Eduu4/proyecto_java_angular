import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMovimiento } from '../movimiento.model';
import { MovimientoService } from '../service/movimiento.service';

const movimientoResolve = (route: ActivatedRouteSnapshot): Observable<null | IMovimiento> => {
  const id = route.params.id;
  if (id) {
    return inject(MovimientoService)
      .find(id)
      .pipe(
        mergeMap((movimiento: HttpResponse<IMovimiento>) => {
          if (movimiento.body) {
            return of(movimiento.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default movimientoResolve;
