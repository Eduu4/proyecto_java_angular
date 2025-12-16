# Módulo Movimientos

Resumen:

- Gestiona movimientos financieros (INGRESO / GASTO).
- Endpoints principales:
  - POST /api/movimientos/registrar → registrar movimiento
  - GET /api/movimientos → listar movimientos
  - GET /api/movimientos/{id} → obtener movimiento
  - GET /api/movimientos/resumen → resumen financiero (ingresos, gastos, balance)

Validaciones:

- `MovimientoRequestDTO` valida: `tipo` (@NotNull), `monto` (@Positive), `descripcion` (@NotBlank), `fecha` (@NotNull).
- Se responde 400 Bad Request cuando falta o es inválido (mensaje claro).

Ejemplo request - registrar gasto:

POST /api/movimientos/registrar

```json
{
  "tipo": "GASTO",
  "monto": 150000.0,
  "descripcion": "Supermercado",
  "categoria": "Alimentación",
  "fecha": "2025-12-10"
}
```

Ejemplo response - resumen:

GET /api/movimientos/resumen

```json
{
  "totalIngresos": 2500000.0,
  "totalGastos": 1200000.0,
  "balance": 1300000.0
}
```

Notas:

- Validaciones adicionales del servidor: no se acepta `monto <= 0`, ni `fecha` futura.
- Para pruebas de integración ver `src/test/java/finanzas/web/rest/MovimientoResourceIT.java`.
