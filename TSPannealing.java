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
// package tsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;
import java.lang.Math;

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
public class TSPannealing {
    static int numNodos;
    static int[] caminos;
    static double[][] costos;
    static double recorrido;


    public static void main(String args[]) {
        

        
	TSPannealing tsp = new TSPannealing(Reader.readInput());
        
        
        //Genera una solucion inicial
        
        caminos = NearestNeighbour();
        
        System.out.println("NN: " + getCost(caminos));
        
        for(int i = 0; i < numNodos+1; i++) {
            System.out.println(caminos[i]);
        }
        System.out.println();
       
        
        
//        
//        for (int i = 0; i <= numNodos; i++) {
//            caminos[i] = i % numNodos;
//        }
//       
//        Random rand = new Random();
//        double CostOri = getCost(caminos);
//        int swap1 = rand.nextInt(numNodos-1) +1;
//        int swap2 = rand.nextInt(numNodos-1) +1; 
//            
//        swap(swap1, swap2, caminos);
//        
//        double Cost1 = getCost(caminos);        
//        double Cost2 = reCost(swap1, swap2, caminos, CostOri);
//        
//        System.out.println("Cost1: " + Cost1 + " and Cost2: " + Cost2);
        
//        
//        for (int i = 0; i < 1000; i++) {
//            int swap1 = rand.nextInt(numNodos-1) +1;
//            int swap2 = rand.nextInt(numNodos-1) +1; 
//            
//            swap(swap1, swap2, caminos);
//        }
//       
        
        
//        double costoFinal = annealing();
//        
//        System.out.println(costoFinal);
    }

    //Realiza optimizacion mediante annealing
    public static double annealing() {
        
        double costoActual = getCost(caminos);
        System.out.println("Costo Inicial: " + costoActual);
        double mejorCosto = costoActual;     //Costo inicial
        double temperatura = 10000;                //Temperatura inicial
        double enfriamiento = 0.999;        //Velocidad de enfriamiento
        
        while (temperatura > 1) {

            int[] nuevaSol = caminos.clone();

            //Consigue dos ciudades al azar y distintas
            Random rand = new Random();
            int swap1 = rand.nextInt(numNodos-1)+1;
            int swap2 = rand.nextInt(numNodos-1)+1;

            swap(swap1, swap2, nuevaSol);

            double costoViejo = costoActual;
            costoActual = getCost(nuevaSol);


            if (costoActual < mejorCosto) {
                caminos = nuevaSol;
                mejorCosto = costoActual;
            }
            else if (aceptar(costoViejo, costoActual, temperatura) < rand.nextDouble()) {
                caminos = nuevaSol;
 
            }

            temperatura*= enfriamiento;
        }
  
        return mejorCosto;
            
    }
    
    public static int[] NearestNeighbour() {
        
	int[] set = new int[numNodos];
        Arrays.fill(set, 0);
	int[] trace = new int[numNodos + 1];
        int start = 0;
        trace[0] = start;
        trace[numNodos] = 0;
        
        for (int i = 0; i < numNodos; i++) {
            double bestCost = Double.MAX_VALUE;
            int newCity = 0;
        
            for (int j = 0; j < numNodos; j++) {
                if (costos[trace[i]][j] < bestCost && set[j] < 1) {
                    
                    bestCost = costos[caminos[i]][j];
                    newCity = j;
                }
                        
            }
            trace[i+1] = newCity;
            set[trace[i]]++;
            set[newCity]++;
        }
        
	return trace;
    }
    
    
//    public static double reCost(int ciudad1, int ciudad2, int[] caminos, double costoViejo) {
//        double nuevoCosto = costoViejo 
//                    - costos[caminos[ciudad1-1]][caminos[ciudad1]]
//                    -costos[caminos[ciudad1]][caminos[ciudad1+1]]
//                    - costos[caminos[ciudad2-1]][caminos[ciudad2]]
//                    -costos[caminos[ciudad2]][caminos[ciudad2+1]] 
//                    + costos[caminos[ciudad1-1]][caminos[ciudad2]]
//                    + costos[caminos[ciudad2]][caminos[ciudad1+1]]
//                    + costos[caminos[ciudad2-1]][caminos[ciudad1]]  
//                    + costos[caminos[ciudad1+1]][caminos[ciudad2]];
//                    
//                    
//        
//        return nuevoCosto;
//    }
//    
    //Funcion de aceptacion probabilistica para casos malos
    public static double aceptar(double costoViejo, double costoNuevo, double temp) {
        double value = Math.exp(0.25*(costoViejo-costoNuevo) / temp);
        //System.out.println(value);
        
        return value;
    }
    
    
    //Devuelve el costo de un camino dado
    public static double getCost(int[] solucion) {
        double costo = 0;
    
        for (int i = 0; i < solucion.length-1; i++) {
            costo += costos[solucion[i]][solucion[i+1]];
        }
        
        return costo;
    }
    
    //Intercambia dos elementos del camino
    public static int[] swap(int ciudad1, int ciudad2, int[] solucion) {
        
        if (costos[ciudad1][ciudad2] > 0) {     //Asi revisa que no intercambia un nodo consigo mismo
            int temp = solucion[ciudad1];
            solucion[ciudad1] = solucion[ciudad2];
            solucion[ciudad2] = temp;
        }
        return solucion;
    }
    
    //Constructor de la clase TSP
    public TSPannealing (double[][] costos) {
	this.costos = costos;
	this.numNodos = costos.length;
        recorrido = 0;
	caminos = new int[this.numNodos+1];
        
       
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
        // File file = new File("C:\\Users\\GabrielAugusto\\Downloads\\output_144.txt");
        // try {
            
            Scanner scanner = new Scanner(System.in);
            int numNodos = scanner.nextInt();
            double[][] l = new double[numNodos][numNodos];

            for(int i = 0; i < numNodos; ++i) {
                scanner.nextDouble();
                for(int j = 0; j < numNodos; ++j) {
                    l[i][j] = scanner.nextDouble();
                }
            }
            for (int i = 0; i < numNodos; i++) {
                l[i][i] = Integer.MAX_VALUE;
            }
            return l;
            
        // }
        // catch (FileNotFoundException e) {
        //     System.out.println("File not Found");
        // } 
        // return null;
    }
}