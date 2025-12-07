import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCopy, faCheck, faPhone, faMessage, faRobot, faClock, faLightbulb } from '@fortawesome/free-solid-svg-icons';

interface MensajeWhatsApp {
  id: number;
  formato: string;
  descripcion: string;
  ejemplo: string;
  categoria: string;
}

interface RegistroWhatsApp {
  id: number;
  mensaje: string;
  tipo: 'INGRESO' | 'GASTO';
  monto: number;
  categoria: string;
  cuenta: string;
  fecha: string;
  estado: 'PENDIENTE' | 'PROCESADO' | 'ERROR';
  respuesta: string;
}

@Component({
  selector: 'app-whatsapp-registro',
  standalone: true,
  imports: [CommonModule, FormsModule, FontAwesomeModule],
  templateUrl: './whatsapp-registro.component.html',
  styleUrls: ['./whatsapp-registro.component.scss'],
})
export class WhatsappRegistroComponent implements OnInit {
  faCopy = faCopy;
  faCheck = faCheck;
  faPhone = faPhone;
  faMessage = faMessage;
  faRobot = faRobot;
  faClock = faClock;
  faLightbulb = faLightbulb;

  // Tabs
  activeTab: 'instrucciones' | 'formatos' | 'historial' | 'config' = 'instrucciones';
  copiado = false;

  // Número de WhatsApp simulado
  numeroWhatsApp = '+34912345678';
  telefonoDisplay = '(+34) 912 345 678';

  // Formatos de mensajes
  formatosMensajes: MensajeWhatsApp[] = [
    {
      id: 1,
      formato: 'GASTO [monto] [categoria] [cuenta]',
      descripcion: 'Registra un gasto con los datos básicos',
      ejemplo: 'GASTO 25.50 Alimentación "Cuenta Principal"',
      categoria: 'Básico',
    },
    {
      id: 2,
      formato: 'GASTO [monto] [categoria] [cuenta] [descripción]',
      descripcion: 'Registra un gasto con descripción detallada',
      ejemplo: 'GASTO 50 Restaurante "Tarjeta Crédito" Almuerzo con equipo',
      categoria: 'Avanzado',
    },
    {
      id: 3,
      formato: 'INGRESO [monto] [categoria] [cuenta]',
      descripcion: 'Registra un ingreso o entrada de dinero',
      ejemplo: 'INGRESO 500 "Otros Ingresos" "Cuenta Principal"',
      categoria: 'Ingresos',
    },
    {
      id: 4,
      formato: 'TRANSFERENCIA [monto] [cuenta origen] [cuenta destino]',
      descripcion: 'Transfiere dinero entre tus cuentas',
      ejemplo: 'TRANSFERENCIA 200 "Cuenta Principal" Ahorros',
      categoria: 'Transferencias',
    },
  ];

  // Historial simulado
  historialRegistros: RegistroWhatsApp[] = [
    {
      id: 1,
      mensaje: 'GASTO 45.99 Alimentación "Cuenta Principal"',
      tipo: 'GASTO',
      monto: 45.99,
      categoria: 'Alimentación',
      cuenta: 'Cuenta Principal',
      fecha: '2025-12-05 14:30',
      estado: 'PROCESADO',
      respuesta: '✓ Gasto registrado exitosamente',
    },
    {
      id: 2,
      mensaje: 'INGRESO 1500 Salario "Cuenta Principal"',
      tipo: 'INGRESO',
      monto: 1500,
      categoria: 'Salario',
      cuenta: 'Cuenta Principal',
      fecha: '2025-12-04 09:15',
      estado: 'PROCESADO',
      respuesta: '✓ Ingreso registrado exitosamente',
    },
    {
      id: 3,
      mensaje: 'GASTO 120 Servicios "Tarjeta Crédito"',
      tipo: 'GASTO',
      monto: 120,
      categoria: 'Servicios',
      cuenta: 'Tarjeta Crédito',
      fecha: '2025-12-03 18:45',
      estado: 'PROCESADO',
      respuesta: '✓ Gasto registrado exitosamente',
    },
    {
      id: 4,
      mensaje: 'GASTO 30 Transporte',
      tipo: 'GASTO',
      monto: 30,
      categoria: 'Transporte',
      cuenta: 'Desconocida',
      fecha: '2025-12-02 16:20',
      estado: 'ERROR',
      respuesta: '⚠ Error: Cuenta no especificada. Usa: GASTO [monto] [categoria] [cuenta]',
    },
  ];

  // Configuración
  estadisticas = {
    gastosRegistrados: 2850.75,
    ingresosProcesados: 5000,
    gastosEsteMes: 1250.30,
    mensajesRecibidos: 42,
    mesajesProcesados: 39,
    tazaExito: '92.86%',
  };

  ngOnInit(): void {}

  copiarNumero(): void {
    navigator.clipboard.writeText(this.numeroWhatsApp);
    this.copiado = true;
    setTimeout(() => {
      this.copiado = false;
    }, 2000);
  }

  copiarFormato(formato: string): void {
    navigator.clipboard.writeText(formato);
  }

  abrirWhatsApp(): void {
    const mensaje = encodeURIComponent('Hola, quiero registrar mis gastos por WhatsApp');
    window.open(`https://wa.me/${this.numeroWhatsApp.replace(/[^\d]/g, '')}?text=${mensaje}`, '_blank');
  }

  obtenerClaseEstado(estado: string): string {
    switch (estado) {
      case 'PROCESADO':
        return 'estado-procesado';
      case 'ERROR':
        return 'estado-error';
      case 'PENDIENTE':
        return 'estado-pendiente';
      default:
        return '';
    }
  }

  obtenerIconoEstado(estado: string) {
    return estado === 'PROCESADO' ? this.faCheck : faLightbulb;
  }

  cambiarTab(tab: 'instrucciones' | 'formatos' | 'historial' | 'config'): void {
    this.activeTab = tab;
  }
}
