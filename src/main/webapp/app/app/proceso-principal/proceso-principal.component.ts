import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faPlus, faEdit, faTrash, faChartLine, faWallet, faArrowUpLong, faArrowDownLong } from '@fortawesome/free-solid-svg-icons';

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
  movimientos: Movimiento[] = [
    {
      id: 1,
      tipo: 'INGRESO',
      monto: 2500,
      categoria: 'Salario',
      cuenta: 'Cuenta Principal',
      fecha: '2025-12-01',
      descripcion: 'Pago mensual',
    },
    {
      id: 2,
      tipo: 'GASTO',
      monto: 150,
      categoria: 'Servicios',
      cuenta: 'Cuenta Principal',
      fecha: '2025-12-02',
      descripcion: 'Pago de luz',
    },
    {
      id: 3,
      tipo: 'GASTO',
      monto: 80,
      categoria: 'Alimentación',
      cuenta: 'Cuenta Principal',
      fecha: '2025-12-02',
      descripcion: 'Compras en supermercado',
    },
    {
      id: 4,
      tipo: 'INGRESO',
      monto: 500,
      categoria: 'Otros Ingresos',
      cuenta: 'Cuenta Principal',
      fecha: '2025-12-03',
      descripcion: 'Venta online',
    },
  ];

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

  ngOnInit(): void {
    this.calcularResumen();
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

    if (this.movimientoEnEdicion) {
      // Editar movimiento existente
      const index = this.movimientos.findIndex(m => m.id === this.movimientoEnEdicion!.id);
      if (index > -1) {
        this.movimientos[index] = {
          ...this.formulario,
          id: this.movimientoEnEdicion.id,
          monto: parseFloat(this.formulario.monto as any),
        } as Movimiento;
      }
    } else {
      // Crear nuevo movimiento
      const nuevoMovimiento: Movimiento = {
        id: Math.max(...this.movimientos.map(m => m.id), 0) + 1,
        ...this.formulario,
        monto: parseFloat(this.formulario.monto as any),
      } as Movimiento;
      this.movimientos.unshift(nuevoMovimiento);
    }

    this.calcularResumen();
    this.cerrarFormulario();
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
