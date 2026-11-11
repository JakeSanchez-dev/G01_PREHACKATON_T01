# 🎓 Sistema de Gestión de Eventos y Conferencias — Equipo G01

**App de escritorio JavaFX** para el control de ponencias, congresos y registro de asistentes, desarrollada como **Pre-Hackatón** en modalidad colaborativa.

`Java 17` · `JavaFX 17` · `MySQL 8 (JDBC)` · `JasperReports 6` · `Maven` · Arquitectura `MVC + DAO`

---

## 👥 Equipo y módulos

Trabajo grupal integrado en **un solo ejecutable** con navegación fluida entre módulos:

| Integrante | Módulo | Responsabilidades |
|---|---|---|
| **Integrante 1** — *[completar nombre]* | 📅 **Eventos** | CRUD completo, validaciones, búsqueda, reporte JasperReports |
| **Integrante 2** — *[completar nombre]* | 👥 **Participantes** | CRUD completo, validaciones, búsqueda, reporte JasperReports |

> ✏️ Editar esta tabla y poner los nombres reales de los integrantes.

---

## 🧱 Arquitectura (MVC + DAO)

Separación estricta de responsabilidades: **ninguna consulta SQL en controladores ni vistas FXML** — todo el SQL vive en la capa DAO.

```
┌─────────────────────────────────────────────┐
│  VISTA (JavaFX FXML + CSS)                  │
│  App.java (ventana unificada, TabPane)      │
│  ├── ParticipanteView.fxml                 │
│  └── EventoView.fxml                       │
└──────────────────┬──────────────────────────┘
                   │ eventos de UI
┌──────────────────▼──────────────────────────┐
│  CONTROLADOR                                 │
│  ParticipanteController / EventoController  │
│  Validaciones, diálogos, refresco de tabla  │
└──────────────────┬──────────────────────────┘
                   │ invoca interfaces DAO
┌──────────────────▼──────────────────────────┐
│  MODELO / DAO (todo el SQL aquí)             │
│  ParticipanteDAO(Impl) / EventoDAO(Impl)     │
│  Entidades: Participante, Evento             │
└──────────────────┬──────────────────────────┘
                   │ JDBC (UNA sola conexión compartida)
┌──────────────────▼──────────────────────────┐
│  MySQL — db_eventos                          │
│  ├── eventos      (Integrante 1)             │
│  └── participantes (Integrante 2)            │
└─────────────────────────────────────────────┘
```

**Decisiones de diseño:**

- **Una sola ventana, dos pestañas**: `App.java` monta un `TabPane` con las pestañas 👥 Participantes y 📅 Eventos — un solo sistema, no dos apps pegadas.
- **Una única conexión JDBC compartida** por ambos módulos (`DBConnection` singleton, `synchronized`), con **auto-reconexión** si el servidor MySQL se reinicia.
- **Carga diferida por pestaña**: cada módulo se carga al abrirse por primera vez; al volver a una pestaña su listado se **recarga desde la BD** automáticamente.
- **Aislamiento de fallos**: si un módulo falla al cargar, se muestra el aviso dentro de su pestaña sin tumbar la otra.
- **Patrón launcher**: `Main` (clase normal) lanza `Application.launch(App.class)` — evita el error *"JavaFX runtime components are missing"* al ejecutar desde IntelliJ/Maven.

---

## 📁 Estructura del proyecto

