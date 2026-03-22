# 📱 Práctica Android: Menú de Ejercicios

Es una aplicación de un menu, el cual contiene 3 bótones uno por cada ejercicio.

---

## 🏠 1. Menú Principal (`MainActivity`)

Es la puerta de entrada de la aplicación.
*   **Diseño (XML):** Utiliza un `LinearLayout` vertical con gravedad centrada para mostrar una lista ordenada de botones.
*   **Lógica:** Cada botón tiene un `OnClickListener` que lanza un `Intent` explícito para navegar a la actividad correspondiente (`Ejercicio1Activity`, etc.).
*   **Tema:** Se ha configurado el `AndroidManifest.xml` y `themes.xml` para mostrar una barra de acción personalizada (o clásica `AppCompat`) con el título "Tema1".

---

## 💱 2. Ejercicio 1: Conversor de Moneda

Una herramienta para convertir entre Euros y Dólares en tiempo real con opciones de personalización.

### Tecnologías Clave:
*   **ViewBinding:** Para acceder a los elementos de la vista sin `findViewById`.
*   **SharedPreferences:** Para guardar la configuración del usuario (color de fondo y ratio de conversión) de forma permanente.
*   **TextWatcher:** Para detectar cambios en el cuadro de texto y calcular automáticamente sin pulsar botones.

### Funcionalidades:
1.  **Conversión Automática:** Al escribir una cantidad o cambiar el `RadioButton` (Euro/Dólar), el resultado se recalcula al instante.
2.  **Manejo de Errores:** Evita cierres inesperados si el campo está vacío usando `toDoubleOrNull()`.
3.  **Menú de Opciones (Toolbar):**
    *   **Color de fondo:** Permite cambiar el color de la pantalla y lo recuerda la próxima vez que abras la app.
    *   **Cambiar Ratio:** Un diálogo permite ajustar el valor del cambio (ej: 1.10) y lo guarda en preferencias.
    *   **Acerca de:** Muestra un Toast con información del creador.

---

## ⏰ 3. Ejercicio 2: Alarmas Consecutivas

Un sistema de temporizadores encadenados que lee una configuración desde un fichero de texto.

### Tecnologías Clave:
*   **Manejo de Ficheros (I/O):** Lectura y escritura de archivos `.txt` en la memoria interna/externa de la app.
*   **CountDownTimer:** Para la cuenta regresiva visual.
*   **Recursividad:** Una función que se llama a sí misma al terminar una alarma para iniciar la siguiente.
*   **Notificaciones y Multimedia:** Uso de `NotificationCompat` y `MediaPlayer`.

### Funcionalidades:
1.  **Editor de Fichero:** Un `EditText` permite escribir las alarmas en formato CSV: `Minutos, Mensaje, Sonido`.
2.  **Cola de Alarmas:** Al pulsar "Guardar y Arrancar", el texto se procesa y se guarda en una cola (`Queue/LinkedList`).
3.  **Ejecución Secuencial:**
    *   El temporizador cuenta hacia atrás.
    *   Al llegar a 0, suena un aviso y sale una notificación.
    *   Automáticamente inicia la siguiente alarma de la lista hasta terminar todas.
4.  **Persistencia:** El nombre del fichero y la preferencia de sonido (ON/OFF) se guardan.

---

## 🛠️ 4. Ejercicio 3: Base de Datos de Herramientas IA

Un gestor CRUD (Crear, Leer, Actualizar, Borrar) completo utilizando una base de datos local.

### Tecnologías Clave:
*   **SQLite (SQLiteOpenHelper):** Base de datos relacional nativa de Android.
*   **RecyclerView + Adapter:** Para mostrar la lista de elementos de forma eficiente.
*   **CardView:** Para el diseño de cada elemento (tarjeta).
*   **ItemTouchHelper:** Para detectar gestos de deslizamiento (Swipe).

### Estructura de Datos (Tabla `herramientas`):
*   `id`: Identificador único (Auto-incremental).
*   `nombre`, `descripcion`, `url`, `categoria`: Textos.
*   `imagen`: Entero (`Int`) que referencia un recurso drawable (icono).

### Funcionalidades:
1.  **Listado:** Muestra todas las herramientas almacenadas con su icono, nombre y descripción.
2.  **Añadir (FloatingActionButton):** Abre un diálogo para introducir datos y seleccionar un icono (usando `RadioGroup`).
3.  **Navegar (Click Corto):** Al pulsar una tarjeta, abre el navegador web con la URL guardada.
4.  **Editar (Click Largo):** Al mantener pulsado, abre el diálogo con los datos precargados para modificarlos.
5.  **Borrar (Swipe):** Al deslizar una tarjeta a la derecha, se elimina de la base de datos y de la lista con una animación.

---

## ⚙️ Configuración Técnica (Notas para el desarrollador)

Para que este proyecto funcione correctamente, se han realizado los siguientes ajustes:

1.  **Permisos (`AndroidManifest.xml`):**
    *   `POST_NOTIFICATIONS`: Para las alertas del Ejercicio 2 (Android 13+).
    *   `VIBRATE`: Para feedback háptico.
2.  **Gradle (`build.gradle`):**
    *   Se ha activado `viewBinding = true` para facilitar la vinculación de vistas.
3.  **Gestión de Versiones BD:**
    *   Se implementó `onUpgrade` en la clase `AdminSQL` para gestionar cambios en la estructura de la tabla (como la adición de la columna imagen).
