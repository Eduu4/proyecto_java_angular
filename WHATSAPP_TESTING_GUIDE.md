# Guía Completa de Prueba del Webhook de WhatsApp

## 🚀 Inicio Rápido (3 Minutos)

### Opción Más Fácil: Script PowerShell

```powershell
# 1. Asegúrate de tener el servidor ejecutándose en puerto 8080
# 2. Abre una terminal PowerShell
# 3. Ejecuta el script en modo interactivo:

cd c:\sistema\angularv3
.\test-whatsapp-webhook.ps1 -Interactivo
```

**Características:**
- ✅ Menú interactivo con opciones preconfiguradas
- ✅ Envío de mensajes personalizados
- ✅ Visualización clara de respuestas
- ✅ Manejo de errores automático

### Ejemplos de Uso del Script:

```powershell
# Prueba con valores por defecto
.\test-whatsapp-webhook.ps1

# Con número y mensaje personalizados
.\test-whatsapp-webhook.ps1 -NumeroTelefonico "+34912345678" -Mensaje "GASTO 50 Transporte"

# Servidor en puerto diferente
.\test-whatsapp-webhook.ps1 -BaseUrl "http://localhost:9000" -Interactivo
```

---

## 📱 Pruebas Locales desde la Aplicación Web

La aplicación incluye una interfaz de prueba completa en `/whatsapp-test`:

1. **Accede a la página de pruebas:**
   - URL: `http://localhost:8080/whatsapp-test`
   - O navega desde la barra de menú → "Prueba WhatsApp"

2. **Configura el número telefónico:**
   - Ingresa el número que registraste en tu perfil de usuario
   - Formato: `+34912345678` o `34912345678`
   - El número debe estar registrado en tu cuenta

3. **Selecciona o escribe un mensaje:**
   - Puedes usar los botones de plantilla rápida
   - O escribe tu propio mensaje en el área de texto

4. **Formatos válidos:**
   ```
   GASTO 25.50 Alimentación "Cuenta Principal"
   INGRESO 500 Freelance "Cuenta Ahorros"
   GASTO 15 Transporte Taxi al trabajo
   INGRESO 3000 Salario Pago mensual
   ```

5. **Haz clic en "Enviar Mensaje de Prueba"**

6. **Verifica el resultado:**
   - Estado: `PROCESADO` ✅ (movimiento creado)
   - Estado: `ERROR` ❌ (revisa la respuesta del bot)
   - La tabla de historial muestra todos los detalles

---

## 🔌 Pruebas con Curl (Línea de Comandos)

### Activar el servidor en background (si no está corriendo):

```powershell
cd c:\sistema\angularv3
.\mvnw spring-boot:run
```

### Enviar un mensaje de prueba:

```powershell
# Gasto simple
$body = @{
    from = "+34912345678"
    text = "GASTO 25.50 Alimentación"
    timestamp = [DateTime]::UtcNow.ToString('o')
    message_id = "test_$(Get-Random)"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/webhook/whatsapp" `
  -Method POST `
  -Body $body `
  -ContentType "application/json"
