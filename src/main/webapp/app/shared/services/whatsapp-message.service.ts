import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationConfigService } from './application-config.service';

export interface WhatsappMessageDTO {
  id?: number;
  mensajeOriginal: string;
  tipoMovimiento?: string;
  monto?: number;
  categoria?: string;
  cuenta?: string;
  descripcion?: string;
  estado: string;
  respuestaBot?: string;
  fechaRecepcion: Date;
  numeroTelefonico?: string;
}

export interface EstadisticasWhatsapp {
  totalMensajes: number;
  mensajesProcesados: number;
  mensajesError: number;
  montoTotal: number;
  movimientosCreados: number;
  ultimoMensaje?: Date;
}

@Injectable({ providedIn: 'root' })
export class WhatsappMessageService {
  private resourceUrl = this.applicationConfigService.getEndpointFor('api/whatsapp-messages');
  private webhookUrl = this.applicationConfigService.getEndpointFor('api/webhook/whatsapp');

  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  /**
   * Obtiene el historial de mensajes WhatsApp del usuario actual
   * @param usuarioId - ID del usuario
   * @param page - Número de página (opcional)
   * @param size - Cantidad de registros por página (opcional, default: 50)
   * @returns Observable con lista de mensajes WhatsApp
   */
  getHistorialUsuario(usuarioId: number, page?: number, size?: number): Observable<WhatsappMessageDTO[]> {
    let params = new HttpParams();
    if (page !== undefined) {
      params = params.set('page', page.toString());
    }
    if (size !== undefined) {
      params = params.set('size', size.toString());
    } else {
      params = params.set('size', '50');
    }
    return this.http.get<WhatsappMessageDTO[]>(`${this.resourceUrl}/usuario/${usuarioId}`, { params });
  }

  /**
   * Obtiene el historial de mensajes WhatsApp por estado
   * @param estado - Estado del procesamiento (PENDIENTE, PROCESADO, ERROR, DESCARTADO)
   * @param page - Número de página (opcional)
   * @param size - Cantidad de registros por página (opcional)
   * @returns Observable con lista de mensajes WhatsApp filtrados por estado
   */
  getHistorialPorEstado(estado: string, page?: number, size?: number): Observable<WhatsappMessageDTO[]> {
    let params = new HttpParams().set('estado', estado);
    if (page !== undefined) {
      params = params.set('page', page.toString());
    }
    if (size !== undefined) {
      params = params.set('size', size.toString());
    }
    return this.http.get<WhatsappMessageDTO[]>(`${this.resourceUrl}/estado`, { params });
  }

  /**
   * Obtiene estadísticas de mensajes WhatsApp del usuario
   * @param usuarioId - ID del usuario
   * @returns Observable con estadísticas
   */
  getEstadisticas(usuarioId: number): Observable<EstadisticasWhatsapp> {
    return this.http.get<EstadisticasWhatsapp>(`${this.resourceUrl}/usuario/${usuarioId}/estadisticas`);
  }

  /**
   * Obtiene estadísticas del mes actual
   * @returns Observable con estadísticas del mes
   */
  getEstadisticasMesActual(): Observable<any> {
    return this.http.get(`${this.resourceUrl}/estadisticas/mes-actual`);
  }

  /**
   * Obtiene mensajes en rango de fechas
   * @param usuarioId - ID del usuario
   * @param fechaInicio - Fecha de inicio (formato ISO)
   * @param fechaFin - Fecha de fin (formato ISO)
   * @returns Observable con lista de mensajes en rango
   */
  getHistorialPorFecha(usuarioId: number, fechaInicio: string, fechaFin: string): Observable<WhatsappMessageDTO[]> {
    const params = new HttpParams()
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin);
    return this.http.get<WhatsappMessageDTO[]>(`${this.resourceUrl}/usuario/${usuarioId}/fecha`, { params });
  }

  /**
   * Obtiene un mensaje específico por ID
   * @param id - ID del mensaje WhatsApp
   * @returns Observable con datos del mensaje
   */
  getDetalle(id: number): Observable<WhatsappMessageDTO> {
    return this.http.get<WhatsappMessageDTO>(`${this.resourceUrl}/${id}`);
  }

  /**
   * Reintenta procesar un mensaje que falló
   * @param id - ID del mensaje WhatsApp
   * @returns Observable con resultado del reprocesamiento
   */
  reprocesarMensaje(id: number): Observable<WhatsappMessageDTO> {
    return this.http.post<WhatsappMessageDTO>(`${this.resourceUrl}/${id}/reprocesar`, {});
  }

  /**
   * Descarta un mensaje WhatsApp
   * @param id - ID del mensaje
   * @returns Observable con resultado
   */
  descartarMensaje(id: number): Observable<void> {
    return this.http.post<void>(`${this.resourceUrl}/${id}/descartar`, {});
  }

  /**
   * Envía un webhook de test (para desarrollo)
   * @param numeroTelefonico - Número telefónico
   * @param mensaje - Texto del mensaje
   * @returns Observable con respuesta del webhook
   */
  testWebhook(numeroTelefonico: string, mensaje: string): Observable<WhatsappMessageDTO> {
    const request = {
      from: numeroTelefonico,
      text: mensaje,
      timestamp: new Date().toISOString(),
      message_id: `test_${Date.now()}`,
    };
    return this.http.post<WhatsappMessageDTO>(this.webhookUrl, request);
  }

  /**
   * Obtiene la configuración del webhook (para mostrar instrucciones de integración)
   * @returns Observable con configuración
   */
  getWebhookConfig(): Observable<any> {
    return this.http.get(`${this.webhookUrl}/config`);
  }
}
