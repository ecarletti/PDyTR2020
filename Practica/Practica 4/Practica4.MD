# Practica 4

  

1. Programar un agente para que periódicamente recorra una secuencia de computadoras y reporte al lugar de origen:

- a) El tiempo total del recorrido para recolectar la información.

- b) La carga de procesamiento de cada una de ellas.

- c) La cantidad de memoria total disponible.

- d) Los nombres de las computadoras.

Comente la relación entre este posible estado del sistema distribuido y el estado que se obtendría implementando el algoritmo de instantánea.

Se creó un agente (**ReportAgente**) el cual crea 10 contenedores, y mediante un array (*ArrayList<String>*) se van agregando las ID’s de cada uno, con el fin de recordarlos y añadiendo al final al contenedor inicial.

Luego se itera sobre la lista, juntando la información del contenedor en el que se encuentra el agente. Para almacenar la información, se crea una clase anidada llamada ContainerInfo, la cual setea los valores de los datos conseguidos, y se almacena en un ArrayList.

Por último, tenemos acceso al container origen, con un arreglo de instancias e imprimimos una lista de ContainerInfo.

Ejecución:

- **make** (el cual hace el build de las clases con javac)

- **make start** (ejecuta el jade.Boot -gui)

- **run-report** (*el cual ejecuta el ReportAgent de la siguiente manera:
`cd Report && java -cp ../lib/jade.jar:classes jade.Boot -gui -container -host localhost -agents mol:ReportAgent`)*

Algoritmo de instantánea:

*Se utiliza un algoritmo de instantánea para crear una instantánea coherente del estado global de un sistema distribuido. Debido a la falta de memoria compartida.*

Varias computadoras trabajan juntas en un sistema distribuido. Cada uno de ellos representa una computadora con una determinada cantidad de memoria total, información, etc. Los participantes pueden modificar su cantidad de memoria disponible.

Al calcular la cantidad de memoria total disponible. Solo solicitar la memoria de cada participante puede llevar a un resultado incorrecto, si uno de ellos acaba de cambiar su espacio de memoria disponible.


Otro ejemplo:

Cada computadora representa una cuenta bancaria con una determinada cantidad de dinero. Los participantes pueden transferir dinero entre sus cuentas intercambiando mensajes.
Suponga que se calculará el saldo global. Solo solicitar el saldo de cada participante puede llevar a un resultado incorrecto, si uno de ellos acaba de enviar un mensaje de transferencia a otro (y por lo tanto ya ha disminuido su propio saldo), que aún no lo recibió.

  **Un algoritmo de instantáneas evita tales inconsistencias.**
  
  
1. Programe un agente para que calcule la suma de todos los números almacenados en un archivo de una computadora que se le pasa como parámetro. Comente cómo se haría lo mismo con una aplicación cliente/servidor. Comente que pasaría si hubiera otros sitios con archivos que deben ser procesados de manera similar.

  

Se creó el agente (**SumaAgent**) que recibe el nombre de una computadora y migra al agente.

Una vez migrado a la computadora destino, se realiza la lectura del archivo, junto con la suma. Al finalizar se retorna la suma al container origen, y se imprime el resultado.

La lectura del archivo es *numeros*, el cual se encuentra dentro de la carpeta Suma.

Ejecución

 - **make**
   
  - **make start**
   
   - **run-suma** (`cd Suma && java -cp ../lib/jade.jar:classes jade.Boot -gui -container -host localhost -agents mol:SumaAgent`)

  

Para simular el modelo cliente/servidor, se podría implementar que el servidor **debe poder acceder** al filesystem del cliente. Y de ingresar, puede copiar y trabajar localmente el archivo, para retornar un valor final.
En cambio de **no poder acceder** al filesystem, se puede crear un servicio que acepte un archivo y retorna su suma.

De todas formas, la interacción puede llegar a ser un poco menos directa, aunque más simple.

3. Defina e implemente con agentes un sistema de archivos distribuido similar al de las prácticas anteriores

- a. Debería tener como mínimo la misma funcionalidad, es decir las operaciones (definiciones copiadas aquí de la práctica anterior):

leer: dado un nombre de archivo, una posición y una cantidad de bytes a leer, retorna 1) la cantidad de bytes del archivo pedida a partir de la posición dada o en caso de haber menos bytes, se retornan los bytes que haya y 2) la cantidad de bytes que efectivamente se retornan leídos.

  
Escribir: dado un nombre de archivo, una cantidad de bytes determinada, y un buffer a partir del cual están los datos, se escriben los datos en el archivo dado. Si el archivo existe, los datos se agregan al final, si el archivo no existe, se crea y se le escriben los datos. En todos los casos se retorna la cantidad de bytes escritos.

  

- b. Implemente un agente que copie un archivo de otro sitio del sistema distribuido en el sistema de archivos local y genere una copia del mismo archivo en el sitio donde esta ́ originalmente. Compare esta solución con la de los sistemas cliente/servidor de las prácticas anteriores.

Se programó el FTPAgent, que puede recibir argumentos (write o read):

- Read

	Recibe 2 argumentos, el nombre del nuevo archivo y el archivo remoto

	Una vez parseados los argumentos del lado cliente, se realiza la migración al servidor. Al terminar la migración se leen los primeros 1024, y el tamaño del mismo y se almacenan en variables de instancia.

	En el cliente, se realiza la copia de los 1024 bytes, y se consulta si el tamaño del archivo es menor o igual al tamaño copiado siendo en la primera vuelta igual a 2014. De no ser así, se suman a una variable de bytes, una cantidad de información copiada y se realiza la migración al servidor.

	En el servidor se copian los siguientes 1024 bytes a partir de los bytes copiados, repitiendo esta acción hasta que se finaliza la escritura del archivo.

- Write

	Trabaja de igual manera del read, pero inversamente, siendo el cliente del item anterior, el servidor y el servidor el cliente.
  Los archivos mediante el write, son almacenados en una carpeta *store* simulando el file system del servidor.


Ejecución

- **make**

- **make start**

y en otra consola para ejecutar el cliente
  
**Upload file**

- ./FTP/run write -o file_name_in_store -i file_path_to_upload

ejemplo: se quiere escribir el archivo prueba en el store ./FTP/run write -o prueba -i $HOME/java.pdf

**Download file**

- ./FTP/run read -i file_name_in_store -o file_path_to_download

ejemplo: se quiere leer el archivo prueba en el store ./FTP/run read -i prueba -o $HOME/file
