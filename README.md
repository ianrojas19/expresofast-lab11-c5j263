# ExpresoFast - Laboratorio 11: Formularios Reactivos Avanzados y Full-Stack

**Universidad de Costa Rica**  
**Sede del Atlántico - Recinto Paraíso**  
**Carrera de Informática Empresarial**  
**Curso:** IF0009 - Desarrollo de Software IV  
**Profesor:** Mag. Jonathan Granados C.  
**Estudiante:** Ian Rojas  

---

En este laboratorio un envío (número de tracking) puede tener varios paquetes. La aplicación en Angular 19 registra el envío y todos sus paquetes al mismo tiempo, y los guarda en SQL Server por medio de un backend en Spring Boot 3.

## Estructura del proyecto

* `expresofast-backend/`: Spring Boot 3.2 con Java 21, JPA y SQL Server. El script de la base de datos está en `expresofast-backend/sql/lab11-sqlserver.sql`.
* `expresofast-frontend/`: Angular 19. El formulario nuevo es `EnvioAvanzadoFormComponent` y está en la ruta `/nuevo-envio`.

### Backend

* Entidades `Envio` (con `@OneToMany` y `cascade = ALL`) y `Paquete` (con `@ManyToOne`, tabla `PAQUETES`).
* `EnvioRegistroDTO` recibe una lista de `PaqueteDTO` y se valida con Bean Validation.
* El método `registrarEnvio` del servicio tiene `@Transactional`, así que el envío y sus paquetes se guardan juntos y si algo falla se revierte todo.
* Endpoint `GET /api/envios/check-tracking/{trackingNumber}` (también funciona en `/api/v1/envios/...`). Devuelve `{"existe": true}` o `{"existe": false}`.
* Si el tracking ya existe responde 409. Si las fechas no son válidas o no hay paquetes responde 400.

### Frontend

* Formulario tipado con `NonNullableFormBuilder`.
* `FormArray` para los paquetes, con el botón "+ Añadir Paquete" y un botón "X" por cada paquete. La "X" se deshabilita cuando solo queda uno.
* Validador cruzado síncrono (`fechasValidator`) a nivel de `FormGroup` para comparar las fechas.
* Validador asíncrono (`trackingUnicoValidator`) que consulta el endpoint del backend y marca el error `trackingTomado`.
* No se usa `[(ngModel)]` en el formulario de envíos.

## Requisitos previos

Java 21, Maven 3.8 o superior, Node.js 18 o superior, Angular CLI y SQL Server local con `sqlcmd`.

## Cómo ejecutarlo

1. Base de datos. Este script crea la base `ExpresoFastLab11_C5J263`, las tablas `ENVIOS` y `PAQUETES`, unos datos de prueba y el usuario `lab11_user`:
   ```bash
   sqlcmd -S localhost -E -C -i expresofast-backend/sql/lab11-sqlserver.sql
   ```
2. Backend en http://localhost:8080 (las credenciales están en `src/main/resources/application.properties`):
   ```bash
   cd expresofast-backend
   mvn spring-boot:run
   ```
3. Frontend en http://localhost:4200:
   ```bash
   cd expresofast-frontend
   npm install
   ng serve
   ```

## Pruebas realizadas

Por terminal (curl): un POST válido con 2 paquetes devolvió 200 y se guardaron las filas en `ENVIOS` y `PAQUETES`. Un tracking repetido devolvió 409, fechas inválidas devolvió 400 y sin paquetes también 400. El endpoint `check-tracking` devolvió `true` o `false` según el caso.

En el navegador (`/nuevo-envio`): con el tracking `EXP-2026-1001` aparece "Este número de rastreo ya está en uso". Con las fechas al revés sale el error global y el botón de registrar queda deshabilitado. Se pueden agregar y quitar paquetes sin errores en consola. Al registrar redirige a la lista y en `/rastreo` se ven los paquetes del envío. Las capturas están en la carpeta `capturas/` (archivos `lab11-*.png`).

---

## Fundamentación teórica

