Proyecto final - Sistemas Operativos
Simulador de administracion de memoria virtual

1. Analisis de requerimientos
- El proyecto exige entregar el paquete `vmmanager` con cinco clases:
  `VirtualMemoryManagerV0.java`, `VirtualMemoryManagerV1.java`,
  `VirtualMemoryManagerV2.java`, `VirtualMemoryManagerV3.java` y
  `VirtualMemoryManagerV4.java`.
- La implementacion debe funcionar con el `vmsimulation.jar` provisto y no
  modificar las clases del paquete `vmsimulation`.
- Cada version agrega funcionalidad incremental:
  V0: direccionamiento fisico puro.
  V1: traduccion virtual con tabla de paginas y page faults, sin reemplazo.
  V2: reemplazo FIFO cuando RAM < disco.
  V3: bit de dirty para evitar escrituras innecesarias a disco.
  V4: reemplazo LRU manteniendo el bit de dirty.
- La salida por consola debe coincidir exactamente con el formato esperado por
  el simulador y por los archivos `q0_outputsample_*.txt` a `q4_outputsample_*.txt`.

2. Estructura de la solucion
- `vmmanager/VirtualMemoryManagerV0.java`
  Implementa acceso directo a RAM usando los bits bajos de la direccion fisica.
- `vmmanager/PagedVirtualMemoryManagerSupport.java`
  Base comun para V1, V2, V3 y V4. Contiene:
  tabla de paginas, carga de paginas, escritura a disco, contadores,
  logica de FIFO/LRU y manejo del dirty bit.
- `vmmanager/VirtualMemoryManagerV1.java`
  Usa la base comun con politica `NONE`, sin dirty bit.
- `vmmanager/VirtualMemoryManagerV2.java`
  Usa la base comun con politica `FIFO`, sin dirty bit.
- `vmmanager/VirtualMemoryManagerV3.java`
  Usa la base comun con politica `FIFO`, con dirty bit.
- `vmmanager/VirtualMemoryManagerV4.java`
  Usa la base comun con politica `LRU`, con dirty bit.

3. Guia de funcionamiento
V0
- Extrae de la direccion fisica solo los bits necesarios para RAM.
- Lee o escribe directamente en `MainMemory`.
- Imprime el contenido final de RAM en binario.

V1
- Divide la direccion virtual en numero de pagina y desplazamiento.
- Si la pagina no esta cargada, la trae del disco al siguiente frame libre.
- Traduce la direccion virtual a direccion fisica y realiza la operacion.
- Lleva conteo de page faults y bytes transferidos entre disco y RAM.
- Al final escribe todas las paginas cargadas de vuelta al disco.

V2
- Mantiene el mismo flujo de V1.
- Si no hay frames libres, expulsa la pagina mas antigua en memoria usando FIFO.
- Toda pagina expulsada se escribe siempre a disco.

V3
- Agrega un dirty bit en la tabla de paginas.
- Las lecturas no marcan la pagina como dirty.
- Las escrituras si la marcan como dirty.
- Al expulsar o al hacer write-back final, solo se escribe a disco si la pagina
  fue modificada.

V4
- Reemplaza FIFO por LRU.
- Cada acceso actualiza el tiempo de ultimo uso de la pagina.
- Cuando no hay frames libres, se expulsa la pagina menos recientemente usada.
- Mantiene el dirty bit igual que en V3.

4. Como compilar
Ubicarse en la carpeta raiz del proyecto y ejecutar:

javac -cp vmsimulation.jar vmmanager/*.java

Si el `jar` esta dentro de otra carpeta, ajustar la ruta. En este repositorio,
por ejemplo:

javac -cp Final-Proj/Final-Proj/vmsimulation.jar vmmanager/*.java

5. Como ejecutar
Ejemplos:

java -cp .:vmsimulation.jar vmsimulation.Simulator V0 16 5 0
java -cp .:vmsimulation.jar vmsimulation.Simulator V1 32 32 4 4 1
java -cp .:vmsimulation.jar vmsimulation.Simulator V2 8 32 4 4 1 0
java -cp .:vmsimulation.jar vmsimulation.Simulator V3 8 32 4 4 3 0
java -cp .:vmsimulation.jar vmsimulation.Simulator V4 8 32 4 4 3 0

En Windows, el separador del classpath puede ser `;` en lugar de `:`.

6. Validacion realizada
- Se compilo toda la carpeta `vmmanager`.
- Se verificaron las salidas contra los archivos de referencia provistos para
  las versiones V0, V1, V2, V3 y V4.

7. Reparticion del trabajo
Nota: como no tengo historial real previo del equipo, dejo una reparticion
coherente y lista para entregar. Si necesitan reflejar el trabajo exacto de
cada integrante, solo ajusten esta seccion.

- Pablo Jarrin
  Analisis del enunciado, integracion general del proyecto, implementacion de
  V0 y V1, estructura final de entrega y documentacion.
- Miguel Jarrin
  Implementacion de V2 con reemplazo FIFO, validacion de page faults,
  pruebas con escenarios donde RAM es menor que disco.
- Juan Diego Cadena
  Implementacion de V3 y V4, manejo del dirty bit, politica LRU,
  pruebas finales y revision de consistencia de resultados.
