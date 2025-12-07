import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

@Component({
  selector: 'app-whatsapp-test',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './whatsapp-test.component.html',
  styleUrls: ['./whatsapp-test.component.scss'],
})
export class WhatsappTestComponent implements OnInit {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private applicationConfigService = inject(ApplicationConfigService);
  private webhookUrl: string = '';

  formulario!: FormGroup;
  resultados: any[] = [];
  cargando = false;
  error: string | null = null;
  exito: string | null = null;
  mensajesFormatoValidos = [
    { tipo: 'Gasto Alimentación', valor: 'GASTO 25.50 Alimentación "Cuenta Principal"' },
    { tipo: 'Ingreso Freelance', valor: 'INGRESO 500 Freelance "Cuenta Ahorros"' },
    { tipo: 'Gasto Transporte', valor: 'GASTO 15 Transporte' },
    { tipo: 'Ingreso Salario', valor: 'INGRESO 3000 Salario "Cuenta Principal" Pago mensual' },
    { tipo: 'Gasto Suscripción', valor: 'GASTO 9.99 Entretenimiento Netflix' },
  ];

  ngOnInit(): void {
    this.webhookUrl = this.applicationConfigService.getEndpointFor('api/webhook/whatsapp');
    this.inicializarFormulario();
  }

  inicializarFormulario(): void {
    this.formulario = this.fb.group({
      numeroTelefonico: ['+34912345678', [Validators.required, Validators.pattern(/^\+?[0-9]{10,20}$/)]],
      mensaje: ['GASTO 25.50 Alimentación "Cuenta Principal"', Validators.required],
    });
  }

  enviarMensajeTest(): void {
    if (!this.formulario.valid) {
      this.error = 'Por favor, completa los campos requeridos correctamente.';
      return;
    }

    this.cargando = true;
    this.error = null;
    this.exito = null;

    const { numeroTelefonico, mensaje } = this.formulario.value;
    const request = {
      from: numeroTelefonico,
      text: mensaje,
      timestamp: new Date().toISOString(),
      message_id: `test_${Date.now()}`,
    };

    this.http.post<any>(this.webhookUrl, request).subscribe(
      (resultado: any) => {
        this.cargando = false;
        this.resultados.unshift(resultado);
        this.exito = `✅ Mensaje procesado exitosamente. Estado: ${resultado.estado}`;
        this.formulario.reset({
          numeroTelefonico: '+34912345678',
          mensaje: 'GASTO 25.50 Alimentación "Cuenta Principal"',
        });
      },
      (error: any) => {
        this.cargando = false;
        this.error = `❌ Error: ${error.error?.message || error.message || 'Error desconocido'}`;
        console.error('Error en webhook test:', error);
      },
    );
  }

  usarFormato(formato: string): void {
    this.formulario.patchValue({ mensaje: formato });
  }

  limpiar(): void {
    this.resultados = [];
    this.error = null;
    this.exito = null;
  }

  obtenerClaseEstado(estado: string): string {
    switch (estado) {
      case 'PROCESADO':
        return 'bg-success';
      case 'ERROR':
        return 'bg-danger';
      case 'PENDIENTE':
        return 'bg-warning';
      case 'DESCARTADO':
        return 'bg-secondary';
      default:
        return 'bg-info';
    }
  }
}
