import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ICategoria } from 'app/entities/categoria/categoria.model';
import { CategoriaService } from 'app/entities/categoria/service/categoria.service';
import { ICuenta } from 'app/entities/cuenta/cuenta.model';
import { CuentaService } from 'app/entities/cuenta/service/cuenta.service';
import { IMovimiento } from '../movimiento.model';
import { MovimientoService } from '../service/movimiento.service';
import { MovimientoFormService } from './movimiento-form.service';

import { MovimientoUpdateComponent } from './movimiento-update.component';

describe('Movimiento Management Update Component', () => {
  let comp: MovimientoUpdateComponent;
  let fixture: ComponentFixture<MovimientoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let movimientoFormService: MovimientoFormService;
  let movimientoService: MovimientoService;
  let userService: UserService;
  let categoriaService: CategoriaService;
  let cuentaService: CuentaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MovimientoUpdateComponent],
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
      .overrideTemplate(MovimientoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MovimientoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    movimientoFormService = TestBed.inject(MovimientoFormService);
    movimientoService = TestBed.inject(MovimientoService);
    userService = TestBed.inject(UserService);
    categoriaService = TestBed.inject(CategoriaService);
    cuentaService = TestBed.inject(CuentaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const movimiento: IMovimiento = { id: 13928 };
      const usuario: IUser = { id: 3944 };
      movimiento.usuario = usuario;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [usuario];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ movimiento });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call Categoria query and add missing value', () => {
      const movimiento: IMovimiento = { id: 13928 };
      const categoria: ICategoria = { id: 24962 };
      movimiento.categoria = categoria;

      const categoriaCollection: ICategoria[] = [{ id: 24962 }];
      jest.spyOn(categoriaService, 'query').mockReturnValue(of(new HttpResponse({ body: categoriaCollection })));
      const additionalCategorias = [categoria];
      const expectedCollection: ICategoria[] = [...additionalCategorias, ...categoriaCollection];
      jest.spyOn(categoriaService, 'addCategoriaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ movimiento });
      comp.ngOnInit();

      expect(categoriaService.query).toHaveBeenCalled();
      expect(categoriaService.addCategoriaToCollectionIfMissing).toHaveBeenCalledWith(
        categoriaCollection,
        ...additionalCategorias.map(expect.objectContaining),
      );
      expect(comp.categoriasSharedCollection).toEqual(expectedCollection);
    });

    it('should call Cuenta query and add missing value', () => {
      const movimiento: IMovimiento = { id: 13928 };
      const cuenta: ICuenta = { id: 32472 };
      movimiento.cuenta = cuenta;

      const cuentaCollection: ICuenta[] = [{ id: 32472 }];
      jest.spyOn(cuentaService, 'query').mockReturnValue(of(new HttpResponse({ body: cuentaCollection })));
      const additionalCuentas = [cuenta];
      const expectedCollection: ICuenta[] = [...additionalCuentas, ...cuentaCollection];
      jest.spyOn(cuentaService, 'addCuentaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ movimiento });
      comp.ngOnInit();

      expect(cuentaService.query).toHaveBeenCalled();
      expect(cuentaService.addCuentaToCollectionIfMissing).toHaveBeenCalledWith(
        cuentaCollection,
        ...additionalCuentas.map(expect.objectContaining),
      );
      expect(comp.cuentasSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const movimiento: IMovimiento = { id: 13928 };
      const usuario: IUser = { id: 3944 };
      movimiento.usuario = usuario;
      const categoria: ICategoria = { id: 24962 };
      movimiento.categoria = categoria;
      const cuenta: ICuenta = { id: 32472 };
      movimiento.cuenta = cuenta;

      activatedRoute.data = of({ movimiento });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(usuario);
      expect(comp.categoriasSharedCollection).toContainEqual(categoria);
      expect(comp.cuentasSharedCollection).toContainEqual(cuenta);
      expect(comp.movimiento).toEqual(movimiento);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMovimiento>>();
      const movimiento = { id: 30126 };
      jest.spyOn(movimientoFormService, 'getMovimiento').mockReturnValue(movimiento);
      jest.spyOn(movimientoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ movimiento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: movimiento }));
      saveSubject.complete();

      // THEN
      expect(movimientoFormService.getMovimiento).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(movimientoService.update).toHaveBeenCalledWith(expect.objectContaining(movimiento));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMovimiento>>();
      const movimiento = { id: 30126 };
      jest.spyOn(movimientoFormService, 'getMovimiento').mockReturnValue({ id: null });
      jest.spyOn(movimientoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ movimiento: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: movimiento }));
      saveSubject.complete();

      // THEN
      expect(movimientoFormService.getMovimiento).toHaveBeenCalled();
      expect(movimientoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMovimiento>>();
      const movimiento = { id: 30126 };
      jest.spyOn(movimientoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ movimiento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(movimientoService.update).toHaveBeenCalled();
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

    describe('compareCategoria', () => {
      it('should forward to categoriaService', () => {
        const entity = { id: 24962 };
        const entity2 = { id: 11537 };
        jest.spyOn(categoriaService, 'compareCategoria');
        comp.compareCategoria(entity, entity2);
        expect(categoriaService.compareCategoria).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCuenta', () => {
      it('should forward to cuentaService', () => {
        const entity = { id: 32472 };
        const entity2 = { id: 10863 };
        jest.spyOn(cuentaService, 'compareCuenta');
        comp.compareCuenta(entity, entity2);
        expect(cuentaService.compareCuenta).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
