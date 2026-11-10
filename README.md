# Módulo Integrante 1 — Gestión de Eventos (CRUD + Reporte)

Módulo desarrollado con **JavaFX + MySQL + JasperReports**, siguiendo la arquitectura **MVC + DAO** con separación estricta de responsabilidades (ninguna consulta SQL en controladores ni vistas FXML).

## Estructura del proyecto

```
G01_PREHACKATON_T01/
├── db_eventos.sql                  # Script SQL: crea BD db_eventos y tabla eventos
├── pom.xml                         # Descriptor Maven (opcional)
├── README.md
└── src/main/
    ├── java/com/prehackaton/eventos/
    │   ├── Main.java               # Punto de entrada JavaFX Application
    │   ├── model/
    │   │   └── Evento.java         # Entidad Evento (id_evento, nombre, descripcion, fecha, lugar, modalidad, estado)
    │   ├── dao/
    │   │   ├── EventoDAO.java      # Interfaz DAO
    │   │   └── EventoDAOImpl.java  # Implementación JDBC (todas las consultas SQL)
    │   ├── controller/
    │   │   └── EventoController.java # Lógica de control y validaciones
    │   └── util/
    │       ├── DBConnection.java   # Conexión JDBC reutilizable
    │       └── ReportUtil.java     # Ejecución de reporte JasperReports
    └── resources/com/prehackaton/eventos/
        ├── view/
        │   └── EventoView.fxml     # Vista JavaFX (BorderPane, VBox, HBox, GridPane, TableView)
        ├── report/
        │   └── reporte_eventos.jrxml # Plantilla JasperReports
        └── style.css               # Estilos modernos de la UI
```

## Requisitos previos

- **JDK 17.0.20** (configura `JAVA_HOME` apuntando a esta versión antes de compilar/ejecutar).
- **JavaFX SDK 17.0.2** (no viene incluido en JDK 11+): descargar de https://gluonhq.com/products/javafx/
- **MySQL Connector/J** (JDBC): `mysql-connector-j-8.x.x.jar`
- **JasperReports 6.x** con sus dependencias (`.jar` de jasperreports, commons-*, etc.)
- **MySQL Server** corriendo en `localhost:3306` con la base de datos `db_eventos`.

## Base de datos

Ejecutar el script en la raíz del proyecto antes de lanzar la aplicación:

```sql
-- En MySQL (root o usuario con privilegios)
source db_eventos.sql
-- o copiar/pegar el contenido de db_eventos.sql
```

Esto crea la base de datos `db_eventos` y la tabla `eventos` con PK `id_evento` auto-incremental e inserta datos de ejemplo.

Credenciales usadas por `DBConnection.java` (ajustar si es necesario):
- URL: `jdbc:mysql://localhost:3306/db_eventos`
- Usuario: `root`
- Contraseña: `(vacía)`

## Compilación y ejecución

Como **no hay Maven instalado** en este entorno, se compila con `javac` y se ejecuta con `java`.

1. Crear una carpeta `lib/` y colocar en ella los JARs requeridos:
   - JavaFX SDK: `javafx.controls.jar`, `javafx.fxml.jar` (y los módulos nativos según el SO)
   - MySQL: `mysql-connector-j-8.x.x.jar`
   - JasperReports: `jasperreports-6.x.x.jar` + dependencias (`commons-beanutils`, `commons-collections`, `commons-digester`, `commons-logging`, `groovy`, `itext`, etc.)

2. Compilar (desde la raíz del proyecto, PowerShell). Asegúrate de que `java -version` muestre **17.0.20**:

```powershell
$LIB = "lib\*"
mkdir -p out
javac --release 17 --module-path $LIB --add-modules javafx.controls,javafx.fxml -cp "$LIB" -d out $(Get-ChildItem -Recurse src\main\java -Filter *.java | ForEach-Object { $_.FullName })
```

3. Ejecutar:

```powershell
java --release 17 --module-path "lib;out" --add-modules javafx.controls,javafx.fxml -cp "out;$LIB" com.prehackaton.eventos.Main
```

*(Si se instala Maven, simplemente: `mvn clean javafx:run` — el `pom.xml` ya está configurado para Java 17 y JavaFX 17.0.2)*

## Funcionalidades incluidas (Integrante 1)

- **CRUD de Eventos:** creación, actualización, listado en tabla y eliminación.
- **Validaciones de calidad de datos:** campos obligatorios, formato de fecha (no anterior a hoy), control de duplicados por nombre, longitud máxima de campos.
- **Diálogos de feedback:** Alert de información, advertencia y error; confirmación previa a eliminación.
- **UI moderna:** BorderPane, VBox, HBox, GridPane, TableView con columnas, DatePicker, ComboBox (modalidad/estado), botones estilizados y hoja de estilos CSS.
- **Reporte JasperReports:** botón "Generar Reporte (PDF)" que muestra el reporte tabular en JasperViewer y permite exportar a PDF. El reporte usa `JRBeanCollectionDataSource` (sin SQL en el reporte).

## Nota sobre dependencias

Este repositorio entrega únicamente el **código fuente**. Los JARs de JavaFX, MySQL Connector/J y JasperReports deben agregarse manualmente en la carpeta `lib/` (o resolverse mediante Maven con el `pom.xml` incluido) para compilar y ejecutar la aplicación.
