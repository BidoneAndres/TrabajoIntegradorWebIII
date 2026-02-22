# ⚡ WebSocket - Quick Start

## 🚀 3 Pasos para Empezar

### 1️⃣ Abre el Cliente de Prueba
```
http://localhost:8080/websocket-example.html
```

### 2️⃣ Conecta al Servidor
```
Click en botón "Conectar"
└─► Verás: "● Conectado"
```

### 3️⃣ Suscríbete a Notificaciones
```
Opción A: Orden Específica
  - Ingresa: ORD-2024-001
  - Click: "Suscribirse a Orden Específica"

Opción B: Todas las Órdenes
  - Click: "Suscribirse a Todas las Órdenes"
```

---

## 📂 Archivos Creados

```
✅ WebSocketConfig.java               - Configuración (STOMP, broker, endpoints)
✅ WebSocketController.java           - Controlador (suscripciones, mensajes)
✅ WebSocketService.java              - Servicio (notificaciones)
✅ message/OrdenStatusMessage.java    - DTO (estructura de mensaje)
✅ websocket-example.html             - Cliente de prueba (UI bonita)
✅ example/OrdenBusinessIntegrationExample.java - Ejemplos de integración
✅ WEBSOCKET_README.md                - Documentación completa
✅ WEBSOCKET_FLOWS.md                 - Diagramas y flujos
✅ WEBSOCKET_QUICKSTART.md            - Este archivo
```

---

## 💻 Integración en tu Código

### En OrdenBusiness.java

```java
// 1. Agregar inyección
@Autowired
private WebSocketService webSocketService;

// 2. En método que cambia estado:
public void cambiarEstadoOrden(Long idOrden, String nuevoEstado, String usuario) {
    Orden orden = repository.findById(idOrden).orElseThrow();
    String estadoAnterior = orden.getEstado();
    
    // ... lógica ...
    orden.setEstado(nuevoEstado);
    repository.save(orden);
    
    // 🔔 ¡ESTO ES TODO LO QUE NECESITAS AGREGAR!
    webSocketService.notifyOrdenStatusChanged(
        orden.getCodExt(),      // ORD-2024-001
        orden.getId(),          // 1
        estadoAnterior,         // RECIBIDA
        nuevoEstado,            // REGISTRADA_PESAJE_INICIAL
        usuario,                // juan@logistica.com
        "Cambio de estado"      // Mensaje
    );
}
```

---

## 🎯 Endpoints WebSocket

### Cliente → Servidor

| Endpoint | Función |
|----------|---------|
| `/app/ordenes/subscribe/{codigoExterno}` | Suscribirse a orden específica |
| `/app/ordenes/subscribe-all` | Suscribirse a todas |
| `/app/ordenes/test` | Enviar mensaje de prueba |

### Servidor → Cliente (Topics)

| Topic | Recibe |
|-------|--------|
| `/topic/ordenes/estado` | Cualquier cambio |
| `/topic/ordenes/{codigoExterno}` | Cambios de esa orden |

---

## 📊 Flujo Visual

```
USUARIO                    SERVIDOR
  │                          │
  │ 1. Conectar              │
  ├─────────────────────────►│
  │                          │ WebSocketConfig
  │                          │ WebSocketController
  │                          │
  │ 2. Suscribirse           │
  ├─────────────────────────►│ /topic/ordenes/estado
  │                          │
  │ 3. Cambio de estado      │
  │ (otro usuario)           │ OrdenBusiness
  │                          │ → cambiarEstado()
  │                          │ → webSocketService.notify()
  │ 4. NOTIFICACIÓN          │
  │◄─────────────────────────┤ ENVIADA EN TIEMPO REAL
  │ ¡Estado cambió!          │
  │                          │
```

---

## ✅ Checklist de Verificación

- [ ] Los archivos están creados sin errores
- [ ] `websocket-example.html` se abre en navegador
- [ ] Puedes clickear "Conectar" sin errores
- [ ] Ves "● Conectado" en verde
- [ ] Puedes suscribirte a órdenes
- [ ] Puedes enviar mensaje de prueba
- [ ] Ves mensaje recibido en panel de notificaciones
- [ ] Agregaste WebSocketService a OrdenBusiness
- [ ] Llamas a `webSocketService.notifyOrdenStatusChanged()` en cambios

---

## 🐛 Si Algo No Funciona

### Error: "No se conecta"
```
1. Verifica que Spring Boot esté corriendo en puerto 8080
2. Abre consola del navegador (F12) y revisa errores
3. Intenta con http://localhost:8080/websocket-example.html
```

### Error: "WebSocket connection failed"
```
1. Revisa logs del servidor
2. Verifica que WebSocketConfig esté anotada con @Configuration
3. Intenta reiniciar el servidor
```

### No recibe mensajes
```
1. Verifica que estés suscrito (deberías ver "Suscrito a...")
2. Intenta enviar mensaje de prueba (botón "Enviar Mensaje de Prueba")
3. Revisa que OrdenBusiness llame a webSocketService.notify()
```

---

## 📚 Recursos

- 📖 Documentación completa: `WEBSOCKET_README.md`
- 📊 Diagramas y flujos: `WEBSOCKET_FLOWS.md`
- 💡 Ejemplos de código: `websocket/example/OrdenBusinessIntegrationExample.java`
- 🧪 Cliente de prueba: `websocket-example.html`

---

## 🎓 Conceptos Clave (30 segundos)

- **WebSocket** = Conexión que se mantiene abierta (no cierra después de cada mensaje)
- **STOMP** = Protocolo simple para enviar mensajes sobre WebSocket
- **/topic/\*** = Canal de broadcast (todos los suscritos reciben)
- **SockJS** = Fallback si WebSocket no está disponible

---

## 🚀 Próximo Paso

1. Abre `websocket-example.html` en navegador
2. Haz clic en "Conectar"
3. Haz clic en "Enviar Mensaje de Prueba"
4. Deberías ver una notificación aparcer en tiempo real ✅

**¡Eso es todo!** El sistema está listo para funcionar. 🎉

---

## 📞 Support

Si tienes dudas, revisa:
1. `WEBSOCKET_README.md` - Documentación detallada
2. `websocket/example/OrdenBusinessIntegrationExample.java` - Ejemplos
3. `WEBSOCKET_FLOWS.md` - Diagramas y flujos

¡Éxito! 🚀
