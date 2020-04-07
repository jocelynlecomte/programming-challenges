package org.lecomte.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdjacencyMatrixGraphTest {
    private AdjacencyMatrixGraph graph;

    @BeforeEach
    public void init() {
        this.graph = createGraph();
    }

    @Test
    public void depthFirstTraversal() {
        List<Integer> traversal = this.graph.depthFirstTraversal(0);
        assertThat(traversal).containsExactly(0, 2, 4, 3, 1);
    }

    @Test
    public void breadthFirstTraversal() {
        List<Integer> traversal = this.graph.breadthFirstTraversal(0);
        assertThat(traversal).containsExactly(0, 1, 2, 3, 4);
    }

    private AdjacencyMatrixGraph createGraph() {
        AdjacencyMatrixGraph graph = new AdjacencyMatrixGraph(5);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);

        return graph;
    }
}