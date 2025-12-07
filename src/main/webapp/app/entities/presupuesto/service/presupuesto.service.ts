import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPresupuesto, NewPresupuesto } from '../presupuesto.model';

export type PartialUpdatePresupuesto = Partial<IPresupuesto> & Pick<IPresupuesto, 'id'>;

type RestOf<T extends IPresupuesto | NewPresupuesto> = Omit<T, 'fechaInicio' | 'fechaFin'> & {
  fechaInicio?: string | null;
  fechaFin?: string | null;
};

export type RestPresupuesto = RestOf<IPresupuesto>;

export type NewRestPresupuesto = RestOf<NewPresupuesto>;

export type PartialUpdateRestPresupuesto = RestOf<PartialUpdatePresupuesto>;

export type EntityResponseType = HttpResponse<IPresupuesto>;
export type EntityArrayResponseType = HttpResponse<IPresupuesto[]>;

@Injectable({ providedIn: 'root' })
export class PresupuestoService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/presupuestos');

  create(presupuesto: NewPresupuesto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(presupuesto);
    return this.http
      .post<RestPresupuesto>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(presupuesto: IPresupuesto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(presupuesto);
    return this.http
      .put<RestPresupuesto>(`${this.resourceUrl}/${this.getPresupuestoIdentifier(presupuesto)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(presupuesto: PartialUpdatePresupuesto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(presupuesto);
    return this.http
      .patch<RestPresupuesto>(`${this.resourceUrl}/${this.getPresupuestoIdentifier(presupuesto)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPresupuesto>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPresupuesto[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPresupuestoIdentifier(presupuesto: Pick<IPresupuesto, 'id'>): number {
    return presupuesto.id;
  }

  comparePresupuesto(o1: Pick<IPresupuesto, 'id'> | null, o2: Pick<IPresupuesto, 'id'> | null): boolean {
    return o1 && o2 ? this.getPresupuestoIdentifier(o1) === this.getPresupuestoIdentifier(o2) : o1 === o2;
  }

  addPresupuestoToCollectionIfMissing<Type extends Pick<IPresupuesto, 'id'>>(
    presupuestoCollection: Type[],
    ...presupuestosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const presupuestos: Type[] = presupuestosToCheck.filter(isPresent);
    if (presupuestos.length > 0) {
      const presupuestoCollectionIdentifiers = presupuestoCollection.map(presupuestoItem => this.getPresupuestoIdentifier(presupuestoItem));
      const presupuestosToAdd = presupuestos.filter(presupuestoItem => {
        const presupuestoIdentifier = this.getPresupuestoIdentifier(presupuestoItem);
        if (presupuestoCollectionIdentifiers.includes(presupuestoIdentifier)) {
          return false;
        }
        presupuestoCollectionIdentifiers.push(presupuestoIdentifier);
        return true;
      });
      return [...presupuestosToAdd, ...presupuestoCollection];
    }
    return presupuestoCollection;
  }

  protected convertDateFromClient<T extends IPresupuesto | NewPresupuesto | PartialUpdatePresupuesto>(presupuesto: T): RestOf<T> {
    return {
      ...presupuesto,
      fechaInicio: presupuesto.fechaInicio?.format(DATE_FORMAT) ?? null,
      fechaFin: presupuesto.fechaFin?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restPresupuesto: RestPresupuesto): IPresupuesto {
    return {
      ...restPresupuesto,
      fechaInicio: restPresupuesto.fechaInicio ? dayjs(restPresupuesto.fechaInicio) : undefined,
      fechaFin: restPresupuesto.fechaFin ? dayjs(restPresupuesto.fechaFin) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPresupuesto>): HttpResponse<IPresupuesto> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPresupuesto[]>): HttpResponse<IPresupuesto[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
