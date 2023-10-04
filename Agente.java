import java.util.Random;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Erik
 */
public class Agente extends Thread{
    private final String nombre;
    private int i;
    private int j;
    private ImageIcon icon;
    private ImageIcon icon2;
    private ImageIcon nave;
    private ImageIcon muestra;
    private int[][] matrix;
    private JLabel tablero[][];
    private Set<int[]> naves;
    private int naveX;
    private int naveY;
    private int ocupado;
    private int[] dx= {1,0,-1,0}, dy= {0,1,0,-1};
    private int[] auxDir = {2, 3, 0, 1};
    private String dir = "drul";
    
    private JLabel casillaAnterior;
    Random aleatorio = new Random(System.currentTimeMillis());
    
    public Agente(String nombre, ImageIcon icon, ImageIcon icon2, int[][] matrix, JLabel tablero[][], Set<int[]> naves, ImageIcon nav, ImageIcon muestra){
        this.nombre = nombre;
        this.icon = icon;
        this.matrix = matrix;
        this.tablero = tablero;
        this.naves = naves;
        this.icon2 = icon2;
        this.nave = nav;
        this.muestra = muestra;
        ocupado = 0;
        
        this.i = aleatorio.nextInt(matrix.length);
        this.j = aleatorio.nextInt(matrix.length);
        tablero[i][j].setIcon(icon);     
    }
    
    @Override
    public void run(){
        int dir = -1,  dirAnterior = -1;
        naveX = naves.iterator().next()[0];
        naveY = naves.iterator().next()[1];

        while(true){
            //System.out.println("Ocupado" + " " + ocupado);
            casillaAnterior = tablero[i][j];
            
            if(ocupado == 0){
                
                
                //Estamos en un ratro de migas
                if(matrix[i][j] >= 30) {
                    System.out.println("No estoy ocupado");
                    System.out.println(i + ","  + j);
                    dir = matrix[i][j] - 30;
                    i -= dx[dir];
                    j -= dy[dir];

                    if(matrix[i][j] > 20 && matrix[i][j] <= 23) {
                        dir = auxDir[dir];
                    }
                }
                else{
                    dir = aleatorio.nextInt(4);  
                    while(!ok(i + dx[dir], j + dy[dir])){
                        dir = aleatorio.nextInt(4);
                    }

                    i += dx[dir];
                    j += dy[dir];
                }

                
                if(matrix[i][j] > 20 && matrix[i][j] <= 23){
                    casillaAnterior.setIcon(null);
                    tablero[i][j].setIcon(icon);
                    
                    ocupado ^= 1;
                    matrix[i][j]--;

                    if(matrix[i][j] == 20) {
                        matrix[i][j] = 0;
                    }

                    casillaAnterior.setIcon(icon);
                    tablero[i][j].setIcon(muestra);

                    i -= dx[dir];
                    j -= dy[dir];
                    swap();
                }

                actualizarPosicion();
                
            }
            else{
                String recorrido = busquedaA();
                dirAnterior = Character.getNumericValue(recorrido.charAt(0));
                
                for(int c = 0; c < recorrido.length(); c++) {
                    casillaAnterior = tablero[i][j];
                    dir = Character.getNumericValue(recorrido.charAt(c));

                    if(matrix[i + dx[dir]][j + dy[dir]] != 2) {
                        matrix[i][j] = (dirAnterior + 30);
                    }
                    i += dx[dir];
                    j += dy[dir];

                    if(matrix[i][j] == 2){
                        casillaAnterior.setIcon(null);
                        tablero[i][j].setIcon(icon);
                        ocupado ^= 1;
                        swap();
                        casillaAnterior.setIcon(icon);
                        tablero[i][j].setIcon(nave);

                        i -= dx[dir];
                        j -= dy[dir];

                        matrix[i][j] = dirAnterior + 30;
                    }
                    else{
                        actualizarPosicion();
                    }
                    dirAnterior = dir;

                    pausa();
                }

                System.out.println("Entregado");
                System.out.println(matrix[i][j]);
                System.out.println(ocupado);

                imp();
                //break;
            }
            
            pausa();
        }

                      
    }


    private String busquedaA() {

        PriorityQueue<Nodo> pq = new PriorityQueue<>(new NodoComparator());
        Nodo inicio = new Nodo(i, j, 0, "", 0);
        pq.add(inicio);
        Nodo actual = inicio, anterior = inicio;

        int iteraciones = 0;
        while(!pq.isEmpty()) {
            iteraciones++;
            anterior = actual;
            actual = pq.poll();

            if(actual.getX() == naveX && actual.getY() == naveY) {
                break;
            }

            for(int i = 0; i < 4; i++) {
                if(ok3(actual, i)) {
                    int x = actual.getX() + dx[i];
                    int y = actual.getY() + dy[i];
                    int dis = actual.getDist() + 1;
                    String recorrido = actual.getRecorrido() + Integer.toString(i);
                    int prio = dis + Math.abs(naveX - x) + Math.abs(naveY - y);

                    if(anterior.getY() != x || anterior.getY() != y) {
                        Nodo aux = new Nodo(x, y, dis, recorrido, prio);
                        pq.add(aux);
                    }
                }
            }
        }

        //System.out.println(actual.getRecorrido());
        //System.out.println(iteraciones);

        return actual.getRecorrido();
    }
    
    public int[] pos(){
        int[] res = {i,j};
        return res;
    }
    
    //Actualiza la matriz
    public void actMat(int[][] m){
        for(int i=0; i<15; i++){
            for(int j=0; j<15; j++){
                matrix[i][j] = m[i][j];
            }
        }
    }
    
    public void imp(){
        System.out.println("");
        for(int i=0; i<15; i++){
            for(int j=0; j<15; j++){
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println("");
        }

        System.out.println("");
    }
    
    //0: vacio, 1:robot, 2:nave, 23:muestra, 4: obstaculo
    private boolean ok(int x, int y){
        if(x>=0 && y>=0 && x<matrix.length && y<matrix.length){
            if(matrix[x][y] == 4)  return false;
            if(matrix[x][y] == 2)  return false;
            return true;
        }
        return false;
    }
    
    private boolean ok2(int x, int y){
        if(x>=0 && y>=0 && x<matrix.length && y<matrix.length){
            if(matrix[x][y] == 4)  return false;
            if(matrix[x][y] > 20 && matrix[x][y] <= 23)  return false;
            //if(matrix[x][y]==2)  return 0;
            return true;
        }
        return false;
    }

    private boolean ok3(Nodo a, int i) {
        int x = a.getX() + dx[i];
        int y = a.getY() + dy[i];
        
        return ok2(x, y);
    }

    //Auxiliar para detener el programa y que sean visibles las animaciones
    private void pausa() {
        try{
            sleep(500);
        }
        catch (InterruptedException ex){
            ex.printStackTrace(System.out);
        }
    }
    
    //Intercambia los iconos si esta ocupado o desocupado
    private void swap(){
        ImageIcon aux = icon;
        icon = icon2;
        icon2 = aux;
    }
    
    public synchronized void actualizarPosicion(){
        casillaAnterior.setIcon(null); // Elimina su figura de la casilla anterior
        tablero[i][j].setIcon(icon); // Pone su figura en la nueva casilla
        //System.out.println(nombre + " in -> Row: " + i + " Col:"+ j);              
    }
    
    public synchronized void actualizarPosicionConNave(){
        casillaAnterior.setIcon(null);
        tablero[i][j].setIcon(icon);
    }
    
}
