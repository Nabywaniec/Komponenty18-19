package Model;

public class Vertex {
    private int id;
    private int x;
    private int y;

    public Vertex(int id) {
        this.id = id;
    }

    public Vertex(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
    public int getId() {
        return this.id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
