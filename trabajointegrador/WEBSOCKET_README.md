# 📡 WebSocket - Ejemplo de Notificaciones en Tiempo Real

Este ejemplo implementa un sistema de notificaciones en **tiempo real** utilizando **WebSocket STOMP** para notificar cambios de estado en las órdenes de carga.

## 🎯 ¿Qué es WebSocket?

WebSocket es un protocolo que permite comunicación **bidireccional en tiempo real** entre cliente y servidor. Perfecto para:

- ✅ Notificaciones en tiempo real
- ✅ Chat en vivo
- ✅ Actualizaciones de estado
- ✅ Monitoreo de procesos
- ✅ Dashboards dinámicos

## 📦 Tecnologías Utilizadas

- **Spring Boot WebSocket Starter** - Soporte nativo para WebSocket
- **STOMP** (Simple Text Oriented Messaging Protocol) - Protocolo de mensajería
- **SockJS** - Fallback a otras técnicas si WebSocket no está soportado
- **Jackson** - Serialización/Deserialización JSON

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────┐
│          Cliente Web (HTML/JS)              │
│  (websocket-example.html)                   │
└──────────────┬──────────────────────────────┘
               │ WebSocket STOMP
               │ (ws://host:port/api/v1/ws/ordenes)
               ▼
┌──────────────────────────────────────────────┐
│         WebSocketConfig                      │
│  - Configura endpoints                       │
│  - Habilita broker de mensajes              │
└──────────────┬───────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────┐
│       WebSocketController                    │
│  - Maneja suscripciones                     │
│  - Procesa mensajes del cliente             │
│  - Notifica cambios (/topic/ordenes/*)     │
└──────────────┬───────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────┐
│        WebSocketService                      │
│  - Interfaz para negocio                     │
│  - Construye mensajes de notificación       │
│  - Disparador desde OrdenBusiness           │
└──────────────────────────────────────────────┘
```

## 📂 Estructura de Archivos

```
websocket/
├── WebSocketConfig.java              # Configuración STOMP
├── WebSocketController.java           # Endpoints de suscripción
├── WebSocketService.java              # Servicio de notificaciones
└── message/
    └── OrdenStatusMessage.java       # DTO para mensajes

resources/
└── websocket-example.html            # Cliente de prueba
```

## 🚀 Cómo Usar

### 1. **Cliente de Prueba (HTML)**

Abre el archivo `websocket-example.html` en tu navegador:

```
http://localhost:8080/websocket-example.html
```

O ubica el archivo en:
```
/src/main/resources/websocket-example.html
```

### 2. **Conectar al Servidor**

1. Ingresa la URL del servidor (por defecto: `http://localhost:8080`)
2. Haz clic en **"Conectar"**
3. Verás un mensaje: "● Conectado"

### 3. **Suscribirse a Notificaciones**

#### Opción A: Orden Específica
```javascript
// En el cliente:
1. Ingresa código externo (ej: ORD-2024-001)
2. Click "Suscribirse a Orden Específica"
3. Espera notificaciones de esa orden
```

#### Opción B: Todas las Órdenes
```javascript
// En el cliente:
1. Click "Suscribirse a Todas las Órdenes"
2. Recibe notificaciones de cualquier cambio
```

### 4. **Enviar Mensaje de Prueba**

Haz clic en **"Enviar Mensaje de Prueba"** para verificar que todo funciona.

## 💻 Integración en el Código Backend

### Desde OrdenBusiness (Cuando Cambias Estado)

```java
@Autowired
private WebSocketService webSocketService;

// En un método que cambia el estado:
public void cambiarEstadoOrden(Long idOrden, String nuevoEstado) throws Exception {
    Orden orden = repository.findById(idOrden).orElseThrow();
    String estadoAnterior = orden.getEstado();
    
    // ... lógica de cambio ...
    orden.setEstado(nuevoEstado);
    repository.save(orden);
    
    // 🔔 Notificar a través de WebSocket
    webSocketService.notifyOrdenStatusChanged(
        orden.getCodExt(),
        orden.getId(),
        estadoAnterior,
        nuevoEstado,
        "usuario@sistema",
        "Cambio de estado realizado"
    );
}
```

### Endpoints WebSocket (Cliente → Servidor)

| Endpoint | Función | Parámetros |
|----------|---------|-----------|
| `/app/ordenes/subscribe/{codigoExterno}` | Suscribirse a orden | codigoExterno |
| `/app/ordenes/subscribe-all` | Suscribirse a todas | — |
| `/app/ordenes/test` | Enviar mensaje de prueba | OrdenStatusMessage |

### Tópicos de Suscripción (Servidor → Cliente)

| Tópico | Recibe |
|--------|--------|
| `/topic/ordenes/estado` | Cambios de estado de cualquier orden |
| `/topic/ordenes/{codigoExterno}` | Cambios de una orden específica |

## 📨 Estructura del Mensaje

```json
{
  "codigoExterno": "ORD-2024-001",
  "idOrden": 123,
  "estadoAnterior": "RECIBIDA",
  "estadoActual": "REGISTRADA_PESAJE_INICIAL",
  "timestamp": 1699999999999,
  "mensaje": "Pesaje inicial completado",
  "usuario": "chofer.juan@logistica.com"
}
```

## 🔧 Configuración Adicional

### En `application.properties` (Opcional)

```properties
# WebSocket
spring.websocket.allowed-origins=*
spring.websocket.stomp.enabled=true

# Logging
logging.level.ar.iua.edu.trabajointegrador.websocket=DEBUG
```

### CORS (Cross-Origin Resource Sharing)

Actualmente, está configurado para aceptar conexiones desde cualquier origen:

```java
registry.addEndpoint(Constants.URL_BASE + "/ws/ordenes")
    .setAllowedOrigins("*")  // ⚠️ En producción, especifica dominios
```

**Para producción:**
```java
.setAllowedOrigins("https://tudominio.com", "https://www.tudominio.com")
```

## 🧪 Pruebas

### Con Postman/Insomnia (WebSocket)

1. Nueva request → WebSocket
2. URL: `ws://localhost:8080/api/v1/ws/ordenes`
3. Conectar
4. En la consola STOMP:
```
SUBSCRIBE
id:1
destination:/topic/ordenes/estado

```

### Con curl (No recomendado para WebSocket, solo REST)

```bash
# Esto no funcionará para WebSocket
# WebSocket requiere una conexión persistente
```

## 📊 Monitoreo en Tiempo Real

El cliente HTML muestra:

- ✅ **Estado de conexión** - Conectado/Desconectado
- ✅ **Mensajes recibidos** - Histórico completo
- ✅ **Estadísticas** - Contadores de eventos
- ✅ **Cambios de estado** - Detalle de cada cambio

## 🐛 Troubleshooting

### "No se conecta"

1. Verifica que el servidor esté corriendo
2. Comprueba la URL: `http://localhost:8080`
3. Revisa la consola del navegador (F12)
4. Verifica CORS en FireFox (puede ser más restrictivo)

### "Recibe mensajes pero no se ve en el cliente"

1. Verifica suscripciones:
```javascript
// En consola del navegador:
stompClient.debug = function(msg) { console.log(msg); }
```

2. Revisa logs del servidor:
```bash
grep "WebSocket\|STOMP" logs/output.log
```

### "Error: STOMP is not defined"

Asegúrate que los scripts están cargados en `websocket-example.html`:
```html
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
```

## 📚 Recursos Adicionales

- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [STOMP Protocol](https://stomp.github.io/stomp-specification-1.2.html)
- [SockJS Client](https://github.com/sockjs/sockjs-client)

## 🎓 Conceptos Clave

| Concepto | Explicación |
|----------|------------|
| **WebSocket** | Protocolo bidireccional, mantiene conexión abierta |
| **STOMP** | Protocolo de mensajería sobre WebSocket |
| **SockJS** | Fallback si WebSocket no está disponible |
| **/topic/\*** | Broadcast a múltiples clientes |
| **/app** | Prefix para mensajes del cliente al servidor |
| **Suscripción** | El cliente se registra para recibir mensajes |

## ✅ Checklist de Implementación

- [x] Configurar WebSocket STOMP
- [x] Crear controlador de WebSocket
- [x] Crear servicio de notificaciones
- [x] Crear DTO para mensajes
- [x] Cliente HTML funcional
- [x] Documentación completa

## 🚀 Próximos Pasos

1. **Integrar en OrdenBusiness** - Llamar a `webSocketService.notifyOrdenStatusChanged()` cuando cambies estado
2. **Autenticación** - Proteger endpoints WebSocket con JWT
3. **Persistencia** - Guardar historial de cambios
4. **Alertas** - Notificaciones por email/SMS en cambios críticos
5. **Dashboard** - UI para monitorear órdenes en tiempo real

---

**¡Listo para empezar!** 🚀
