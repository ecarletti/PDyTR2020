#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

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
        int sockfd, portno, n;
        struct sockaddr_in serv_addr;
        struct hostent *server;

        char buffer[BUFFER_SIZE];

        if (argc < 3)
        {
                fprintf(stderr, "usage %s hostname port\n", argv[0]);
                exit(0);
        }

        /* TOMA EL NUMERO DE PUERTO DE LOS ARGUMENTOS */
        portno = atoi(argv[2]);

        /* CREA EL FILE DESCRIPTOR DEL SOCKET PARA LA CONEXION */
        sockfd = socket(AF_INET, SOCK_STREAM, 0);
        /* AF_INET - FAMILIA DEL PROTOCOLO - IPV4 PROTOCOLS INTERNET */
        /* SOCK_STREAM - TIPO DE SOCKET */

        if (sockfd < 0)
                error("ERROR opening socket");

        /* TOMA LA DIRECCION DEL SERVER DE LOS ARGUMENTOS */
        server = gethostbyname(argv[1]);
        if (server == NULL)
        {
                fprintf(stderr, "ERROR, no such host\n");
                exit(0);
        }

        bzero((char *) &serv_addr, sizeof(serv_addr));
        serv_addr.sin_family = AF_INET;

        /* COPIA LA DIRECCION IP Y EL PUERTO DEL SERVIDOR A LA ESTRUCTURA DEL SOCKET */
        bcopy((char *) server->h_addr,
              (char *) &serv_addr.sin_addr.s_addr,
              server->h_length);
        serv_addr.sin_port = htons(portno);

        /* DESCRIPTOR - DIRECCION - TAMAÑO DIRECCION */       
        if (connect(sockfd,(const struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0) 
                error("ERROR connecting");
       //Seteamos el buffer completo con el caracter e 
       
       //https://www.ibm.com/support/knowledgecenter/SSLTBW_2.3.0/com.ibm.zos.v2r3.bpxbd00/memset.htm
        memset((buffer), 'e', BUFFER_SIZE - 1 );

        // Se comentan las proximas lineas que pedian el ingreso de caracteres par enviarlo al server
       // printf("Please enter the message: ");
       // bzero(buffer,BUFFER_SIZE);
       // fgets(buffer,BUFFER_SIZE,stdin);
        //ENVIA UN MENSAJE AL SOCKET
         //       n = write(sockfd,buffer,strlen(buffer));
       // if (n < 0) 
                // error("ERROR writing to socket");
       // bzero(buffer,BUFFER_SIZE);



	
        /* ENVIA UN MENSAJE AL SOCKET */
        n = write(sockfd, buffer, strlen(buffer));
        if (n < 0)
                error("ERROR writing to socket");
   

        //Envia el tamaño del dato
        size_t data_length = strlen(buffer);
        n = write(sockfd, &data_length, sizeof(data_length));

        //Envia el checksum
        unsigned long buffer_hash = djb2(buffer);
        n = write(sockfd, &buffer_hash, sizeof(buffer_hash));

        bzero(buffer, BUFFER_SIZE);

        /* ESPERA RECIBIR UNA RESPUESTA */
        n = read(sockfd, buffer, BUFFER_SIZE - 1);
        if (n < 0)
                error("ERROR reading from socket");

        printf("%s\n", buffer);

        return 0;
}

// función para hash
unsigned long
djb2(unsigned char *str)
{
        unsigned long hash = 5381;
        int c;

        while (c = *str++)
                hash = ((hash << 5) + hash) + c; /* hash * 33 + c */

        return hash;
}
