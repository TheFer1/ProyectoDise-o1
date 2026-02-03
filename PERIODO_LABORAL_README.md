# Actualización: Campos de Periodo Laboral

## Descripción
Se han agregado dos nuevos campos al formulario de registro de ayudantes para capturar el periodo laboral (fecha desde y fecha hasta).

## Cambios Realizados

### 1. Base de Datos
**Archivo**: `data/alter_periodo_laboral.sql`

Para actualizar tu base de datos, ejecuta los siguientes comandos SQL:

```sql
ALTER TABLE formularios ADD COLUMN periodo_laboral_desde TEXT;
ALTER TABLE formularios ADD COLUMN periodo_laboral_hasta TEXT;
```

**Nota**: Si estás usando una base de datos existente, debes ejecutar estos comandos. Si vas a crear una base de datos nueva, agrega estas columnas en el script `data.sql` directamente.

### 2. Archivos Modificados

#### a) **Formulario.java**
- ✅ Agregados atributos `periodoLaboralDesde` y `periodoLaboralHasta`
- ✅ Agregados getters y setters
- ✅ Actualizado método `toString()`

#### b) **FormularioDAO.java**
- ✅ Actualizado método `insertar()` para guardar las fechas
- ✅ Actualizado método `actualizar()` para modificar las fechas
- ✅ Actualizados métodos `obtenerTodos()`, `obtenerPorId()`, `buscarPorEstado()`, `obtenerFormulariosPorUsuario()` y `buscarPorProyecto()` para cargar las fechas

#### c) **ExtractorPDF.java**
- ✅ Agregado método `extraerPeriodoLaboralDesdePDF()` para extraer la fecha "DESDE:" del PDF
- ✅ Agregado método `extraerPeriodoLaboralHastaPDF()` para extraer la fecha "HASTA:" del PDF

#### d) **SistemaGestionWindow.java**
- ✅ Agregados campos de texto `txtPeriodoDesde` y `txtPeriodoHasta` al formulario
- ✅ Agregado botón "📄 Extraer desde PDF" para extraer las fechas automáticamente
- ✅ Actualizado evento de guardar para incluir las fechas del periodo laboral
- ✅ Actualizado evento de limpiar para resetear los campos de fecha

## Uso

### Registro Manual
1. Ingresa al sistema como Director
2. Ve a la pestaña "Formularios"
3. Completa todos los campos del formulario, incluyendo:
   - **Periodo Laboral Desde**: Fecha de inicio (ej: 1/2/2026)
   - **Periodo Laboral Hasta**: Fecha de fin (ej: 30/11/2026)
4. Haz clic en "✓ Guardar"

### Extracción desde PDF
1. Completa los demás campos del formulario
2. Haz clic en el botón "📄 Extraer desde PDF"
3. Selecciona el archivo PDF que contiene la información
4. El sistema extraerá automáticamente las fechas que contengan las palabras "DESDE:" y "HASTA:"
5. Verifica que las fechas se hayan extraído correctamente
6. Haz clic en "✓ Guardar"

## Formato de Fechas en PDF

El extractor buscará en el PDF las siguientes palabras clave:
- **DESDE:** seguido de la fecha de inicio
- **HASTA:** seguido de la fecha de fin

Ejemplo de texto en PDF:
```
PERÍODO LABORAL    DESDE: 1/2/2026    HASTA: 30/11/2026
```

## Notas Importantes

1. **Base de Datos**: Asegúrate de ejecutar el script SQL antes de usar la nueva funcionalidad
2. **Formato de Fechas**: Los campos aceptan texto libre, por lo que puedes usar cualquier formato (DD/MM/YYYY, MM/DD/YYYY, etc.)
3. **Campos Opcionales**: Los campos de periodo laboral son opcionales, no es obligatorio completarlos
4. **PDF**: El extractor funciona mejor si el PDF contiene texto (no imágenes escaneadas)

## Validación

Para verificar que todo funciona correctamente:

1. ✅ La base de datos tiene las nuevas columnas
2. ✅ El formulario muestra los campos "Periodo Laboral Desde" y "Periodo Laboral Hasta"
3. ✅ El botón "Extraer desde PDF" está visible
4. ✅ Se pueden guardar registros con las fechas
5. ✅ Las fechas se visualizan correctamente en la tabla de formularios

## Solución de Problemas

### Error al guardar: "no such column: periodo_laboral_desde"
**Solución**: Ejecuta el script SQL de actualización en tu base de datos

### El botón de extraer PDF no funciona
**Solución**: Verifica que el PDF contenga las palabras "DESDE:" y "HASTA:" en mayúsculas

### Las fechas no se guardan
**Solución**: Verifica que los campos no estén vacíos antes de guardar
