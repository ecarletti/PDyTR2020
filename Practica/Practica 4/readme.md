# FTP
`git clone https://github.com/ecarletti/PDyTR2020.git`

`cd PDyTR2020/Practica/Practica\ 4/src`
## Upload file
`./FTP/run write -o file_name_in_store -i file_path_to_upload`

ejemplo: se quiere escribir el archivo prueba en el store `./FTP/run write -o prueba -i $HOME/java.pdf`

## Download file
`./FTP/run read -i file_name_in_store -o file_path_to_download`

ejemplo: se quiere leer el archivo prueba en el store `./FTP/run read -i prueba -o $HOME/file`
