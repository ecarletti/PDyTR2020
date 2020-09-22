/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <getopt.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

// Hash Functions http://www.cse.yorku.ca/~oz/hash.html
unsigned long djb2(unsigned char *str);

/*
 * En tiempo de compilación se puede definir esta macro con un valor numérico >= 0
 *
 */
#ifndef BUFFER_SIZE
#define BUFFER_SIZE 1000
#endif


/*
 * Error
 * -- Muestra un mensaje de error y finaliza la ejecución del programa.
 *
 * @param char * msg, Mensaje a mostrar
 *
 * @return void
 *
 */
void
error(char *msg)
{
        perror(msg);
        exit(0);
}


int
main(int argc, char *argv[])
{
        static int verbose_flag;

        int sockfd, newsockfd, portno, clilen;
        char buffer[BUFFER_SIZE];
        struct sockaddr_in serv_addr, cli_addr;
        int n;

        if (argc < 2)
        {
                fprintf(stderr, "ERROR, no port provided\n");
                exit(1);
        }

        /* CREA EL FILE DESCRIPTOR DEL SOCKET PARA LA CONEXION */
        sockfd = socket(AF_INET, SOCK_STREAM, 0);
        /* AF_INET - FAMILIA DEL PROTOCOLO - IPV4 PROTOCOLS INTERNET */
        /* SOCK_STREAM - TIPO DE SOCKET */

        if (sockfd < 0)
                error("ERROR opening socket");

        bzero((char *) &serv_addr, sizeof(serv_addr));
        /* ASIGNA EL PUERTO PASADO POR ARGUMENTO */
        /* ASIGNA LA IP EN DONDE ESCUCHA (SU PROPIA IP) */
        portno = atoi(argv[1]);
        serv_addr.sin_family = AF_INET;
        serv_addr.sin_addr.s_addr = INADDR_ANY;
        serv_addr.sin_port = htons(portno);


        /* VINCULA EL FILE DESCRIPTOR CON LA DIRECCION Y EL PUERTO */
        if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0)
                error("ERROR on binding");

        /* SETEA LA CANTIDAD QUE PUEDEN ESPERAR MIENTRAS SE MANEJA UNA CONEXION */
        listen(sockfd, 5);

        /* SE BLOQUEA A ESPERAR UNA CONEXION */
        clilen = sizeof(cli_addr);
        newsockfd = accept(sockfd,
                           (struct sockaddr *) &cli_addr,
                           &clilen);

        /* DEVUELVE UN NUEVO DESCRIPTOR POR EL CUAL SE VAN A REALIZAR LAS COMUNICACIONES */
        if (newsockfd < 0)
                error("ERROR on accept");

        bzero(buffer, BUFFER_SIZE);

        /* LEE EL MENSAJE DEL CLIENTE */
        n = read(newsockfd, buffer, BUFFER_SIZE - 1);
        if (n < 0)
                error("ERROR reading from socket");
        
        printf("Receibed message with %d characters\n", n);

        /* Obtener el tamaño del dato del cliente */
        size_t data_length;
        n = read(newsockfd, &data_length, sizeof(data_length));
        if (n < 0) error("ERROR reading from socket");
        if (strlen(buffer) == data_length)
        {
                printf("Length match!! Received size: %lu\nSize sent by Client %lu\n",
                       strlen(buffer),
                       data_length);
        } else {
                printf("The size received and the size of the data do not match\n");
        }

        /* Se obtiene el checksum del cliente */
        unsigned long buffer_checksum;
        n = read(newsockfd, &buffer_checksum, sizeof(buffer_checksum));
        if (n < 0) error("ERROR reading from socket");
        printf("Client Checksum: %lu\nServer checksum %lu\n", buffer_checksum, djb2(buffer));


        /* RESPONDE AL CLIENTE */
        n = write(newsockfd, "I got your message", 18);
        if (n < 0) error("ERROR writing to socket");

        return 0;
}


unsigned long
djb2(unsigned char *str)
{
        unsigned long hash = 5381;
        int c;

        while (c = *str++)
                hash = ((hash << 5) + hash) + c; /* hash * 33 + c */

        return hash;
}
