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
public class TSP {
    static int numNodos;
    static int[] caminos;
    static double[][] costos;
    static double recorrido;


    public static void main(String args[]) {
        

        
	TSP tsp = new TSP(Reader.readInput());
        
        
        //Genera una solucion inicial
        
        caminos = NearestNeighbour();
        
        System.out.println("NN: " + getCost(caminos));
                
//        for (int i = 0; i < numNodos; i++) {
//            caminos[i] = i;
//        }
//        double cost = getCost(caminos);
//        System.out.println(getCost(caminos));
//        //System.out.println(cost);
//        
//        
//        
//
//        Random rand = new Random();
        
        
//        for (int i = 0; i < 1000; i++) {
//            int swap1 = rand.nextInt(numNodos-1) +1;
//            int swap2 = rand.nextInt(numNodos-1) +1; 
//            
//            swap(swap1, swap2, caminos);
//        }
       
//        caminos = localSearch(caminos, cost);
//         System.out.println(getCost(caminos));
       
//        for(int i = 0; i < numNodos+1; i++) {
//            System.out.println(caminos[i]);
//        }
//        System.out.println();
       
        
        
        double costoFinal = annealing();
        
        
        
        System.out.println(costoFinal);
        
        
    }

    
    
    public static int[] localSearch(int[] solution, double cost) {
        
        
        boolean improvement = true;
        int[] newSol = solution.clone();
        double costy = cost;
        while(improvement) {
            
            improvement = false;
            for (int i = 1; i < numNodos-2; i++) {

                int[] nextSol = newSol.clone();
                for (int j = 1; j < numNodos-1; j++) {
                    //busca los candidatos
                    if (i != j) {
                        swap(nextSol[i], nextSol[j], nextSol);
                        double newCost = updateCost(newSol, nextSol, cost, i, j);
                        if (costy > newCost) {
                            improvement = true;
                            costy = newCost;
                            newSol = nextSol;
                        }
                    }
                }
            }
        }    
        
        return newSol;
    }
    
    //Realiza optimizacion mediante annealing
    public static double annealing() {
        
        double costoActual = getCost(caminos);
        System.out.println("Costo Inicial: " + costoActual);
        double mejorCosto = costoActual;     //Costo inicial
        double temperatura = 10000;           //Temperatura inicial
        double enfriamiento = 0.999993;        //Velocidad de enfriamiento
        int iteraciones = 0;
        int totalsh = 0;
        int conteo = 0;
        while (temperatura > 1) {

            int[] nuevaSol = new int[caminos.length];
                    
            nuevaSol =  caminos.clone();

            //Consigue dos ciudades al azar y distintas
            Random rand = new Random();
            int swap1 = rand.nextInt(numNodos-1);
            int swap2 = rand.nextInt(numNodos-1);

            swap(swap1, swap2, nuevaSol);

            
            double costoViejo = costoActual;
        
            costoActual = getCost(nuevaSol);
//
//            
            
            if (costoActual < mejorCosto) {
                caminos = nuevaSol;
                mejorCosto = costoActual;
                System.out.println("Costo: " + mejorCosto + "; numero de iteraciones " + iteraciones + "; totalsh:" + totalsh);
                iteraciones = 0;
                
            }
            else if (aceptar(costoViejo, costoActual, temperatura) < rand.nextDouble()) {
                caminos = nuevaSol;
                conteo++;
                //System.out.println("Getting in");
            }

            //Si encuentra un "Codo"
//            if (iteraciones > 250000) {
//                System.out.println("AHHH");
//                iteraciones = 0;
//                temperatura = 10000;
//                enfriamiento *= 0.999995;
//            }
            
            iteraciones ++;
            totalsh++;
            temperatura*= enfriamiento;
            
        }
        System.out.println("Conteo: " + conteo + " iteraciones :" + totalsh );
        return mejorCosto;
            
    }
    
    public static int[] NearestNeighbour() {
	int[] set = new int[numNodos];
        Arrays.fill(set, 0);
	int[] trace = new int[numNodos];
        int start = 0;
        trace[0] = start;
        
        for (int i = 0; i < numNodos-1; i++) {
            double bestCost = Double.MAX_VALUE;
            int newCity = 0;
        
            for (int j = 0; j < numNodos; j++) {
                if (costos[trace[i]][j] < bestCost && set[j] < 1) {
                    
                    bestCost = costos[trace[i]][j];
                    newCity = j;
                }
                        
            }
            trace[i+1] = newCity;
            set[trace[i]]++;
            set[newCity]++;
        }
        
	return trace;
    }
    
    
    public static double updateCost(int[] pathOld, int[] pathNew, double cost, int city1, int city2) {
	
        double newCost = cost;
	newCost -= costos[pathOld[city1]][pathOld[city1 - 1]];
	newCost -= costos[pathOld[city1]][pathOld[city1 + 1]];
	newCost += costos[pathNew[city1]][pathNew[city1 - 1]];
	newCost += costos[pathNew[city1]][pathNew[city1 + 1]];

	newCost -= costos[pathOld[city2]][pathOld[city2 - 1]];
	newCost -= costos[pathOld[city2]][pathOld[city2 + 1]];
	newCost += costos[pathNew[city2]][pathNew[city2 - 1]];
	newCost += costos[pathNew[city2]][pathNew[city2 + 1]];
	return newCost;
    }


    //Funcion de aceptacion probabilistica para casos malos
    public static double aceptar(double costoViejo, double costoNuevo, double temp) {
        double value = 1.6*Math.exp(0.000025*(costoViejo-costoNuevo) / temp);
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
    public TSP (double[][] costos) {
	this.costos = costos;
	this.numNodos = costos.length;
        recorrido = 0;
	caminos = new int[this.numNodos];
        
       
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
        File file = new File("C:\\Users\\GabrielAugusto\\Downloads\\rl1889.txt");
        try {
            
            Scanner scanner = new Scanner(file);
            int numNodos = scanner.nextInt();
            double[][] l = new double[numNodos][numNodos];
            scanner.nextInt();
            
            
            for(int i = 0; i < numNodos-1; ++i) {
                
                for(int j = 0; j < numNodos; ++j) {
                    l[i][j] = scanner.nextDouble();
                }
                scanner.nextInt();
                scanner.nextInt();
            }
            //Last isolated case
            for(int j = 0; j < numNodos; ++j) {
                    l[numNodos-1][j] = scanner.nextDouble();
            }
            
            for (int i = 0; i < numNodos; i++) {
                l[i][i] = Integer.MAX_VALUE;
            }
           
            return l;
        }
        catch (FileNotFoundException e) {
            System.out.println("File not Found");
        } 
        return null;
    }
}