/*
NOTA: estoy leyendo directamente de el archivo porque fuck da police
LAS ESTRUCTURAS SE USARON PORQUE    
numNodos: Permite la iteracion a traves de los elementos sencillamente,
          sintener que recurrir a funciones de tamaño de arreglos
caminos:  Asigna sencillamente a cada elemento su siguiente. Permite conocer
          el sucesor en orden lineal, lo cual ayuda enormemente para optimizacion 2-opt.
          Tambien otorga la idea de un ciclo dirigido, que tiene mas sentido 
          que con una matriz de adyacencias.
costos:   Permite realizar busquedas de costos en orden constante. Como los costos
          son preprocesados, el costo(redundancia) se amortiza sobre el tiempo
recorrido:bleh

 */
package tsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;

/*
  Clase: TSP
  FunciÃ³n: implementa el TSP. Esta es la clase principal.
  ParÃ¡metros:
  - numNodos: representa el nÃºmero de nodos que tiene el grafo.
  - listaNodos: es un arreglo que almacena todos los nodos del grafo.
  - caminos: Arreglo que posee para cada elemento, a cual se mueve (la siguiente ciudad a recorrer)

  - costos: Es una matriz de double que guarda en cada posiciÃ³n (i, j) 
            el costo de viajar entre el nodo i y el nodo j, y viceversa.
  - vecinos: Lista ordenada de los nodos mas cercanos a cada nodo.
  - recorrido: define el costo total de hacer un recorrido.
*/
public class TSP {
    static int numNodos;
    static int[] caminos;
    static double[][] costos;
    static double recorrido;


    public static void main(String args[]) {
	TSP tsp = new TSP(Reader.readInput());
        
        //Crea un camino inicial mediante busqueda de vecinos (Nearest Neighbor)
        int[] set = new int[numNodos];
        Arrays.fill(set,0);
        int start = 30;          //Nodo donde comienza y cierra el ciclo
        int num = numNodos;
        int l = start;
	while (num-- > 1) {
            double menor = Double.MAX_VALUE;
            int nodoDes = 0;
            for(int j = 0; j < numNodos; ++j) {
                if (costos[l][j] <= menor && set[j] < 1 && (l != j)) {
                    menor = costos[l][j];
                    nodoDes = j;
                }
            }
            caminos[l] = nodoDes;
            recorrido += menor;
            set[l]++;
            set[nodoDes]++;
            l = nodoDes;
            
	}
        caminos[l] = start;
        recorrido += costos[l][start];
        System.out.println("METODO DEL VECINO MAS CERCANO");
        System.out.println("Costo total= " + recorrido);
        
        //Optimiza el camino ya creado mediante 2-opt
        boolean improvement = true;
        while(improvement) {
            improvement = false;
            for (int i = 0; i < numNodos-1; i++) {
                for (int j = i+1; j < numNodos; j++) {
                    //busca los candidatos
                    int i2 = caminos[i];
                    int j2 = caminos[j];
                        double costoActual = costos[i][i2] + costos[j][j2];
                        double nuevoCosto = costos[i][j] + costos[i2][j2];
                        if (costoActual > nuevoCosto) {
                            improvement = true;
                            recorrido = recorrido - costoActual + nuevoCosto;
                            caminos[i] = j;
                            caminos[i2] = j2;
                        }
                }
            }
        }    
        System.out.println("OPTIMIZACION 2-OPT");
        System.out.println("Costo total= " + recorrido);
    }

    
    public TSP (double[][] costos) {
	this.costos = costos;
	this.numNodos = costos.length;
        recorrido = 0;
	caminos = new int[numNodos];
        
       
    }
}


/*
  Clase: Reader
  FunciÃ³n: procesa el archivo de entrada y crea la lista de nodos.
*/

class Reader {
    public Reader() {

    }

    /*
      Método: readInput.
      FunciÃ³n: lee la información por entrada estándar 
      (mediante uno de los archivos output.txt creados con Preprocessor)
    */
    public static double[][] readInput() {
        File file = new File(System.in);
        try {
            
            Scanner scanner = new Scanner(file);
            int numNodos = scanner.nextInt();
            double[][] l = new double[numNodos][numNodos];

            for(int i = 0; i < numNodos; ++i) {
                scanner.nextDouble();
                for(int j = 0; j < numNodos; ++j) {
                    l[i][j] = scanner.nextDouble();
                }
            }
            for (int i = 0; i < numNodos; i++) {
                l[i][i] = 1000000;
            }
            return l;
            
        }
        catch (FileNotFoundException e) {
            System.out.println("File not Found");
        } 
        return null;
    }
}