### 1. UX y escalabilidad: FormArray y formularios reactivos vs. 10 campos estáticos ocultos

Un `FormArray` es un arreglo de controles o grupos que puede crecer o achicarse mientras la aplicación corre. Su validez y su valor se suman solos al `FormGroup` padre. En este laboratorio cada paquete es un `FormGroup` con descripción y peso, y cuando el usuario presiona "+ Añadir Paquete" simplemente se hace un `push` al arreglo. Al enviar el formulario, `getRawValue().paquetes` ya trae la lista lista para mandarla al API.

Si en cambio se ponen 10 campos fijos ocultos en el HTML, el modelo no representa lo que realmente pasa, porque un envío puede tener 1 paquete o 30. Habría que revisar cuáles campos están en uso, descartar los vacíos antes de enviar, y validar cada campo por separado. Además el usuario quedaría limitado a 10 paquetes, y si se necesitan más hay que modificar el código.

En cuanto a la experiencia de usuario, con el FormArray la persona solo ve los bloques que necesita, no se llena la pantalla de campos vacíos, y cada paquete se valida por su cuenta. Los campos ocultos con `display: none` siguen existiendo en el DOM y pueden causar errores de validación que el usuario no ve, y por eso no entiende por qué el botón no se habilita.

Sobre mantenibilidad, el código del paquete se escribe una sola vez (el método `crearPaquete()`) y en el HTML se recorre con `@for`, en vez de copiar y pegar el mismo bloque 10 veces. Si hay que cambiar una regla, por ejemplo el peso máximo, se cambia en un solo lugar. Al usar formularios reactivos tipados, TypeScript avisa en compilación si se intenta asignar un `string` a `pesoKg`, cosa que con `ngModel` no se detecta y el error aparece hasta que se ejecuta. También es más fácil agregar validaciones cruzadas y asíncronas, que con los formularios de plantilla son más complicadas.

### 2. Ciclo de eventos: validador de fechas (síncrono) vs. validador de tracking (asíncrono)

JavaScript solo ejecuta una cosa a la vez, en un único hilo. El código se ejecuta en el Call Stack. Las tareas que tardan, como una petición HTTP o un `setTimeout`, se le pasan al navegador (Web APIs), y cuando terminan su callback se pone en la cola de tareas (Task Queue). Las promesas usan otra cola, la de microtareas, que se atiende antes. El Event Loop revisa constantemente si el Call Stack está vacío, y solo cuando lo está toma la siguiente tarea de las colas.

El validador de fechas es síncrono porque solo compara dos valores que ya están en memoria. Se ejecuta completo dentro del Call Stack, sin esperar nada, y devuelve el resultado (`{ fechasInvalidas: true }` o `null`) en el momento. Por eso el error aparece en pantalla apenas el usuario cambia una fecha.

El validador de tracking es asíncrono porque tiene que preguntarle al backend si el número ya existe, y no se sabe cuánto va a tardar la respuesta. Si el código se quedara esperando en el Call Stack, toda la página se congelaría (no se podría escribir ni hacer clic). Por eso la petición HTTP se delega a los Web APIs, el validador devuelve de inmediato un `Observable`, y cuando llega la respuesta el Event Loop ejecuta el callback que resuelve la validación.

Angular necesita que el validador asíncrono devuelva un `Observable` o una `Promise` porque ambos representan un valor que va a estar disponible más adelante. Con eso Angular pone el control en estado `PENDING` mientras espera, se suscribe, y cuando llega el resultado le asigna el error `trackingTomado` o `null` y vuelve a calcular si el formulario es válido. Mientras está pendiente, el botón de registrar se mantiene deshabilitado. Si el validador devolviera un valor normal, Angular no tendría forma de saber que todavía falta esperar una respuesta. Otra ventaja de usar Observable es que se puede cancelar: en el código usé `timer(400)` junto con `switchMap`, así que si el usuario sigue escribiendo se cancela la petición anterior y solo se toma en cuenta la última, evitando que una respuesta vieja pise a una nueva.
