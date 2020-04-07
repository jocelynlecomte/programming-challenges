package org.lecomte.graph;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyMatrixGraph extends Graph<Integer> {
    private boolean[][] adjacencyMatrix;

    public AdjacencyMatrixGraph(int size) {
        this.adjacencyMatrix = new boolean[size][size];
    }

    public void addEdge(Integer value1, Integer value2) {
        adjacencyMatrix[value1][value2] = true;
    }

    @Override
    public List<Integer> getAdjacentVertices(Integer value) {
        List<Integer> neighbours = new ArrayList<>();
        for (int i = 0; i < adjacencyMatrix[value].length; i++) {
            if (adjacencyMatrix[value][i]) {
                neighbours.add(i);
            }
        }
        return neighbours;
    }
}
