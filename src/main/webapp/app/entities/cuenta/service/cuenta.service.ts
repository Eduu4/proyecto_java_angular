import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICuenta, NewCuenta } from '../cuenta.model';

export type PartialUpdateCuenta = Partial<ICuenta> & Pick<ICuenta, 'id'>;

export type EntityResponseType = HttpResponse<ICuenta>;
export type EntityArrayResponseType = HttpResponse<ICuenta[]>;

@Injectable({ providedIn: 'root' })
export class CuentaService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cuentas');

  create(cuenta: NewCuenta): Observable<EntityResponseType> {
    return this.http.post<ICuenta>(this.resourceUrl, cuenta, { observe: 'response' });
  }

  update(cuenta: ICuenta): Observable<EntityResponseType> {
    return this.http.put<ICuenta>(`${this.resourceUrl}/${this.getCuentaIdentifier(cuenta)}`, cuenta, { observe: 'response' });
  }

  partialUpdate(cuenta: PartialUpdateCuenta): Observable<EntityResponseType> {
    return this.http.patch<ICuenta>(`${this.resourceUrl}/${this.getCuentaIdentifier(cuenta)}`, cuenta, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICuenta>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICuenta[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCuentaIdentifier(cuenta: Pick<ICuenta, 'id'>): number {
    return cuenta.id;
  }

  compareCuenta(o1: Pick<ICuenta, 'id'> | null, o2: Pick<ICuenta, 'id'> | null): boolean {
    return o1 && o2 ? this.getCuentaIdentifier(o1) === this.getCuentaIdentifier(o2) : o1 === o2;
  }

  addCuentaToCollectionIfMissing<Type extends Pick<ICuenta, 'id'>>(
    cuentaCollection: Type[],
    ...cuentasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cuentas: Type[] = cuentasToCheck.filter(isPresent);
    if (cuentas.length > 0) {
      const cuentaCollectionIdentifiers = cuentaCollection.map(cuentaItem => this.getCuentaIdentifier(cuentaItem));
      const cuentasToAdd = cuentas.filter(cuentaItem => {
        const cuentaIdentifier = this.getCuentaIdentifier(cuentaItem);
        if (cuentaCollectionIdentifiers.includes(cuentaIdentifier)) {
          return false;
        }
        cuentaCollectionIdentifiers.push(cuentaIdentifier);
        return true;
      });
      return [...cuentasToAdd, ...cuentaCollection];
    }
    return cuentaCollection;
  }
}
