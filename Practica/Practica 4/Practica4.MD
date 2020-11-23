# Practica 4

1. Programar un agente para que periódicamente recorra una secuencia de computadoras y reporte al lugar de origen:

- a) El tiempo total del recorrido para recolectar la información.
- b) La carga de procesamiento de cada una de ellas.
- c) La cantidad de memoria total disponible.
- d) Los nombres de las computadoras.

Comente  la relación entre  este  posible  estado  del  sistema  distribuido  y el  estado  que  se obtendría implementando el algoritmo de instantánea.

2. Programe un agente para que calcule la suma de todos los números almacenados en un archivo de una computadora que se le pasa como parámetro. Comente cómo se haría lo mismo con una aplicación cliente/servidor. Comente que pasaría si hubiera otros sitios con archivos que deben ser procesados de manera similar.

3. Defina  e  implemente  con  agentes  un  sistema  de  archivos  distribuido  similar  al  de  las prácticas anteriores

- a. Debería tener como mínimo la misma funcionalidad, es decir las operaciones (definiciones copiadas aquí de la práctica anterior):

  leer: dado un nombre de archivo, una posición y una cantidad de bytes a leer, retorna 1) la  cantidad  de  bytes  del  archivo  pedida  a  partir  de  la posición dada  o  en  caso  de  haber menos bytes, se retornan los bytes que haya y 2) la cantidad de bytes que efectivamente se retornan leídos.

  Escribir:  dado  un  nombre  de  archivo,  una  cantidad  de  bytes  determinada,  y  un  buffer  a partir del cual están los datos, se escriben los datos en el archivo dado. Si el archivo existe, los datos se agregan al final, si el archivo no existe, se crea y se le escriben los datos. En todos los casos se retorna la cantidad de bytes escritos.

  

- b. Implemente  un  agente  que  copie  un  archivo  de  otro  sitio  del  sistema  distribuido  en  el sistema  de  archivos  local  y  genere  una  copia  del  mismo  archivo  en  el  sitio  donde  esta ́ originalmente.  Compare  esta solución con  la  de  los  sistemas  cliente/servidor  de  las prácticas anteriores.