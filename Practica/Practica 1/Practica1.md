# Practica 1 

---

>**1.** Identifique similitudes y diferencias entre los sockets en C y en Java.

* **Creación y utilización:**
  - En C se utiliza la librería estándar **sys/socket.h** (tiene funciones y manejos primitivos).
  - En Java se utiliza **java.net** para la creación de los sockets y otros módulos provenientes de la librería **java.io** para la comunicación de los mismos a modo de *read & write*, le da un uso a los sockets en un paradigma de alto nivel.
  >
* **Implemetacion:**
    - En enfoque a Java, cuenta con una clase **java.net.Socket** ,la cual permite una comunicación bidireccional entre dos procesos en una red(la cual son definidos por el programa). También cuenta con la clase **java.net.ServerSocket** para la implementación de sockets que estén *listen* y aceptando conexiones de procesos clientes.
    >
    - En cambio C, no existe una distinción marcada entre el Cliente/Servidor. Si no que el modelo puede ser implementado pero la librería que se utiliza no cuenta con funciones directamente relacionadas con esta tarea. La librería **sys/socket.h** contiene la función socket, que se carga de crear un socket.  En C, son representados por file descriptors, de los cuales se conoce un numero único (de identificación), los cuales se utilizan de la misma manera que archivos en un file system (*leyendolos y escribiendolos*). Ademas se utilizan operaciones tales como: *bind*, *listen* y *accept* sobre el socket servidor para darle una dirección, permitirle escuchar conexiones o aceptarlas respectivamente.
  >
* **Similitudes:**
  - Tanto Java como C tienen el mismo fin, poseen características y funciones suficientes para cumplir el modelo de Cliente / Servidor, poder lograr una comunicación a través de los sockets, pueden intercambiar cualquier flujo de datos ya seas procesos en la misma PC o distinta(se permite elegir el protocolo (TCP/UDP). 
  La lectura funciona de la misma manera. Apenas llega algún dato al receptor, se libera (el buffer/stream) para continuar con la ejecución.


>**2.** Tanto en C como en Java (directorios csock-javasock):

>- **a.-** ¿Por qué puede decirse que los ejemplos no son representativos del modelo c/s?. 

  - Las conexión no implica específicamente una petición, o una respuesta a una petición, **solo es comunicación**. En este caso, el servidor cierra la conexión al primer request y envió de respuesta. No se respetan los pasos a cumplir de una conexión: inicialización ,envió/recepción de peticiones finalización.
  Solo se realiza la transferencias de datos sin mensajes de envió/cierre de conexión. 
  
>- **b.-** Muestre que no necesariamente siempre se leen/escriben todos los datos involucrados en las comunicaciones con una llamada read/write con sockets.
*Sugerencia: puede modificar los programas (C o Java o ambos) para que la cantidad de datos que se comunican sea de 10<sup>3</sup>,  10<sup>4</sup>,  10<sup>5</sup> y  10<sup>6</sup> bytes  y  contengan  bytes  asignados  directamente  en  el  programa (pueden  no  leer  de  teclado  ni  mostrar  en  pantalla  cada  uno  de  los  datos  del  buffer), explicando  el  resultado  en  cada  caso. Importante: notar  el  uso de  “attempts”  en “...attempts to read up to count bytes from file descriptor fd...” así como el valor de retorno de la función read (del man read).*

  - Modificamos el código del cliente y servidor (del lenguaje C), en donde el buffer puede ser definido en tiempo de compilación (con las directivas #ifdef, #else y #endif). 
   `Se encuentran en csocks los archivos client_2b.c y server_2b.c`
  - El tamaño del buffer tanto del cliente como del servidor se fueron ejecutando con los diferentes valores mencionados en el punto, para ver hasta que cantidad de datos se pueden leer.
  El servidor pudo leer correctamente todos los caracteres enviados para un *BUFFER_SIZE* menor o igual a 10<sup>4</sup>.
  **Cuando el tamaño es mayor solo se pueden leer ≈ 65482** *como podemos observar en la siguiente imagen.*
    + ![Máximo recibido buffer](images/2b.png).


>- **c.-** Agregue a la modificación anterior una verificación de llegada correcta de los datos que se envían(cantidad y contenido del buffer), de forma tal que se asegure que todos los datos enviados llegan    correctamente, independientemente de la cantidad  de datos involucrados.
  
>- **d.-** Grafique el promedio y la desviación estándar de los tiempos de comunicaciones de cada comunicación.    Explique    el    experimento    realizado    para    calcular    el    tiempo    de comunicaciones.

>**3.** ¿Por qué en  C  se  puede  usar  la  misma  variable  tanto  para  leer  de  teclado  como  para enviar por un socket? ¿Esto sería relevante para las aplicaciones c/s?

>**4.** ¿Podría implementar  un  servidor  de  archivos  remotos  utilizando  sockets?  Describa brevemente la interfaz y los detalles que considere más importantes del diseño. No es necesario implementar.

>**5.** Defina qué es  un  servidor  con  estado  (stateful  server)  y qué es  un  servidor  sin  estado (stateless server).
