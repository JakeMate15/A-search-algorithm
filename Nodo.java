import java.util.*;

public class Nodo {
    private int x, y, dist, prioridad;
    private String recorrido;

    public Nodo() {
        x = -1;
        y = -1;
        dist = 0;
        prioridad = 0;
        recorrido = "";
    }

    public Nodo(int x, int y, int dist, String recorrido, int prioridad) {
        this.x = x;
        this.y = y;
        this.dist = dist;
        this.recorrido = recorrido;
        this.prioridad = prioridad;
    }


    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDist() {
        return this.dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public int getPrioridad() {
        return this.prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public String getRecorrido() {
        return this.recorrido;
    }

    public void setRecorrido(String recorrido) {
        this.recorrido = recorrido;
    }
}

class NodoComparator implements Comparator<Nodo> {
    public int compare(Nodo n1, Nodo n2) {
        if(n1.getPrioridad() > n2.getPrioridad())
            return 1;
        if(n1.getPrioridad() < n2.getPrioridad())
            return -1;
        return 0;
    }
}
