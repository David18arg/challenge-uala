### Mobile Challenge - Ualá

# 🌍 Desafío de Filtrado de Ciudades

Este proyecto aborda el desafío de filtrar y mostrar eficientemente una lista extensa (~200.000 ciudades) en Android, priorizando rendimiento, arquitectura limpia y experiencia de usuario fluida.

##  Enfoque para Resolver el Problema de Búsqueda


## 🚀 1. Descarga y Amortización Inicial

Durante el arranque de la aplicación, se busca amortizar el tiempo de espera por medio de una experiencia visual atractiva y una estrategia eficiente de carga de datos.

### 🎬 Splash animado con Lottie
- Se muestra una animación en pantalla en el **hilo principal** (`Main thread`) utilizando Lottie, lo que mantiene la UI activa y atractiva mientras se inicia la app.
- Esta animación no bloquea el flujo principal de interacción con la app y se sincroniza con el progreso de carga en segundo plano.

### 📥 Descarga y Persistencia de Datos
- En **un hilo secundario** (`IO dispatcher`), se realiza la descarga inicial de la lista de ciudades usando Retrofit.
- Los datos se procesan y se insertan en la base local Room utilizando el patrón DAO, sin necesidad de cargar todo en memoria.
- Esta operación se desacopla de la UI mediante corrutinas (`viewModelScope.launch(Dispatchers.IO)`), garantizando una experiencia fluida sin bloqueos.

### 💾 Ventajas Clave
- Se evita el uso intensivo de memoria al persistir directamente los datos.
- Se mejora la experiencia de usuario inicial evitando pantallas en blanco o bloqueos durante la carga.
- La transición entre el splash y la pantalla principal se coordina automáticamente tras completar el proceso en segundo plano.


## 2. ⚡️ Carga Reactiva y Paginación Progresiva

La aplicación implementa una arquitectura centrada en la reactividad y eficiencia, combinando `Flow`, `PagingSource` y `LazyColumn` para garantizar una experiencia fluida y escalable:

### 🔄 Room + Flow
Los datos de ciudades se almacenan localmente en Room y se exponen como flujos (`Flow`) que permiten:
- Actualización automática de la UI ante cambios en favoritos o nuevos datos.
- Filtrado dinámico por prefijo en tiempo real desde el buscador.

### 📚 PagingSource
Se utiliza una fuente de paginación personalizada para interactuar con la base Room. El comportamiento es el siguiente:
- Carga bloques de 10 elementos (`pageSize = 10`).
- Implementa pre-fetching para evitar saltos de carga cuando el usuario se acerca al final de la lista visible.
- Soporta búsqueda por prefijo y filtro de favoritos.
- Optimizado para minimizar delays entre Room, el repositorio y la capa UI.

### 🧱 Jetpack Compose + LazyColumn
La visualización se hace con `LazyColumn`, en conjunto con `collectAsLazyPagingItems()` para integrar la paginación con Compose:
- Renderizado eficiente de solo los ítems visibles.
- Actualización automática mientras el usuario escribe o modifica filtros.
- Soporte total para navegación, selección de favoritos y transiciones fluidas.

🔍 **Ventaja clave**: Este enfoque reactivo evita cálculos innecesarios, mejora el rendimiento en dispositivos limitados y brinda una UX moderna, con tiempos de respuesta mínimos al buscar o cambiar de vista.



## 3. 🎨 Componentización UI en Jetpack Compose

Se adoptó un enfoque de diseño modular para desacoplar la lógica visual en distintas pantallas y facilitar la escalabilidad del código Compose:

### 🧩 Composición por View
Cada screen (`CityListScreen`, `CityMapScreen`, `CityDetailScreen`) define su propio conjunto de composables, organizados por responsabilidad:
- Las vistas principales se encapsulan en componentes `Stateful`, encargados de manejar eventos, estados, y comunicación con el ViewModel.
- Dentro de estas, se reutilizan componentes `Stateless` para la representación visual (ej. ítems de ciudad, íconos, botones).

### ♻️ Reutilización de Composables
- Se creó una carpeta `common` dentro de la capa `presentation` que agrupa todos los composables reutilizables entre pantallas: `Loading`, `Message`, `Theme`, `Splash`, entre otros.
- Cada pantalla importa solo los composables que necesita, evitando duplicaciones y promoviendo claridad en el flujo de UI.

### 🔀 Stateless vs Stateful
- Los composables **stateless** reciben todo por parámetros y se centran únicamente en la visualización, lo que permite su prueba y reutilización sin dependencias.
- Los composables **stateful** coordinan estados locales, callbacks y navegación. Solo estos interactúan directamente con `StateFlow` o `ViewModel`.

> 🛠️ Esta organización mejora la mantenibilidad del proyecto y facilita la incorporación de nuevas pantallas o componentes, siguiendo el principio de separación de responsabilidades.



## 🧪 4. Estrategia de Testing

El proyecto incorpora una estrategia de pruebas enfocada en asegurar la estabilidad de los flujos principales y la experiencia del usuario mediante tests unitarios y de UI con Jetpack Compose.

### ✅ Tests Unitarios

Se implementaron pruebas directas sobre los flujos que concentran lógica crítica:

- 🔁 **Repositorios**: Se testean casos de uso como filtrado por prefijo, recuperación de favoritos, y paginación progresiva utilizando mocks y simulaciones de Room y API.
- 📡 **ViewModels**: Validación de estados emitidos (`StateFlow`), manejo de búsquedas, navegación y persistencia. Se simulan interacciones con dependencias inyectadas.
- 🎨 **Composables**: Pruebas en componentes `Stateless` reutilizables con entradas controladas para verificar layout, estilo y eventos (`onClick`, `onToggle`, etc.).

### ✅ Tests de UI con Compose Test

Se implementaron pruebas de interfaz en diferentes escenarios de uso:

- 🔍 Búsqueda reactiva desde el `SearchBar`, actualización en tiempo real en la lista.
- ⭐ Marcado y desmarcado de ciudades favoritas y persistencia visual.
- 🧪 Se usan reglas como `composeTestRule` y `onNodeWithText()` para validar interacciones, estados visibles y lógica de recomposición.

> 🧠 Se prioriza la cobertura en funciones donde se concentran transformaciones de datos, lógica condicional, y composición visual relevante.


