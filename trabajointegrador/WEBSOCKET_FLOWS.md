# 🔄 Flujos y Diagramas de WebSocket

## 1. Diagrama de Conexión

```
Cliente Web (Navegador)
     │
     │ 1. HTTP GET websocket-example.html
     │ (Descarga interfaz HTML)
     ▼
Servidor (Spring Boot)
     │
     │ 2. Usuario hace clic "Conectar"
     ▼
Socket HTTP upgrade
     │
     │ 3. ws://localhost:8080/api/v1/ws/ordenes
     │ (Upgrade a protocolo WebSocket)
     ▼
STOMP CONNECT
     │
     │ ✅ CONNECTED frame
     │ (Conexión establecida)
     ▼
Cliente Conectado
     │
     │ 4. SUBSCRIBE /topic/ordenes/estado
     │ (Cliente se registra para recibir)
     ▼
Servidor Escucha
     │
     │ (Espera cambios de estado)
     │
     │ 5. OrdenBusiness.cambiarEstado()
     │    → webSocketService.notify()
     │    → /topic/ordenes/estado
     ▼
MESSAGE STOMP
     │
     │ JSON: {codigoExterno, estadoAnterior, estadoActual, ...}
     ▼
Cliente Recibe
     │
     │ UI se actualiza en TIEMPO REAL
     │ ✅ Notification recibida
     ▼
Fin
```

## 2. Flujo de Cambio de Estado de Orden

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENTE WEB                          │
│                                                         │
│  Usuario hace clic en "Pesaje Inicial"                 │
└─────────────┬───────────────────────────────────────────┘
              │
              │ POST /api/v1/ordenes/123/registrar-pesaje-inicial
              ▼
┌─────────────────────────────────────────────────────────┐
│              SERVIDOR (Spring Boot)                     │
│                                                         │
│  1️⃣  OrdenRestController.registrarPesajeInicial()      │
└─────────────┬───────────────────────────────────────────┘
              │
              │
              ▼
┌─────────────────────────────────────────────────────────┐
│         OrdenBusiness.registrarPesajeInicial()          │
│                                                         │
│  - Obtiene orden de BD                                 │
│  - Valida estado = RECIBIDA                           │
│  - Actualiza a REGISTRADA_PESAJE_INICIAL              │
│  - Guarda en BD                                        │
└─────────────┬───────────────────────────────────────────┘
              │
              │ webSocketService.notifyOrdenStatusChanged(
              │   codExt: "ORD-2024-001",
              │   estado: RECIBIDA → REGISTRADA_PESAJE_INICIAL,
              │   usuario: "juan@logistica.com"
              │ )
              ▼
┌─────────────────────────────────────────────────────────┐
│            WebSocketService                             │
│                                                         │
│  - Construye OrdenStatusMessage                        │
│  - Llama a controller.notifyOrdenStatusChange()        │
└─────────────┬───────────────────────────────────────────┘
              │
              │ webSocketController.notifyOrdenStatusChange(message)
              ▼
┌─────────────────────────────────────────────────────────┐
│          WebSocketController                            │
│                                                         │
│  - messagingTemplate.convertAndSend(                   │
│      "/topic/ordenes/estado",                          │
│      message                                           │
│    )                                                   │
│                                                         │
│  - messagingTemplate.convertAndSend(                   │
│      "/topic/ordenes/ORD-2024-001",                    │
│      message                                           │
│    )                                                   │
└─────────────┬───────────────────────────────────────────┘
              │
              │ STOMP MESSAGE frame
              │
     ┌────────┴────────┐
     │                 │
     ▼                 ▼
Topic Broadcast    Topic Específico
/topic/ordenes/    /topic/ordenes/
     estado        ORD-2024-001
     │                 │
     └────────┬────────┘
              │
     ┌────────┴──────────────┐
     │                       │
     ▼                       ▼
Cliente 1            Cliente 2
(Suscrito a         (Suscrito a
 todas)             ORD-2024-001)
     │                       │
     │ ✅ Recibe mensaje     │ ✅ Recibe mensaje
     │                       │
     ▼                       ▼
  UI Update              UI Update
  Panel de           Panel específico
  notificaciones     de la orden
