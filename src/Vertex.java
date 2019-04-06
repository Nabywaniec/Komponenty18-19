import java.util.List;

public class Vertex {
    private List<Vertex> neighbours;
    private List<Vertex> redirections;
    private int id;

    public Vertex(int id) {
        this.id = id;
    }
    public void addNeighbour(Vertex neighbour) {
        this.neighbours.add(neighbour);
    }

    public void setRedirection(List<Vertex> redirections) {
        this.redirections = redirections;
    }

    public List<Vertex> getNeighbours() {
        return this.neighbours;
    }

    public List<Vertex> getRedirections() {
        return this.redirections;
    }

    public int getId(){
        return this.id;
    }

}