```
G01_PREHACKATON_T01/
├── db_eventos.sql                     # Script SQL: BD, tablas y datos de ejemplo
├── iniciar_mysql.bat                  # Arranca el MySQL local (usar tras reiniciar la PC)
├── pom.xml                            # Maven: Java 17, JavaFX 17.0.2, MySQL, JasperReports
├── README.md
└── src/main/
    ├── java/com/prehackaton/eventos/
    │   ├── Main.java                  # Punto de entrada ejecutable (lanza la app)
    │   ├── App.java                   # Ventana principal unificada (TabPane, 2 pestañas)
    │   ├── controller/
    │   │   ├── EventoController.java        # Lógica del módulo Eventos
    │   │   └── ParticipanteController.java  # Lógica del módulo Participantes
    │   ├── model/
    │   │   ├── Evento.java / EventoDAO.java / EventoDAOImpl.java
    │   │   └── Participante.java / ParticipanteDAO.java / ParticipanteDAOImpl.java
    │   └── View/                      # Utilidades compartidas (paquete util)
    │       ├── DBConnection.java      # Conexión JDBC única compartida (singleton)
    │       ├── ReportUtil.java        # Reporte Jasper del módulo Eventos
    │       └── ParticipanteReportUtil.java  # Reporte Jasper del módulo Participantes
    └── resources/com/prehackaton/eventos/
        ├── view/
        │   ├── EventoView.fxml        # Vista FXML Eventos
        │   └── ParticipanteView.fxml  # Vista FXML Participantes
        ├── report/
        │   ├── reporte_eventos.jrxml       # Plantilla JasperReports (Eventos)
        │   └── reporte_participantes.jrxml # Plantilla JasperReports (Participantes)
        └── style.css                   # Hoja de estilos unificada (look compartido)
```

---

## ✅ Cumplimiento del enunciado

| Requisito del reto | Estado | Dónde se cumple |
|---|---|---|
| Un solo ejecutable JavaFX por equipo con navegación fluida | ✅ | `App.java` — TabPane con 2 pestañas |
| Persistencia MySQL mediante JDBC | ✅ | `DBConnection` + mysql-connector-j 8.4.0 |
| Arquitectura limpia MVC + DAO (sin SQL en controladores/vistas) | ✅ | Paquetes `controller` / `model` / `view` |
| CRUD individual (4 operaciones) por integrante | ✅ | Insertar, listar/leer, actualizar, eliminar en ambos módulos |
| Reporte JasperReports por integrante, formato tabular | ✅ | 2 plantillas `.jrxml`, visor + exportación PDF |
| UI desktop rica y moderna | ✅ | FXML + `style.css` (cards, tabla estilizada, botones coloreados) |
| Repositorio único en GitHub con trabajo en ramas | ✅ | Este repositorio (integración en rama principal) |
| Datos de ejemplo para pruebas | ✅ | `db_eventos.sql` (6 eventos + 4 participantes) |

---

## 🚀 Requisitos previos

| Software | Versión |
|---|---|
| JDK | 17 o superior (configurar `JAVA_HOME`) |
| Maven | 3.8+ (IntelliJ lo trae integrado) |
| MySQL Server | 8.x corriendo en `localhost:3306` |
| IntelliJ IDEA | Recomendado (Community funciona) |

> Las dependencias (JavaFX 17.0.2, MySQL Connector/J 8.4.0, JasperReports 6.20.0) se descargan solas con Maven — no hay que instalar JARs a mano.

---

## 🗄️ Configuración de la base de datos

### 1. Crear la BD y las tablas

Opción A — **MySQL Workbench** (recomendado):

1. Conectarse a `127.0.0.1`, puerto `3306`, usuario `root` (ver credenciales abajo).
2. `File → Open SQL Script…` → seleccionar **`db_eventos.sql`** de la raíz del proyecto.
3. Ejecutar el script (⚡ lightning).

Opción B — **Línea de comandos**:

```powershell
mysql -u root -p < db_eventos.sql
```

El script crea la base `db_eventos`, las tablas `eventos` y `participantes`, e inserta datos de ejemplo.

### 2. Credenciales usadas por la aplicación

No requieren configuración: están en `DBConnection.java` y corresponden a un MySQL local estándar.

| Parámetro | Valor |
|---|---|
| Host | `localhost` |
| Puerto | `3306` |
| Base de datos | `db_eventos` |
| Usuario | `root` |
| Contraseña | `secret123` |

### 3. Esquema de tablas

```sql
-- Integrante 1
CREATE TABLE eventos (
    id_evento   INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    fecha       DATE NOT NULL,
    lugar       VARCHAR(100),
    modalidad   VARCHAR(50),
    estado      VARCHAR(50)
);

-- Integrante 2
CREATE TABLE participantes (
    id_participante INT AUTO_INCREMENT PRIMARY KEY,
    nombres      VARCHAR(100) NOT NULL,
    apellidos    VARCHAR(100) NOT NULL,
    dni          VARCHAR(8)  NOT NULL UNIQUE,
    correo       VARCHAR(100) NOT NULL,
    telefono     VARCHAR(15),
    institucion  VARCHAR(150) NOT NULL
);
```

