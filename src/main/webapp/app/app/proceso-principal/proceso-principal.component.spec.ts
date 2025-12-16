// Use TestBed providers for AccountService and MovimientoService mocks

import { TestBed, waitForAsync } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import ProcesoPrincipalComponent from './proceso-principal.component';
import { AccountService } from 'app/core/auth/account.service';
import { MovimientoService } from 'app/entities/movimiento/service/movimiento.service';

describe('ProcesoPrincipalComponent', () => {
  let comp: ProcesoPrincipalComponent;
  let mockAccountService: AccountService;
  let mockMovimientoService: MovimientoService;
  let mockRouter: any;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ProcesoPrincipalComponent],
      providers: [AccountService, MovimientoService, { provide: Router, useValue: { navigate: jest.fn() } }],
    })
      .overrideTemplate(ProcesoPrincipalComponent, '')
      .compileComponents();
  }));

  beforeEach(() => {
    const fixture = TestBed.createComponent(ProcesoPrincipalComponent);
    comp = fixture.componentInstance;
    mockAccountService = TestBed.inject(AccountService);
    mockMovimientoService = TestBed.inject(MovimientoService);
    mockRouter = TestBed.inject(Router) as any;
  });

  it('should redirect to login when not authenticated', () => {
    // GIVEN
    mockAccountService.identity = jest.fn(() => of(null));

    // WHEN
    comp.ngOnInit();

    // THEN
    expect(mockAccountService.identity).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should load movimientos and resumen when authenticated', () => {
    // GIVEN
    mockAccountService.identity = jest.fn(() => of({ login: 'user' }));
    const sampleMovimiento = { id: 1, tipo: 'GASTO', monto: 100, descripcion: 'x', fechaMovimiento: '2025-12-01' } as any;
    mockMovimientoService.query = jest.fn(() => of(new HttpResponse({ body: [sampleMovimiento] })));
    mockMovimientoService.resumen = jest.fn(() => of({ totalIngresos: 200, totalGastos: 100, balance: 100 }));

    // WHEN
    comp.ngOnInit();

    // THEN
    expect(mockMovimientoService.query).toHaveBeenCalled();
    expect(mockMovimientoService.resumen).toHaveBeenCalled();
  });

  it('should create a new movimiento when guardarMovimiento is called for new item', () => {
    // GIVEN
    mockAccountService.identity = jest.fn(() => of({ login: 'user' }));
    mockMovimientoService.create = jest.fn(() =>
      of(new HttpResponse({ body: { id: 123, tipo: 'INGRESO', monto: 250, fechaMovimiento: {} } } as any)),
    );

    comp.formulario = {
      tipo: 'INGRESO',
      monto: '250',
      categoria: 'Salario',
      cuenta: 'Cuenta Principal',
      fecha: '2025-12-01',
      descripcion: 'Pago',
    } as any;

    // WHEN
    comp.guardarMovimiento();

    // THEN
    expect(mockMovimientoService.create).toHaveBeenCalled();
    expect(comp.movimientos[0].id).toBe(123);
  });

  it('should update an existing movimiento when guardarMovimiento is called in edit mode', () => {
    // GIVEN
    const existing = { id: 1, tipo: 'GASTO', monto: 100, descripcion: 'x', fechaMovimiento: '2025-12-01' } as any;
    comp.movimientos = [{ id: 1, tipo: 'GASTO', monto: 100, categoria: 'A', cuenta: 'B', fecha: '2025-12-01', descripcion: 'x' } as any];
    comp.movimientoEnEdicion = { id: 1 } as any;
    comp.formulario = {
      tipo: 'GASTO',
      monto: '150',
      categoria: 'Servicios',
      cuenta: 'Cuenta Principal',
      fecha: '2025-12-02',
      descripcion: 'update',
    } as any;

    mockMovimientoService.update = jest.fn(() =>
      of(new HttpResponse({ body: { id: 1, tipo: 'GASTO', monto: 150, fechaMovimiento: {} } } as any)),
    );

    // WHEN
    comp.guardarMovimiento();

    // THEN
    expect(mockMovimientoService.update).toHaveBeenCalled();
    expect(comp.movimientos[0].monto).toBe(150);
  });
});
