# Entrega practica 3 (gRPC)

## [Entrega](Practica3.pdf)

### Comandos utiles

- Consola docker: 
```sh
  docker exec -it --user root pdytr bash
```

- Directorio del proyecto: 
```sh
  cd /pdytr/Practica/Practica\ 3/ej4a/grpc-hello-server/
```

- Compilar: 
```sh
mvn compile
```

- Server: 
```sh
  mvn exec:java -Dexec.mainClass="pdytr.grpc.UploadFileServer"
```

- Cliente para escritura: 
```sh
  mvn exec:java -Dexec.mainClass="pdytr.grpc.UploadFileClient" \
    -Dexec.args="write --src $HOME/path --dest filenameInServer"
```

- Cliente para lectura: 
```sh
  mvn exec:java -Dexec.mainClass="pdytr.grpc.UploadFileClient" \
    -Dexec.args="read --src fileNameInServer --dest $HOME/path"
```
