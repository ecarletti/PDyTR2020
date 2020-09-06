# Practica 1

1) Identifique similitudes y diferencias entre los sockets en C y en Java.

2. Tanto en C como en Java (directorios csock-javasock):

    a) ¿Por qué puede decirse que los ejemplos no son representativos del modelo c/s?

    b) Muestre que no necesariamente siempre se leen/escriben todos los datos involucrados en las comunicaciones con una llamada read/write con sockets. Sugerencia: puede modificar los programas (C o Java o ambos) para que la cantidad de datos que se comunican sea de 103,  104,  105 y  106 bytes  y  contengan  bytes  asignados  directamente  en  el  programa (pueden  no  leer  de  teclado  ni  mostrar  en  pantalla  cada  uno  de  los  datos  del  buffer), explicando  el  resultado  en  cada  caso. Importante: notar  el  uso de  “attempts”  en “...attempts to read up to count bytes from file descriptor fd...” así como el valor de retorno de la funciónread (del man read).

    c) Agregue a la modificaciónanterior una verificaciónde llegada correcta de los datos que se envían(cantidad y contenido del buffer), de forma tal que se asegure que todos los datos enviados llegan    correctamente, independientemente de la cantidad  de datos involucrados.
    
    d) Grafique el promedio y la desviaciónestándarde los tiempos de comunicaciones de cada comunicación.    Explique    el    experimento    realizado    para    calcular    el    tiempo    de comunicaciones.

3. ¿Por qué en  C  se  puede  usar  la  misma  variable  tanto  para  leer  de  teclado  como  para enviar por un socket? ¿Estoseríarelevante para las aplicaciones c/s?

4. ¿Podría implementar  un  servidor  de  archivos  remotos  utilizando  sockets?  Describa brevemente la interfaz y los detalles que considere másimportantes del diseño. No es necesario implementar.

5. Defina quées  un  servidor  con  estado  (stateful  server)  y quées  un  servidor  sin  estado (stateless server).