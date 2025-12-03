import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICuenta } from '../cuenta.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../cuenta.test-samples';

import { CuentaService } from './cuenta.service';

const requireRestSample: ICuenta = {
  ...sampleWithRequiredData,
};

describe('Cuenta Service', () => {
  let service: CuentaService;
  let httpMock: HttpTestingController;
  let expectedResult: ICuenta | ICuenta[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CuentaService);
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

    it('should create a Cuenta', () => {
      const cuenta = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cuenta).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Cuenta', () => {
      const cuenta = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cuenta).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Cuenta', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Cuenta', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Cuenta', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCuentaToCollectionIfMissing', () => {
      it('should add a Cuenta to an empty array', () => {
        const cuenta: ICuenta = sampleWithRequiredData;
        expectedResult = service.addCuentaToCollectionIfMissing([], cuenta);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cuenta);
      });

      it('should not add a Cuenta to an array that contains it', () => {
        const cuenta: ICuenta = sampleWithRequiredData;
        const cuentaCollection: ICuenta[] = [
          {
            ...cuenta,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCuentaToCollectionIfMissing(cuentaCollection, cuenta);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Cuenta to an array that doesn't contain it", () => {
        const cuenta: ICuenta = sampleWithRequiredData;
        const cuentaCollection: ICuenta[] = [sampleWithPartialData];
        expectedResult = service.addCuentaToCollectionIfMissing(cuentaCollection, cuenta);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cuenta);
      });

      it('should add only unique Cuenta to an array', () => {
        const cuentaArray: ICuenta[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cuentaCollection: ICuenta[] = [sampleWithRequiredData];
        expectedResult = service.addCuentaToCollectionIfMissing(cuentaCollection, ...cuentaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cuenta: ICuenta = sampleWithRequiredData;
        const cuenta2: ICuenta = sampleWithPartialData;
        expectedResult = service.addCuentaToCollectionIfMissing([], cuenta, cuenta2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cuenta);
        expect(expectedResult).toContain(cuenta2);
      });

      it('should accept null and undefined values', () => {
        const cuenta: ICuenta = sampleWithRequiredData;
        expectedResult = service.addCuentaToCollectionIfMissing([], null, cuenta, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cuenta);
      });

      it('should return initial array if no Cuenta is added', () => {
        const cuentaCollection: ICuenta[] = [sampleWithRequiredData];
        expectedResult = service.addCuentaToCollectionIfMissing(cuentaCollection, undefined, null);
        expect(expectedResult).toEqual(cuentaCollection);
      });
    });

    describe('compareCuenta', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCuenta(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 32472 };
        const entity2 = null;

        const compareResult1 = service.compareCuenta(entity1, entity2);
        const compareResult2 = service.compareCuenta(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 32472 };
        const entity2 = { id: 10863 };

        const compareResult1 = service.compareCuenta(entity1, entity2);
        const compareResult2 = service.compareCuenta(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 32472 };
        const entity2 = { id: 32472 };

        const compareResult1 = service.compareCuenta(entity1, entity2);
        const compareResult2 = service.compareCuenta(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
