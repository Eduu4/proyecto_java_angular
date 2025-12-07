import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ICategoria } from 'app/entities/categoria/categoria.model';
import { CategoriaService } from 'app/entities/categoria/service/categoria.service';
import { IPresupuesto } from '../presupuesto.model';
import { PresupuestoService } from '../service/presupuesto.service';
import { PresupuestoFormService } from './presupuesto-form.service';

import { PresupuestoUpdateComponent } from './presupuesto-update.component';

describe('Presupuesto Management Update Component', () => {
  let comp: PresupuestoUpdateComponent;
  let fixture: ComponentFixture<PresupuestoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let presupuestoFormService: PresupuestoFormService;
  let presupuestoService: PresupuestoService;
  let userService: UserService;
  let categoriaService: CategoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PresupuestoUpdateComponent],
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
      .overrideTemplate(PresupuestoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PresupuestoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    presupuestoFormService = TestBed.inject(PresupuestoFormService);
    presupuestoService = TestBed.inject(PresupuestoService);
    userService = TestBed.inject(UserService);
    categoriaService = TestBed.inject(CategoriaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const presupuesto: IPresupuesto = { id: 12045 };
      const usuario: IUser = { id: 3944 };
      presupuesto.usuario = usuario;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [usuario];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ presupuesto });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call Categoria query and add missing value', () => {
      const presupuesto: IPresupuesto = { id: 12045 };
      const categoria: ICategoria = { id: 24962 };
      presupuesto.categoria = categoria;

      const categoriaCollection: ICategoria[] = [{ id: 24962 }];
      jest.spyOn(categoriaService, 'query').mockReturnValue(of(new HttpResponse({ body: categoriaCollection })));
      const additionalCategorias = [categoria];
      const expectedCollection: ICategoria[] = [...additionalCategorias, ...categoriaCollection];
      jest.spyOn(categoriaService, 'addCategoriaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ presupuesto });
      comp.ngOnInit();

      expect(categoriaService.query).toHaveBeenCalled();
      expect(categoriaService.addCategoriaToCollectionIfMissing).toHaveBeenCalledWith(
        categoriaCollection,
        ...additionalCategorias.map(expect.objectContaining),
      );
      expect(comp.categoriasSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const presupuesto: IPresupuesto = { id: 12045 };
      const usuario: IUser = { id: 3944 };
      presupuesto.usuario = usuario;
      const categoria: ICategoria = { id: 24962 };
      presupuesto.categoria = categoria;

      activatedRoute.data = of({ presupuesto });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(usuario);
      expect(comp.categoriasSharedCollection).toContainEqual(categoria);
      expect(comp.presupuesto).toEqual(presupuesto);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPresupuesto>>();
      const presupuesto = { id: 6207 };
      jest.spyOn(presupuestoFormService, 'getPresupuesto').mockReturnValue(presupuesto);
      jest.spyOn(presupuestoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ presupuesto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: presupuesto }));
      saveSubject.complete();

      // THEN
      expect(presupuestoFormService.getPresupuesto).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(presupuestoService.update).toHaveBeenCalledWith(expect.objectContaining(presupuesto));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPresupuesto>>();
      const presupuesto = { id: 6207 };
      jest.spyOn(presupuestoFormService, 'getPresupuesto').mockReturnValue({ id: null });
      jest.spyOn(presupuestoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ presupuesto: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: presupuesto }));
      saveSubject.complete();

      // THEN
      expect(presupuestoFormService.getPresupuesto).toHaveBeenCalled();
      expect(presupuestoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPresupuesto>>();
      const presupuesto = { id: 6207 };
      jest.spyOn(presupuestoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ presupuesto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(presupuestoService.update).toHaveBeenCalled();
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
  });
});
