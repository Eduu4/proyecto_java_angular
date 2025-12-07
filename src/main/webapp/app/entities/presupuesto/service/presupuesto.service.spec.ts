import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPresupuesto } from '../presupuesto.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../presupuesto.test-samples';

import { PresupuestoService, RestPresupuesto } from './presupuesto.service';

const requireRestSample: RestPresupuesto = {
  ...sampleWithRequiredData,
  fechaInicio: sampleWithRequiredData.fechaInicio?.format(DATE_FORMAT),
  fechaFin: sampleWithRequiredData.fechaFin?.format(DATE_FORMAT),
};

describe('Presupuesto Service', () => {
  let service: PresupuestoService;
  let httpMock: HttpTestingController;
  let expectedResult: IPresupuesto | IPresupuesto[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PresupuestoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Presupuesto', () => {
      const presupuesto = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(presupuesto).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Presupuesto', () => {
      const presupuesto = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(presupuesto).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Presupuesto', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Presupuesto', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Presupuesto', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPresupuestoToCollectionIfMissing', () => {
      it('should add a Presupuesto to an empty array', () => {
        const presupuesto: IPresupuesto = sampleWithRequiredData;
        expectedResult = service.addPresupuestoToCollectionIfMissing([], presupuesto);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(presupuesto);
      });

      it('should not add a Presupuesto to an array that contains it', () => {
        const presupuesto: IPresupuesto = sampleWithRequiredData;
        const presupuestoCollection: IPresupuesto[] = [
          {
            ...presupuesto,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPresupuestoToCollectionIfMissing(presupuestoCollection, presupuesto);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Presupuesto to an array that doesn't contain it", () => {
        const presupuesto: IPresupuesto = sampleWithRequiredData;
        const presupuestoCollection: IPresupuesto[] = [sampleWithPartialData];
        expectedResult = service.addPresupuestoToCollectionIfMissing(presupuestoCollection, presupuesto);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(presupuesto);
      });

      it('should add only unique Presupuesto to an array', () => {
        const presupuestoArray: IPresupuesto[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const presupuestoCollection: IPresupuesto[] = [sampleWithRequiredData];
        expectedResult = service.addPresupuestoToCollectionIfMissing(presupuestoCollection, ...presupuestoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const presupuesto: IPresupuesto = sampleWithRequiredData;
        const presupuesto2: IPresupuesto = sampleWithPartialData;
        expectedResult = service.addPresupuestoToCollectionIfMissing([], presupuesto, presupuesto2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(presupuesto);
        expect(expectedResult).toContain(presupuesto2);
      });

      it('should accept null and undefined values', () => {
        const presupuesto: IPresupuesto = sampleWithRequiredData;
        expectedResult = service.addPresupuestoToCollectionIfMissing([], null, presupuesto, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(presupuesto);
      });

      it('should return initial array if no Presupuesto is added', () => {
        const presupuestoCollection: IPresupuesto[] = [sampleWithRequiredData];
        expectedResult = service.addPresupuestoToCollectionIfMissing(presupuestoCollection, undefined, null);
        expect(expectedResult).toEqual(presupuestoCollection);
      });
    });

    describe('comparePresupuesto', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePresupuesto(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 6207 };
        const entity2 = null;

        const compareResult1 = service.comparePresupuesto(entity1, entity2);
        const compareResult2 = service.comparePresupuesto(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 6207 };
        const entity2 = { id: 12045 };

        const compareResult1 = service.comparePresupuesto(entity1, entity2);
        const compareResult2 = service.comparePresupuesto(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 6207 };
        const entity2 = { id: 6207 };

        const compareResult1 = service.comparePresupuesto(entity1, entity2);
        const compareResult2 = service.comparePresupuesto(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