```

## 3. Estructura de Paquetes

```
websocket/
│
├── WebSocketConfig.java
│   └── Configura STOMP, broker, endpoints
│
├── WebSocketController.java
│   ├── @MessageMapping("/ordenes/subscribe/{codigoExterno}")
│   ├── @MessageMapping("/ordenes/subscribe-all")
│   ├── @MessageMapping("/ordenes/test")
│   └── notifyOrdenStatusChange(message)
│
├── WebSocketService.java
│   └── notifyOrdenStatusChanged(...)
│
├── message/
│   └── OrdenStatusMessage.java (DTO)
│
└── example/
    └── OrdenBusinessIntegrationExample.java (referencia)
```

## 4. Ciclo de Vida de una Conexión

```
┌────────────────┐
│   DISCONNECT   │◄─────────────────────────┐
└────────┬───────┘                          │
         │                                  │ Error o desconexión
         │                                  │
         ▼                                  │
┌────────────────┐     ┌──────────────┐    │
│   CONNECTING   │────►│   CONNECTED  │────┤
└────────────────┘     └──────┬───────┘    │
                              │            │
                              │ SUBSCRIBE  │
                              │ /topic/*   │
                              │            │
                              ▼            │
                       ┌──────────────┐    │
                       │  LISTENING   │────┤
                       │ /topic/*     │    │
                       └──────┬───────┘    │
                              │            │
                              │ Receive    │
                              │ MESSAGE    │
                              │            │
                              ▼            │
                       ┌──────────────┐    │
                       │    MESSAGE   │    │
                       │   RECEIVED   │    │
                       │ Update UI    │    │
                       └──────┬───────┘    │
                              │            │
                              └────────────┘
```

## 5. Patrones de Suscripción

### Patrón 1: Todas las Órdenes (Broadcast)

```javascript
// Cliente
stompClient.subscribe('/topic/ordenes/estado', callback);

// Servidor
webSocketController.notifyOrdenStatusChange(message);
  └─► messagingTemplate.convertAndSend("/topic/ordenes/estado", message)
      ├─► Cliente 1
      ├─► Cliente 2
      ├─► Cliente 3
      └─► Cliente N
```

**Casos de uso:**
- 📊 Dashboard ejecutivo
- 📈 Monitor de todas las operaciones
- 🚨 Alertas críticas

### Patrón 2: Orden Específica

```javascript
// Cliente
stompClient.subscribe('/topic/ordenes/ORD-2024-001', callback);

// Servidor
webSocketController.notifyOrdenStatusChange(message);
  └─► messagingTemplate.convertAndSend(
      "/topic/ordenes/ORD-2024-001", message
    )
      ├─► Cliente 1 (ORD-2024-001)
      ├─► Cliente 2 (ORD-2024-001)
      └─► ✗ Cliente 3 (otras órdenes) NO recibe
```

**Casos de uso:**
- 👤 Seguimiento individual del cliente
- 🚚 Conductor monitoreando su carga
- 📱 App móvil de una orden específica

### Patrón 3: Híbrido

```javascript
// Cliente
stompClient.subscribe('/topic/ordenes/estado', callback1);
stompClient.subscribe('/topic/ordenes/ORD-2024-001', callback2);

// Recibe:
// ✓ Todos los cambios generales
// ✓ Todos los cambios de ORD-2024-001
```

## 6. Manejo de Errores

```
┌──────────────┐
│   CONNECT    │
└──────┬───────┘
       │
       ▼
   Timeout
       │
       ▼
┌──────────────┐          ┌─────────────────┐
│  Error Frame │─────────►│ Reconnect Logic │
└──────┬───────┘          └────────┬────────┘
       │                           │
       │ ◄───────────────────────────
       │
       ▼
┌──────────────┐
│  Exponential │
│  Backoff     │
│  1s, 2s,     │
│  4s, 8s...   │
└──────┬───────┘
       │
       ▼
   Max Retries?
       │
   ┌───┴───┐
   │       │
   No      Yes
   │       │
   │       ▼
   │   ┌────────────┐
   │   │ User Alert │
   │   │ "Reconectar│
   │   │  manualmente
   │   └────────────┘
   │
   ▼
┌──────────────┐
│   CONNECT    │
└──────────────┘
```

## 7. Mensajes de Ejemplo

### Ejemplo 1: Cambio de Estado Normal
```json
{
  "codigoExterno": "ORD-2024-001",
  "idOrden": 1,
  "estadoAnterior": "RECIBIDA",
  "estadoActual": "REGISTRADA_PESAJE_INICIAL",
  "timestamp": 1699999999999,
  "mensaje": "Pesaje inicial registrado correctamente",
  "usuario": "juan.perez@logistica.com"
}
```

### Ejemplo 2: Cancelación de Orden
```json
{
  "codigoExterno": "ORD-2024-001",
  "idOrden": 1,
  "estadoAnterior": "REGISTRADA_PESAJE_INICIAL",
  "estadoActual": "CANCELADA",
  "timestamp": 1699999999999,
  "mensaje": "Orden CANCELADA - Cliente canceló por cambio de ruta",
  "usuario": "admin@logistica.com"
}
```

### Ejemplo 3: Pesaje Final
```json
{
  "codigoExterno": "ORD-2024-001",
  "idOrden": 1,
  "estadoAnterior": "CERRADA",
  "estadoActual": "REGISTRADA_PESAJE_FINAL",
  "timestamp": 1699999999999,
  "mensaje": "Pesaje final: 5500.25 kg, Diferencia: +45.25 kg",
  "usuario": "carlos.gomez@logistica.com"
}
```

## 8. Performance

### Latencia Esperada
```
Cliente WebSocket
      │
      │ 1-2ms (red local)
      ▼
Spring Boot
      │
      │ <1ms (procesamiento)
      ▼
STOMP Broker
      │
      │ <1ms (broadcasting)
      ▼
Cliente Recibe
      │
      │ Total: < 5ms ✅
      ▼
```

### Escalabilidad
```
1 instancia (local)
├─ 1000 conexiones ✅
└─ <500MB RAM

3 instancias (producción)
├─ 10,000 conexiones ✅
└─ Balanceador + Redis broker (para distribuir)
```

## 9. Comparación: REST vs WebSocket

| Aspecto | REST | WebSocket |
|---------|------|-----------|
| **Conexión** | Request-Response | Persistente bidireccional |
| **Iniciador** | Solo cliente | Ambos |
| **Latencia** | 100-500ms | 1-5ms |
| **Overhead HTTP** | Si | No (después de upgrade) |
| **Polling** | Requerido | No necesario |
| **Casos de uso** | CRUD, consultas | Tiempo real |
| **Complejidad** | Baja | Media |

### Ejemplo Comparativo

```
REST: Monitoreo de órdenes cada 2 segundos
┌─────────────────────────────────────────────────────────────────┐
│ GET /api/v1/ordenes     GET /api/v1/ordenes     GET /api/v1/... │
├────────────────────────┬───────────────────────┬────────────────┤
│ 2000ms                 │ 2000ms                │ 2000ms         │
├────────────────────────┼───────────────────────┼────────────────┤
│ Total: 6000ms                                                   │
│ Cambio: Si ocurre en segundo 1.5, espero 500ms más             │
│ Tráfico: Innecesario si no hay cambios                         │
└─────────────────────────────────────────────────────────────────┘

WebSocket: Notificación en tiempo real
┌─────────────────────────────────────────────────────────────────┐
│ Cambio ocurre                                                   │
│ └─► Notificación enviada < 5ms                                 │
│ └─► Cliente recibe inmediatamente                              │
│ └─► UI actualiza                                               │
│ Total: < 5ms                                                   │
│ Tráfico: Solo cuando hay cambios                               │
└─────────────────────────────────────────────────────────────────┘
```

---

**Conclusión:** Para monitoreo de órdenes en tiempo real, WebSocket es **mucho más eficiente** que REST.
