## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## Notificador Automático (Integrado en Notificacion.java)

### Descripción
La clase **Notificacion** incluye métodos para enviar recordatorios periódicos automáticamente a todos los directores para que registren sus ayudantes.

### Características
- ✓ Envía notificaciones cada **2 minutos** automáticamente
- ✓ Guarda las notificaciones en la base de datos
- ✓ Envía correos electrónicos a cada director
- ✓ Se inicia automáticamente al ejecutar la aplicación
- ✓ Se detiene automáticamente al cerrar la aplicación

### Mensaje de Notificación
> "El Sistema de Gestión de Ayudantes le recuerda que debe registrar sus ayudantes, para que al final del semestre su firma en el avance del proyecto sea válida por Jefatura."

### Uso Programático

#### Iniciar notificador automático
```java
Notificacion.iniciarNotificadorAutomatico();
```

#### Detener notificador automático
```java
Notificacion.detenerNotificadorAutomatico();
```

#### Cambiar intervalo de notificaciones
```java
// Cambiar a 5 minutos
Notificacion.cambiarIntervaloNotificador(5);
```

### Configuración de Correo
Las credenciales SMTP se encuentran en la clase `Notificacion.java`:
- **Host SMTP**: smtp.gmail.com
- **Puerto**: 587
- **Email origen**: Configurado en `EMAIL_FROM`
- **Contraseña**: Configurada en `EMAIL_PASSWORD`

Para cambiar estas credenciales, edita los valores en `Notificacion.java`.

### Flujo de Notificaciones
1. El notificador se inicia automáticamente al ejecutar la aplicación
2. Cada 2 minutos, obtiene la lista de todos los directores
3. Para cada director:
   - Crea una notificación en la base de datos
   - Intenta enviar un correo electrónico
   - Registra los resultados en la consola
4. Al cerrar la aplicación, se detiene automáticamente

## Validación de Límite de Ayudantes

### Descripción
El sistema valida automáticamente que **no se puedan registrar más ayudantes de los permitidos** en cada proyecto.

### Características
- ✓ Validación automática al registrar formularios
- ✓ Verifica límite configurado en cada proyecto
- ✓ Cuenta ayudantes ya registrados
- ✓ Rechaza registros que excedan el límite
- ✓ Muestra cupos disponibles en tiempo real

### Funcionamiento
```java
// El método registrarFormulario() valida automáticamente
Director director = new Director();
boolean resultado = director.registrarFormulario(formulario);

// Si hay cupo: ✓ Formulario registrado (2/3, queda 1 cupo)
// Si no hay cupo: ❌ Error: Límite de 3 ayudantes alcanzado
```

### Consultar Cupos Disponibles
```java
int cupos = director.verificarCuposDisponibles(idProyecto);
System.out.println("Cupos disponibles: " + cupos);
```

Para más detalles, consulta [VALIDACION_AYUDANTES.md](VALIDACION_AYUDANTES.md)

## Pantalla de Información del Sistema

### Descripción
Después de iniciar sesión, los usuarios ven una pantalla informativa que explica:
- **⚠️ Advertencia**: Si no registra ayudantes, no podrá subir su avance semestral
- **Restricción**: Jefatura no firmará documentos sin ayudantes registrados
- **Instrucciones**: Pasos detallados para registrar ayudantes
- **Información**: Detalles sobre el funcionamiento del sistema

### Características
- ✓ Se muestra automáticamente después del login
- ✓ Interfaz visualmente clara y organizada por colores
- ✓ Contenido scroll para leer toda la información
- ✓ Botón "Entendido, Continuar" para acceder al sistema
- ✓ Personalizada con nombre del usuario

### Secciones Principales

#### 1. ⚠️ Advertencia Importante
Advierte que sin registrar ayudantes:
- No podrá subir el avance semestral
- Jefatura no le firmará los documentos

#### 2. 📋 ¿Qué es este Sistema?
Explica los beneficios y funcionalidades del sistema

#### 3. 📝 Pasos para Registrar Ayudantes
Proporciona 7 pasos detallados:
1. Inicie sesión
2. Vaya a "Mis Proyectos"
3. Seleccione el proyecto
4. Haga clic en "Registrar Nuevo Ayudante"
5. Complete los datos del ayudante
6. Guarde el registro
7. Confirme

#### 4. 💡 Información Adicional
Detalles sobre:
- Límites de ayudantes por proyecto
- Notificaciones automáticas
- Estado de registro en tiempo real

Para más detalles, consulta [PANTALLA_INFORMACION_SISTEMA.md](PANTALLA_INFORMACION_SISTEMA.md)


