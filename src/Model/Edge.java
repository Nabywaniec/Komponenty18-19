package Model;

public class Edge {

    private int cost;
    private int firstVertexId;
    private int secondVertexId;

    public Edge(int cost, int firstVertexId, int secondVertexId) {
        this.cost = cost;
        this.firstVertexId = firstVertexId;
        this.secondVertexId = secondVertexId;
    }

    public int getCost() {
        return this.cost;
    }

    public int getFirstVertexId() {
        return firstVertexId;
    }

    public int getSecondVertexId() {
        return secondVertexId;
    }
}
