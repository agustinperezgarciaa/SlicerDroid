****************************************************************************
*                              SlicerDroid                                 *
*                                                                          *
* Version 1, 30 September 2015

 Copyright (C) 2015 Free Software Foundation *
*                                                                          *
****************************************************************************

Requisitos básicos:

- Cualquier distribución de Linux.
- JDK (en caso de no tenerlo instalado, puede obtenerlo desde el siguiente 
  link: http://www.oracle.com/technetwork/es/java/javase/downloads/index.html)
- Python (en caso de no tenerlo instalado, puede obtenerlo desde el siguiente link: https://www.python.org/)
- Androguard (que se puede descargar accediendo al siguiente 
  link: https://code.google.com/p/androguard/downloads/detail?name=androguard-1.9.tar.gz)
- Graphviz (en caso de no tenerlo instalado, puede obtenerlo desde el siguiente
  link: http://www.graphviz.org/Download..php)

Instrucciones de uso:

- Elija cualquier aplicación Android (.apk)  	

- Ingrese desde una terminal a la carpeta androguard-1.9 y ejecute el siguiente 
  comando: ./androdd.py -i ~/RutaAplicacionAndroid.apk -png -o  ~/RutaSalida
  el cual devuelve como salida una carpeta llamada RutaAplicacionAndroid. Dentro de
  esta carpeta se encuentran subcarpetas que contienen archivos con extension ag.

- Seleccione el archivo .ag que desee procesar y copielo.

- Pegue el archivo .ag copiado en la carpeta SlicerDroid.

- Ingrese desde una terminal a la carpeta SlicerDroid y ejecute el siguiente comando:
  java -jar SlicerDroid.jar -help
  éste le indicara todos los comandos posibles para obtener la salida que usted requiera.
  Ejemplo de uso:
  java -jar SlicerDroid.jar -cfg -pdt -png 
  éste comando dará como salida dos archivo con extension .png, los cuales representan el grafo de control de flujo
  y el arbol postdominador correspondientes al archivo .ag de entrada.
  NOTA: cabe aclarar que debe explicitar dentro de la linea de comando al menos uno de los siguientes flags:
        -dot o -png (que dan como salida un archivo con extensión .dot y .png respectivamente), sino no habrá
        salida alguna.