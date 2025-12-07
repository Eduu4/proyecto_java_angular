#!/usr/bin/env pwsh

<#
.SYNOPSIS
Script para probar el webhook de WhatsApp localmente

.DESCRIPTION
Permite enviar mensajes de prueba al endpoint del webhook sin necesidad de WhatsApp real

.PARAMETER NumeroTelefonico
Número telefónico del remitente (ej: +34912345678)

.PARAMETER Mensaje
Texto del mensaje (ej: GASTO 25.50 Alimentación "Cuenta Principal")

.PARAMETER BaseUrl
URL base de la aplicación (default: http://localhost:8080)

.EXAMPLE
.\test-whatsapp-webhook.ps1 -NumeroTelefonico "+34912345678" -Mensaje 'GASTO 25.50 Alimentación "Cuenta Principal"'

#>

param(
    [Parameter(Mandatory=$false)]
    [string]$NumeroTelefonico = "+34912345678",
    
    [Parameter(Mandatory=$false)]
    [string]$Mensaje = 'GASTO 25.50 Alimentación "Cuenta Principal"',
    
    [Parameter(Mandatory=$false)]
    [string]$BaseUrl = "http://localhost:8080",
    
    [Parameter(Mandatory=$false)]
    [switch]$Interactivo
)

function Mostrar-Menu {
    Write-Host "`n╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
    Write-Host "║      PRUEBA DE WEBHOOK DE WHATSAPP                             ║" -ForegroundColor Cyan
    Write-Host "╚════════════════════════════════════════════════════════════════╝`n" -ForegroundColor Cyan
    
    Write-Host "1. Enviar GASTO simple" -ForegroundColor Green
    Write-Host "2. Enviar INGRESO simple" -ForegroundColor Green
    Write-Host "3. Enviar GASTO con cuenta específica" -ForegroundColor Green
    Write-Host "4. Enviar INGRESO con descripción" -ForegroundColor Green
    Write-Host "5. Número personalizado y mensaje" -ForegroundColor Yellow
    Write-Host "6. Salir" -ForegroundColor Red
    Write-Host ""
}

function Enviar-Webhook {
    param(
        [string]$Numero,
        [string]$MensajeTexto,
        [string]$Descripcion
    )
    
    $request = @{
        from = $Numero
        text = $MensajeTexto
        timestamp = [DateTime]::UtcNow.ToString('o')
        message_id = "test_$(Get-Random -Minimum 1000 -Maximum 9999)"
    }
    
    Write-Host "`n📤 Enviando mensaje de prueba..." -ForegroundColor Cyan
    Write-Host "   Descripción: $Descripcion" -ForegroundColor Gray
    Write-Host "   Desde: $Numero" -ForegroundColor Gray
    Write-Host "   Mensaje: '$MensajeTexto'" -ForegroundColor Gray
    Write-Host "   URL: $BaseUrl/api/webhook/whatsapp" -ForegroundColor Gray
    
    try {
        $response = Invoke-WebRequest -Uri "$BaseUrl/api/webhook/whatsapp" `
            -Method POST `
            -Body ($request | ConvertTo-Json) `
            -ContentType "application/json" `
            -ErrorAction Stop
        
        $resultado = $response.Content | ConvertFrom-Json
        
        Write-Host "`n✅ Respuesta recibida:" -ForegroundColor Green
        Write-Host "   Estado: $($resultado.estado)" -ForegroundColor $(if($resultado.estado -eq 'PROCESADO') {'Green'} else {'Yellow'})
        Write-Host "   Tipo: $($resultado.tipoMovimiento)" -ForegroundColor Cyan
        if ($resultado.monto) {
            Write-Host "   Monto: `$$($resultado.monto)" -ForegroundColor Cyan
        }
        if ($resultado.categoria) {
            Write-Host "   Categoría: $($resultado.categoria)" -ForegroundColor Cyan
        }
        if ($resultado.cuenta) {
            Write-Host "   Cuenta: $($resultado.cuenta)" -ForegroundColor Cyan
        }
        Write-Host "   Bot: $($resultado.respuestaBot)" -ForegroundColor Gray
        
        return $true
    } catch {
        Write-Host "`n❌ Error en la solicitud:" -ForegroundColor Red
        Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
        
        if ($_.Exception.Response) {
            Write-Host "`n   Respuesta del servidor:" -ForegroundColor Yellow
            Write-Host "   $($_.Exception.Response.Content)" -ForegroundColor Yellow
        }
        
        return $false
    }
}

# Si se pasa modo interactivo
if ($Interactivo -or (-not $NumeroTelefonico -and -not $Mensaje)) {
    $continuar = $true
    
    while ($continuar) {
        Mostrar-Menu
        $opcion = Read-Host "Selecciona una opción"
        
        switch ($opcion) {
            "1" {
                Enviar-Webhook -Numero "+34912345678" `
                    -MensajeTexto "GASTO 25.50 Alimentación" `
                    -Descripcion "Gasto de alimentación"
            }
            "2" {
                Enviar-Webhook -Numero "+34912345678" `
                    -MensajeTexto "INGRESO 500 Freelance" `
                    -Descripcion "Ingreso por trabajo freelance"
            }
            "3" {
                Enviar-Webhook -Numero "+34912345678" `
                    -MensajeTexto 'GASTO 45.99 Restaurante "Tarjeta Crédito"' `
                    -Descripcion "Gasto en restaurante con cuenta específica"
            }
            "4" {
                Enviar-Webhook -Numero "+34912345678" `
                    -MensajeTexto "INGRESO 1500 Salario Pago mensual" `
                    -Descripcion "Ingreso salarial con descripción"
            }
            "5" {
                $numero = Read-Host "Número telefónico (default: +34912345678)"
                if ([string]::IsNullOrEmpty($numero)) { $numero = "+34912345678" }
                
                $msg = Read-Host "Mensaje (default: GASTO 25.50 Alimentación)"
                if ([string]::IsNullOrEmpty($msg)) { $msg = "GASTO 25.50 Alimentación" }
                
                Enviar-Webhook -Numero $numero `
                    -MensajeTexto $msg `
                    -Descripcion "Mensaje personalizado"
            }
            "6" {
                Write-Host "`n👋 ¡Hasta luego!" -ForegroundColor Cyan
                $continuar = $false
            }
            default {
                Write-Host "Opción no válida" -ForegroundColor Red
            }
        }
        
        if ($continuar -and ($opcion -ne "6")) {
            Read-Host "`nPresiona Enter para continuar..."
        }
    }
} else {
    # Modo no interactivo
    Enviar-Webhook -Numero $NumeroTelefonico -MensajeTexto $Mensaje -Descripcion "Mensaje de prueba"
}