### 4. Verificar

```sql
USE db_eventos;
SELECT COUNT(*) FROM eventos;       -- 6
SELECT COUNT(*) FROM participantes; -- 4
```

---

## ▶️ Ejecución

### Opción A — IntelliJ IDEA (recomendada)

1. Abrir el proyecto (`Open` → carpeta raíz, donde está `pom.xml`).
2. Esperar a que IntelliJ termine de importar las dependencias de Maven.
3. Ejecutar **`Main`** (clic derecho → `Run 'Main.main()'` o botón ▶).
4. Se abre la ventana unificada; la primera pestaña es **👥 Participantes**.

### Opción B — Terminal (Maven)

```powershell
mvn clean javafx:run
```

### Opción C — Compilar y ejecutar manualmente

```powershell
mvn clean compile
mvn dependency:build-classpath "-Dmdep.outputFile=cp.txt"
java -cp "target\classes;$(Get-Content cp.txt)" com.prehackaton.eventos.Main
```

> **Windows:** si MySQL no está instalado como servicio, usar el script **`iniciar_mysql.bat`** de la raíz para arrancarlo antes de abrir la app (es necesario tras reiniciar la PC).

---

## ⚙️ Funcionalidades

### 📅 Módulo Eventos (Integrante 1)

- CRUD completo: crear, listar (tabla), actualizar, eliminar con confirmación.
- Validaciones: campos obligatorios, fecha no anterior a hoy, nombre único (control de duplicados), longitudes máximas.
- Búsqueda por nombre.
- Reporte JasperReports tabular con visor y exportación a PDF.

### 👥 Módulo Participantes (Integrante 2)

- CRUD completo: crear, listar (tabla), actualizar, eliminar con confirmación.
- Validaciones: campos obligatorios, DNI de 8 dígitos único, formato de correo, teléfono opcional (7–15 dígitos).
- Búsqueda por nombre, apellido, DNI o institución.
- Reporte JasperReports tabular con visor y exportación a PDF.

### 🪟 Navegación unificada

- Un solo ejecutable (`Main` → `App`): pestañas por módulo, carga diferida, refresco automático del listado al volver a cada pestaña y estilo visual compartido (`style.css`).

---

## 📄 Reportes JasperReports

Cada módulo tiene su plantilla en `src/main/resources/com/prehackaton/eventos/report/`:

| Plantilla | Módulo | Botón |
|---|---|---|
| `reporte_eventos.jrxml` | Eventos | "Generar Reporte" |
| `reporte_participantes.jrxml` | Participantes | "📄 Generar Reporte" |

Los reportes se llenan con los datos de la capa DAO (`JRBeanCollectionDataSource`), se abren en el **JasperViewer** y desde ahí se pueden **exportar/imprimir en PDF**.

---

## 🛠️ Solución de problemas

| Problema | Causa | Solución |
|---|---|---|
| *"Communications link failure / Connection refused: connect"* al abrir un módulo | MySQL no está corriendo | Arrancar MySQL (servicio de Windows o `iniciar_mysql.bat`) y reabrir la app |
| Inserto datos en Workbench y no aparecen en la app | Workbench conectado a **otro** servidor/puerto | Verificar que la conexión de Workbench sea `127.0.0.1`, puerto **`3306`** (no 3307, 3314 ni servidores en la nube) |
| *"JavaFX runtime components are missing"* | Se ejecutó `App` directamente sin módulos JavaFX | Ejecutar **`Main`** (patrón launcher) o `mvn javafx:run` |
| La tabla no refresca tras insertar en la BD | La pestaña ya estaba cargada | Cambiar de pestaña y volver, o usar el botón **"📋 Mostrar todos"** |

---

## 📌 Notas finales

- Este repositorio contiene **solo código fuente**; las dependencias se resuelven con Maven automáticamente.
- **Trabajo futuro**: módulo de **Inscripciones** (relación evento–participante con historial transaccional), extendiendo el patrón DAO ya establecido.
- La aplicación cierra la conexión a MySQL únicamente al finalizar, garantizando que ambos módulos compartan **una sola conexión** durante toda la sesión.
