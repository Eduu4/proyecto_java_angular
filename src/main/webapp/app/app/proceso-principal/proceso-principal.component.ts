import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faPlus, faEdit, faTrash, faChartLine, faWallet, faArrowUpLong, faArrowDownLong } from '@fortawesome/free-solid-svg-icons';
import { AccountService } from 'app/core/auth/account.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';
import { Router } from '@angular/router';
import { MovimientoService } from 'app/entities/movimiento/service/movimiento.service';
import { IMovimiento, NewMovimiento } from 'app/entities/movimiento/movimiento.model';
import { IResumenFinanciero } from 'app/entities/movimiento/resumen-financiero.model';
import dayjs from 'dayjs/esm';

interface Movimiento {
  id: number;
  tipo: 'INGRESO' | 'GASTO';
  monto: number;
  categoria: string;
  cuenta: string;
  fecha: string;
  descripcion: string;
}

interface ResumenFinanciero {
  saldoTotal: number;
  totalIngresos: number;
  totalGastos: number;
  balanceNeto: number;
}

@Component({
  selector: 'app-proceso-principal',
  standalone: true,
  imports: [CommonModule, FormsModule, FontAwesomeModule],
  templateUrl: './proceso-principal.component.html',
  styleUrls: ['./proceso-principal.component.scss'],
})
export class ProcesoPrincipalComponent implements OnInit {
  faPlus = faPlus;
  faEdit = faEdit;
  faTrash = faTrash;
  faChartLine = faChartLine;
  faWallet = faWallet;
  faArrowUpLong = faArrowUpLong;
  faArrowDownLong = faArrowDownLong;

  // Estados y modales
  showFormMovimiento = false;
  showFiltros = false;
  tipoMovimientoSeleccionado: 'INGRESO' | 'GASTO' | '' = '';
  movimientoEnEdicion: Movimiento | null = null;

  // Datos de prueba (sin backend)
  movimientos: Movimiento[] = [];

  categorias = ['Salario', 'Servicios', 'Alimentación', 'Transporte', 'Otros Ingresos', 'Entretenimiento'];
  cuentas = ['Cuenta Principal', 'Ahorros', 'Tarjeta de Crédito'];

  // Formulario
  formulario = {
    tipo: 'GASTO' as 'INGRESO' | 'GASTO',
    monto: '',
    categoria: '',
    cuenta: '',
    fecha: new Date().toISOString().split('T')[0],
    descripcion: '',
  };

  resumen: ResumenFinanciero = {
    saldoTotal: 0,
    totalIngresos: 0,
    totalGastos: 0,
    balanceNeto: 0,
  };

  // services
  private readonly movimientoService = inject(MovimientoService);
  private readonly accountService = inject(AccountService);
  private readonly stateStorageService = inject(StateStorageService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    // Ensure only authenticated users can use this component (double check besides route guard)
    this.accountService.identity().subscribe(account => {
      if (!account) {
        // store intended url and redirect to login
        this.stateStorageService.storeUrl('/proceso-principal');
        this.router.navigate(['/login']);
        return;
      }
      // fetch real data from backend
      this.loadDatos();
    });
  }

  loadDatos(): void {
    // fetch list
    this.movimientoService.query({ sort: 'id,desc' }).subscribe(res => {
      const body = res.body ?? [];
      this.movimientos = body.map(m => ({
        id: m.id!,
        tipo: m.tipo as 'INGRESO' | 'GASTO',
        monto: Number(m.monto ?? 0),
        categoria: (m.categoria as any)?.nombre ?? m.descripcion ?? '',
        cuenta: (m.cuenta as any)?.nombre ?? '',
        fecha: m.fechaMovimiento?.format('YYYY-MM-DD') ?? '',
        descripcion: m.descripcion ?? '',
      }));
      this.calcularResumen();
    });

    // fetch resumen
    this.movimientoService.resumen().subscribe((r: IResumenFinanciero) => {
      this.resumen = {
        saldoTotal: this.resumen.saldoTotal,
        totalIngresos: r.totalIngresos,
        totalGastos: r.totalGastos,
        balanceNeto: r.balance,
      };
    });
  }

  calcularResumen(): void {
    let totalIngresos = 0;
    let totalGastos = 0;

    this.movimientos.forEach(mov => {
      if (mov.tipo === 'INGRESO') {
        totalIngresos += mov.monto;
      } else {
        totalGastos += mov.monto;
      }
    });

    this.resumen = {
      saldoTotal: 5000, // Valor inicial simulado
      totalIngresos,
      totalGastos,
      balanceNeto: totalIngresos - totalGastos,
    };
  }

