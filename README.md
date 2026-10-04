# ExpresoFast - Consola Logística SPA (Laboratorio 10)

**Universidad de Costa Rica**  
**Sede del Atlántico - Recinto Paraíso**  
**Carrera de Informática Empresarial**  
**Curso:** IF0009 - Desarrollo de Software IV  
**Profesor:** Mag. Jonathan Granados C.  
**Estudiante:** Ian Rojas  

---

Este repositorio contiene la solución completa al Laboratorio 10. La arquitectura monolítica anterior ha sido refactorizada hacia una arquitectura moderna Cliente-Servidor, constando de una **Single Page Application (SPA)** en Angular Standalone conectada a un **Backend RESTful por capas** desarrollado en Spring Boot 3.x.

## 🏗 Estructura del Proyecto

El repositorio está compuesto de dos subproyectos principales:
* `expresofast-backend/`: Proyecto Spring Boot (Java 21) que gestiona la persistencia, lógica de negocio y expone los servicios REST.
* `expresofast-frontend/`: Proyecto Angular 19 (Standalone) que consume la API e implementa la interfaz de usuario moderna (UI/UX).

## 📋 Requisitos Previos
- **Java 21** (Configurado en el entorno de variables)
- **Maven** (3.8+)
- **Node.js** (v18 o superior)
- **Angular CLI** (`npm install -g @angular/cli`)

---

## 🚀 Instrucciones de Ejecución

### 1. Inicializar el Backend (Spring Boot)
1. Abrir una terminal y navegar al directorio del backend:
   ```bash
   cd expresofast-backend
   ```
2. Compilar y ejecutar la aplicación mediante Maven:
   ```bash
   mvn clean spring-boot:run
   ```
   *(Nota: Alternativamente se puede usar `./mvnw spring-boot:run` si se usa el wrapper de Maven).*
3. El backend arrancará en **http://localhost:8080**. 
   > La base de datos H2 se inicializa en memoria y se pobla automáticamente con 4 registros de prueba (mediante el script `data.sql`).

### 2. Inicializar el Frontend (Angular)
1. Abrir una nueva pestaña en la terminal y navegar al directorio del frontend:
   ```bash
   cd expresofast-frontend
   ```
2. Instalar todas las dependencias requeridas (solo la primera vez):
   ```bash
   npm install
   ```
3. Ejecutar el servidor local de desarrollo de Angular:
   ```bash
   ng serve
   ```
4. Navegar a **http://localhost:4200** en cualquier navegador web moderno.

---

## 🧪 Pruebas de Funcionalidad (Casos de Uso para el Evaluador)

Para confirmar que la solución cumple con todos los requerimientos de la rúbrica, se le invita a seguir esta secuencia de pruebas dentro del navegador (`http://localhost:4200`):

1. **Prueba de Carga Inicial (GET & UI Reactiva)**
   - **Acción:** Abra la aplicación en la pestaña "Lista de Envíos".
   - **Resultado Esperado:** Debe observar la lista cargada dinámicamente desde la base de datos H2 con 4 registros semilla. Note las etiquetas de colores para los diferentes estados (PENDIENTE, EN_TRANSITO, etc.) y la interfaz moderna con efecto Glassmorphism.

2. **Prueba de Creación de Envíos (POST & Routing)**
   - **Acción:** Haga clic en "Registrar Envío" usando el navbar. Llene el formulario con datos de prueba (Ej: Destinatario: "Profesor Jonathan", Flete: 15000) y haga clic en registrar.
   - **Resultado Esperado:** Al enviar, la aplicación consumirá el endpoint `POST`, el backend generará automáticamente el código de rastreo (Ej. `EXP-2026-XXXX`) y lo guardará. Automáticamente, el router de Angular lo redirigirá de vuelta a la "Lista de Envíos" donde podrá observar el paquete recién creado al final de la tabla.

3. **Prueba de Actualización de Estado (PATCH)**
   - **Acción:** En la tabla de la "Lista de Envíos", ubique el registro que acaba de crear. Use el menú desplegable (`select`) a la derecha para cambiar su estado a `EN_TRANSITO`.
   - **Resultado Esperado:** Al seleccionar el nuevo valor, el sistema enviará inmediatamente un request HTTP `PATCH`. Verá como el color de la etiqueta (Badge) cambia en tiempo real a azul, confirmando la mutación exitosa en base de datos.

4. **Prueba de Rastreo Dinámico (GET by Rastreo)**
   - **Acción:** Copie el código de rastreo de cualquier paquete (Ej: `EXP-2026-1001`). Vaya a la pestaña "Rastrear Guía", pegue el código y presione Buscar.
   - **Resultado Esperado:** Se desplegará la "Ficha del Paquete" detallando la información. Se mostrará una **Barra de Progreso** reactiva que cambia su porcentaje (25%, 50%, 100%) y su color en base al estado actual del envío extraído de la API.

---

## 🛠 Arquitectura y Cambios Estructurales

- **Migración a Standalone:** La aplicación web ya no depende del `AppModule`, utilizando `provideHttpClient(withFetch())` para llamadas asíncronas modernas.
- **Arquitectura por Capas (Clean Code):** El backend migró del paquete monolítico genérico a `com.expresofast.model`, `repository`, `dto`, `service` y `controller`.
- **CORS Integrado:** Se aplicó `@CrossOrigin(origins = "http://localhost:4200")` para autorizar las solicitudes seguras del frontend al servidor 8080.
- **Diseño Premium:** Se erradicaron los estilos genéricos, diseñando un modo oscuro elegante implementando transiciones y tipografías personalizadas (Inter).
