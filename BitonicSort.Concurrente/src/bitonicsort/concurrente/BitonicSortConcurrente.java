package bitonicsort.concurrente;

import java.util.Random; //Importa la clase para generar números aleatorios
import java.util.concurrent.RecursiveAction; //Clase base para tareas que no devuelven resultados
import java.util.concurrent.ForkJoinPool; //Pool de hilos para ejecutar tareas en paralelo


public class BitonicSortConcurrente {
    
    public static void main(String[] args) {
        
        int tam = 33554432; // El tamaño debe ser potencia de 2
        int[] array = new int[tam];
        Random rand = new Random();
        
        //int numHilos = 8;
        
        //Llenado del arreglo con números aleatorios entre 0 y 999
        for (int i = 0; i < tam; i++) {
            array[i] = rand.nextInt(1000);
        }

        //System.out.println("Arreglo original:");
        //mostrarArreglo(array);

        //ForkJoinPool pool = new ForkJoinPool(numHilos); Para utilizar una cantidad de hilos a eleccion
        
        ForkJoinPool pool = new ForkJoinPool(); //Utiliza una cantidad de hilos basada en la cantidad de procesadores de la maquina

        //Inicia cronómetro en nanosegundos
        long inicio = System.nanoTime();

        // Ejecuta la tarea principal que ordena todo el arreglo en orden ascendente
        pool.invoke(new BitonicSortTask(array, 0, tam, ASCENDENTE));

        long fin = System.nanoTime();

        //Muestra cantidad de núcleos disponibles
        System.out.println("Procesadores disponibles: " + Runtime.getRuntime().availableProcessors());

        //Calcula el tiempo en milisegundos con mayor precisión
        double tiempoMs = (fin - inicio) / 1_000_000.0;
        System.out.printf("Tiempo de ejecución: %.3f ms%n", tiempoMs);

        System.out.println("Arreglo ya ordenado");
        //mostrarArreglo(array);

    }
    
    //Constantes para indicar el sentido de ordenamiento.    
    static final int ASCENDENTE = 1;    // Constante para indicar orden ascendente
    static final int DESCENDENTE = 0;  // Constante para indicar orden descendente

    // Esta clase representa la tarea de ordenamiento bitónico concurrente
    // Extiende RecursiveAction, ya que no devuelve resultado
    // Ordena una subsecuencia del arreglo 'array' desde la posición 'low',
    // con 'cant' elementos, en la dirección 'dir' (1 = ascendente, 0 = descendente)
    static class BitonicSortTask extends RecursiveAction {
        
        private final int[] array;// Arreglo a ordenar
        private final int low;    // Índice inicial del subarreglo
        private final int cant;    // Cantidad de elementos a ordenar
        private final int dir;    //dirección de orden: 1 = asc, 0 = desc

        // Constructor
        BitonicSortTask(int[] a, int low, int cant, int dir) {
            this.array = a;
            this.low = low;
            this.cant = cant;
            this.dir = dir;
        }
        
        // Método principal que define la lógica de ordenamiento
       @Override
        protected void compute() {
            
            //Si el array tiene más de un elemento, divide la secuencia en dos mitades.
            if (cant > 1) {
                int k = cant / 2;
                
              // Lanza dos tareas en paralelo:
              // Una para ordenar la primera mitad en orden ascendente,
              // y otra para la segunda mitad en orden descendente
                invokeAll(
                    new BitonicSortTask(array, low, k, ASCENDENTE),
                    new BitonicSortTask(array, low + k, k, DESCENDENTE)
                );
                
                // Una vez que ambas mitades están ordenadas,
                // se fusionan en la dirección indicada por 'dir'
                invokeAll(new BitonicMergeTask(array, low, cant, dir));
            }
        }
    }

    // Esta clase representa la tarea de fusión bitónica concurrente.
    // Toma una subsecuencia del arreglo 'array' desde la posición 'low',
    // con 'cant' elementos, y la fusiona en la dirección 'dir'
    static class BitonicMergeTask extends RecursiveAction {
        
        private final int[] array;
        private final int low, cant, dir;

        BitonicMergeTask(int[] a, int low, int cant, int dir) {
            this.array = a;
            this.low = low;
            this.cant = cant;
            this.dir = dir;
        }

         @Override
        protected void compute() {
            
            if (cant > 1) {
                //compara y ordena elementos por pares según la dirección.
                bitonicCompare(array, low, cant, dir);
                
                //divide la secuencia y vuelve a aplicar la fusión recursivamente en paralelo.
                int k = cant / 2;
                // Lanza dos tareas en paralelo para fusionar ambas mitades recursivamente
                invokeAll(
                    new BitonicMergeTask(array, low, k, dir),
                    new BitonicMergeTask(array, low + k, k, dir)
                );
            }
        }
    }

    // Compara y ordena los elementos en pares entre la primera y segunda mitad
    // Si están en orden incorrecto según la dirección 'dir', los intercambia
    // 'cant' debe ser divisible por 2
    static void bitonicCompare(int[] arr, int low, int cant, int dir) {
        
        int k = cant / 2;
        
        // Recorre desde 'low' hasta 'low + k' comparando cada par de elementos
        for (int i = low; i < low + k; i++) {
            
            // Si la dirección es ascendente y el primer valor es mayor que el segundo
            // o si la dirección es descendente y el primero es menor que el segundo
            // entonces los elementos están en orden incorrecto y se intercambian
            if ((dir == ASCENDENTE && arr[i] > arr[i + k]) ||
                (dir == DESCENDENTE && arr[i] < arr[i + k])) {
                // Intercambio de elementos
                int temp = arr[i];
                arr[i] = arr[i + k];
                arr[i + k] = temp;
            }
        }
    }

    //Método para mostrar el arreglo
    static void mostrarArreglo(int[] array) {
        for (int j : array) {
            System.out.print(j + " | ");
        }
        System.out.println();
    }

    
}
    

