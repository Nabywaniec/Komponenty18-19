package Model;

public class Edge {

    private double cost;
    private int firstVertexId;
    private int secondVertexId;

    public Edge(double cost, int firstVertexId, int secondVertexId) {
        this.cost = cost;
        this.firstVertexId = firstVertexId;
        this.secondVertexId = secondVertexId;
    }

    public double getCost() {
        return this.cost;
    }

    public int getFirstVertexId() {
        return firstVertexId;
    }

    public int getSecondVertexId() {
        return secondVertexId;
    }
}
