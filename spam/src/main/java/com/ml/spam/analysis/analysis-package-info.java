
/**
 * Este package proporciona una interfaz gráfica JavaFX para explorar y analizar datasets CSV,
 * ofreciendo funcionalidades inspiradas en la biblioteca pandas de Python.
 *
 * <p>Incluye las siguientes herramientas:
 * <ul>
 *   <li>Carga de archivos CSV mediante selector de archivos</li>
 *   <li>Visualización del resumen general con gráfico de valores faltantes</li>
 *   <li>Visualización de histogramas y boxplots para columnas numéricas</li>
 *   <li>Cálculo manual de la correlación de Pearson entre columnas</li>
 *   <li>Visualización del header: columnas en forma vertical y horizontal</li>
 *   <li>Lectura de las primeras filas del dataset (head)</li>
 *   <li>Estimación de tipos de datos (dtypes)</li>
 *   <li>Resumen estadístico (min, max, media, desviación estándar)</li>
 *   <li>Valores únicos y conteo por categoría en columnas seleccionadas</li>
 * </ul>
 *
 * <p>Clases principales:
 * <ul>
 *   <li>{@link DatasetInspector}: Contiene los métodos estáticos para analizar datasets: valores faltantes,
 *       histogramas, boxplots y correlación. Actúa como backend lógico.</li>
 *
 *   <li>{@link DatasetInspectorUI}: Clase JavaFX que implementa la interfaz gráfica con botones y ventanas para
 *       acceder a las funcionalidades del inspector. Permite seleccionar archivos y visualizar resultados.</li>
 *
 *   <li>{@link ChartLauncher}: Clase de utilidad para lanzar ventanas gráficas de JavaFX en nuevos hilos. Se encarga
 *       de la separación entre lógica y visualización.</li>
 *
 *   <li>{@link UnifiedChartApp}: Aplicación JavaFX que renderiza histogramas o boxplots, dependiendo de los datos
 *       y argumentos recibidos. Es invocada por ChartLauncher.</li>
 *
 *   <li>{@link DatasetInspectorMain}: Clase de arranque para probar la lógica del inspector desde consola. Lanza
 *       operaciones predefinidas sin interfaz gráfica.</li>
 * </ul>
 *
 * <p>La interfaz principal se lanza desde {@link DatasetInspectorUI}.
 */
package com.ml.spam.analysis;
