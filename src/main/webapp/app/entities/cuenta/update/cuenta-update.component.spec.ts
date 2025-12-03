import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { CuentaService } from '../service/cuenta.service';
import { ICuenta } from '../cuenta.model';
import { CuentaFormService } from './cuenta-form.service';

import { CuentaUpdateComponent } from './cuenta-update.component';

describe('Cuenta Management Update Component', () => {
  let comp: CuentaUpdateComponent;
  let fixture: ComponentFixture<CuentaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cuentaFormService: CuentaFormService;
  let cuentaService: CuentaService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CuentaUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CuentaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CuentaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cuentaFormService = TestBed.inject(CuentaFormService);
    cuentaService = TestBed.inject(CuentaService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const cuenta: ICuenta = { id: 10863 };
      const usuario: IUser = { id: 3944 };
      cuenta.usuario = usuario;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [usuario];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cuenta });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const cuenta: ICuenta = { id: 10863 };
      const usuario: IUser = { id: 3944 };
      cuenta.usuario = usuario;

      activatedRoute.data = of({ cuenta });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(usuario);
      expect(comp.cuenta).toEqual(cuenta);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICuenta>>();
      const cuenta = { id: 32472 };
      jest.spyOn(cuentaFormService, 'getCuenta').mockReturnValue(cuenta);
      jest.spyOn(cuentaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cuenta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cuenta }));
      saveSubject.complete();

      // THEN
      expect(cuentaFormService.getCuenta).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cuentaService.update).toHaveBeenCalledWith(expect.objectContaining(cuenta));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICuenta>>();
      const cuenta = { id: 32472 };
      jest.spyOn(cuentaFormService, 'getCuenta').mockReturnValue({ id: null });
      jest.spyOn(cuentaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cuenta: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cuenta }));
      saveSubject.complete();

      // THEN
      expect(cuentaFormService.getCuenta).toHaveBeenCalled();
      expect(cuentaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICuenta>>();
      const cuenta = { id: 32472 };
      jest.spyOn(cuentaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cuenta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cuentaService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
