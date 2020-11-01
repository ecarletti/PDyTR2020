##Práctica 3

1) Utilizando como base el programa ejemplo​1​ de gRPC:
   a)  Mostrar experimentos donde se produzcan errores de conectividad del lado del cliente y del lado del servidor. Si es necesario realice cambios mínimos para,por ejemplo,incluir sleep()o exit(),de forma tal que no se reciban comunicaciones o no haya receptor para las comunicaciones.

2) Describir y analizar los tipos de API que tiene gRPC. Desarolle una conclusion acerca de cual es la mejor opcion para los siguientes escenarios:
   - Un sistema de pub/sub
   - Un sistema de archivos FTP
   - Un sistema de chat
  
3) Analizar la transparencia de gRPC en cuanto al manejo de parametros de los procedimientos remotos. Considerar lo que sucede en el caso de los valores de retorno. Puede aprovechar el ejemplo provistos.

4) Con la finalidad de contar con una version muy restringida de un sistema de archivos remotos, en el cual se puede llevar a cabo las operaciones enunciadas informalmente como:
   - Leer
   - Escribir

    a.- Defina e implemente con gRPC un servidor. Documente todas las decisiones tomadas.
    b.- Investigue si es posible que varias invocaciones remotas esten ejecutandose concurrentemente y si esto es apropiado o no para el servidor de archivos del ejercicio anterior. En caso de que no se apropiado, analice si es posible proveer una solucion (enunciar/describir una solucion, no es necesario implementarla).
    **Nota:** diseñe un experimento con el que se pueda demostrar fehacientemente que dos o mas invocaciones remotas se ejecutan concurrentemente o no.

5) Timeouts en gRPC:
   a.- Desarrollar un experimento que muestre el timeout definido para las llamadas gRPC y el promedio de tiempo de una llamada gRPC.
   b.- Reducir el timeout de las llamadas gRPC a un 10% menos del promedio encontrado anteriormente. Mostrar y explicar el resultado para 10 llamadas.
   c.- Desarrollar un cliente/servidor gRPC de forma tal que siempre se supere el tiempo de timeout. Una forma sencilla puede utilizar el tiempo de timeout como parametro del procedimiento remoto donde se lo utiliza del lado del servidor en una llamda a sleep(), por ejemplo.