```

### Ejemplo más completo:

```powershell
# Gasto con cuenta especificada
$body = @{
    from = "+34912345678"
    text = 'GASTO 25.50 Alimentación "Cuenta Principal"'
    timestamp = [DateTime]::UtcNow.ToString('o')
    message_id = "test_$(Get-Random)"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/webhook/whatsapp" `
  -Method POST `
  -Body $body `
  -ContentType "application/json" `
  -PassThru

# Mostrar la respuesta
$response.Content | ConvertFrom-Json | Format-Object -Property estado, tipoMovimiento, monto, respuestaBot
```

### Verificación del webhook (Challenge token):

```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/webhook/whatsapp?hub.mode=subscribe&hub.challenge=test123&hub.verify_token=finanzas_webhook_token"
```

---

## 🗄️ Verificación en la Base de Datos

Después de enviar un mensaje, puedes verificar que se guardó:

```sql
-- Ver los últimos mensajes de WhatsApp recibidos
SELECT * FROM whatsapp_message 
ORDER BY fecha_recepcion DESC 
LIMIT 10;

-- Ver mensajes por estado
SELECT estado, COUNT(*) as cantidad
FROM whatsapp_message
GROUP BY estado;

-- Ver movimientos creados desde WhatsApp
SELECT m.* FROM movimiento m
WHERE m.id IN (
  SELECT movimiento_id FROM whatsapp_message WHERE movimiento_id IS NOT NULL
)
ORDER BY m.fecha DESC;
```

---

## 🔧 Configuración Requerida

### 1. Registrar el Número de Teléfono en tu Usuario

1. Inicia sesión en la aplicación
2. Accede a tu perfil de usuario
3. Busca el campo "Número Telefónico" (tenga cuidado con la privacidad)
4. Ingresa el número en formato internacional: `+34912345678`
5. Guarda los cambios

### 2. Crear Categorías y Cuentas

Antes de enviar mensajes, asegúrate de tener:

- **Categorías:** Alimentación, Transporte, Entretenimiento, Salario, Freelance, etc.
- **Cuentas:** Cuenta Principal, Cuenta Ahorros, etc.

Crea estas desde el menú de Entidades → Categoría y Entidades → Cuenta

### 3. Variables de Entorno (Producción)

Para producción, configura:

```bash
export WHATSAPP_VERIFY_TOKEN=tu_token_secreto
export WHATSAPP_API_KEY=tu_api_key
export WHATSAPP_PHONE_NUMBER=tu_numero_whatsapp
```

---

## 📊 Formatos de Mensaje Aceptados

### Formato Básico
```
GASTO <monto> <categoría>
INGRESO <monto> <categoría>
```

### Con Cuenta
```
GASTO <monto> <categoría> "<nombre_cuenta>"
INGRESO <monto> <categoría> "<nombre_cuenta>"
```

### Con Descripción
```
GASTO <monto> <categoría> <descripción>
GASTO <monto> <categoría> "<nombre_cuenta>" <descripción>
```

### Ejemplos Reales
```
GASTO 25.50 Alimentación "Cuenta Principal"
INGRESO 500 Freelance "Cuenta Ahorros"
GASTO 15 Transporte
INGRESO 3000 Salario Pago mensual
GASTO 9.99 Entretenimiento Netflix Premium
GASTO 45 Servicios "Cuenta Ahorros" Pago de Internet
```

---

## ❌ Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| "Número no registrado" | El número no está en la BD | Registra tu número en el perfil |
| "Categoría no encontrada" | Typo en el nombre | Usa exactamente el nombre de la categoría |
| "Cuenta no encontrada" | La cuenta no existe | Crea la cuenta primero |
| "Monto inválido" | Formato incorrecto o monto ≤ 0 | Usa formato: `25.50` o `25,50` |
| "Mensaje vacío" | El texto está en blanco | Escribe un mensaje válido |

---

## 🔗 Integración con Proveedores Reales

### Twilio WhatsApp Business API

1. **Crear cuenta en Twilio:** https://www.twilio.com
2. **Configurar Webhook:**
   - **URL:** `https://tudominio.com/api/webhook/whatsapp`
   - **Método:** POST
   - **Eventos:** message (incoming message)

3. **Configurar Verify Token:**
   ```
   Producción: tu_token_secreto_seguro
   ```

### Meta Business API

1. **Crear app en Meta:** https://developers.facebook.com
2. **Configurar Webhook:**
   - **URL:** `https://tudominio.com/api/webhook/whatsapp`
   - **Evento:** messages
   - **Verify Token:** tu_token_secreto_seguro

3. **Payload esperado:**
   ```json
   {
     "from": "+34912345678",
     "text": "GASTO 25.50 Alimentación",
     "timestamp": "1234567890",
     "message_id": "wamid.xxx"
   }
   ```

---

## 📈 Estadísticas y Monitoreo

### En la aplicación:
- Navega a "Registrar por WhatsApp" → "Estadísticas"
- Verá un resumen de:
  - Total de mensajes recibidos
  - Mensajes procesados exitosamente
  - Mensajes con error
  - Monto total registrado
  - Movimientos creados

### En logs:
```
tail -f logs/spring.log | grep -i whatsapp
```

---

## 🚀 Próximos Pasos

1. **Conectar con Twilio:** Solicita credenciales API
2. **Configurar números de teléfono:** Asigna un número WhatsApp Business
3. **Habilitar webhook en producción:** Deploy en servidor seguro
4. **Agregar validaciones:** Implementar tokens de seguridad adicionales
5. **Notificaciones:** Enviar confirmación al usuario después de procesar

---

## 📞 Soporte

Si encuentras problemas:

1. Revisa los logs de la aplicación
2. Verifica que el usuario tenga número registrado
3. Asegúrate de que categorías y cuentas existan
4. Comprueba el formato del mensaje
5. Consulta la base de datos directamente

Tabla útil para debugging:
```sql
SELECT * FROM whatsapp_message WHERE numero_telefonico = '+34912345678' ORDER BY fecha_recepcion DESC LIMIT 5;
```