  abrirFormulario(movimiento?: Movimiento): void {
    if (movimiento) {
      this.movimientoEnEdicion = movimiento;
      this.formulario = {
        tipo: movimiento.tipo,
        monto: movimiento.monto.toString(),
        categoria: movimiento.categoria,
        cuenta: movimiento.cuenta,
        fecha: movimiento.fecha,
        descripcion: movimiento.descripcion,
      };
    } else {
      this.movimientoEnEdicion = null;
      this.resetearFormulario();
    }
    this.showFormMovimiento = true;
  }

  cerrarFormulario(): void {
    this.showFormMovimiento = false;
    this.movimientoEnEdicion = null;
    this.resetearFormulario();
  }

  resetearFormulario(): void {
    this.formulario = {
      tipo: 'GASTO',
      monto: '',
      categoria: '',
      cuenta: '',
      fecha: new Date().toISOString().split('T')[0],
      descripcion: '',
    };
  }

  guardarMovimiento(): void {
    if (!this.formulario.monto || !this.formulario.categoria || !this.formulario.cuenta) {
      alert('Por favor completa todos los campos requeridos');
      return;
    }
    // Build the payload for the backend
    const payloadBase: Partial<IMovimiento> = {
      tipo: this.formulario.tipo as any,
      monto: parseFloat(this.formulario.monto as any),
      fechaMovimiento: dayjs(this.formulario.fecha),
      // backend requires fechaRegistro (@NotNull on DTO)
      fechaRegistro: dayjs(),
      descripcion: this.formulario.descripcion || undefined,
    };

    if (this.movimientoEnEdicion) {
      // Update existing movimiento via backend
      const movimientoToUpdate: IMovimiento = {
        id: this.movimientoEnEdicion.id,
        ...payloadBase,
      } as IMovimiento;

      this.movimientoService.update(movimientoToUpdate).subscribe({
        next: res => {
          const m = res.body!;
          // update local cache
          const index = this.movimientos.findIndex(x => x.id === m.id);
          const mapped: Movimiento = {
            id: m.id!,
            tipo: (m.tipo as any) || 'GASTO',
            monto: Number(m.monto ?? 0),
            categoria: (m.categoria as any)?.nombre ?? '',
            cuenta: (m.cuenta as any)?.nombre ?? '',
            fecha: m.fechaMovimiento?.format('YYYY-MM-DD') ?? '',
            descripcion: m.descripcion ?? '',
          };
          if (index > -1) {
            this.movimientos[index] = mapped;
          }
          this.calcularResumen();
          this.cerrarFormulario();
        },
        error: () => {
          alert('Error al actualizar el movimiento');
        },
      });
    } else {
      // Create new movimiento via backend
      const newMovimiento: NewMovimiento = {
        id: null,
        ...payloadBase,
      } as NewMovimiento;

      this.movimientoService.create(newMovimiento).subscribe({
        next: res => {
          const m = res.body!;
          const mapped: Movimiento = {
            id: m.id!,
            tipo: (m.tipo as any) || 'GASTO',
            monto: Number(m.monto ?? 0),
            categoria: (m.categoria as any)?.nombre ?? '',
            cuenta: (m.cuenta as any)?.nombre ?? '',
            fecha: m.fechaMovimiento?.format('YYYY-MM-DD') ?? '',
            descripcion: m.descripcion ?? '',
          };
          this.movimientos.unshift(mapped);
          this.calcularResumen();
          this.cerrarFormulario();
        },
        error: () => {
          alert('Error al crear el movimiento');
        },
      });
    }
  }

  eliminarMovimiento(id: number): void {
    if (confirm('¿Está seguro de que desea eliminar este movimiento?')) {
      this.movimientos = this.movimientos.filter(m => m.id !== id);
      this.calcularResumen();
    }
  }

  cambiarTipo(tipo: 'INGRESO' | 'GASTO'): void {
    this.formulario.tipo = tipo;
    this.tipoMovimientoSeleccionado = tipo;
  }

  obtenerClaseMovimiento(tipo: string): string {
    return tipo === 'INGRESO' ? 'movimiento-ingreso' : 'movimiento-gasto';
  }

  obtenerIconoMovimiento(tipo: string) {
    return tipo === 'INGRESO' ? this.faArrowUpLong : this.faArrowDownLong;
  }
}
