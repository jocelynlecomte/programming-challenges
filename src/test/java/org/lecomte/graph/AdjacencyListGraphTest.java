package org.lecomte.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdjacencyListGraphTest {
    private AdjacencyListGraph<String> graph;

    @BeforeEach
    public void init() {
        this.graph = createGraph();
    }

    @Test
    public void depthFirstTraversal() {
        List<String> traversal = this.graph.depthFirstTraversal("Bob");
        assertThat(traversal).containsExactly("Bob", "Rob", "Mark", "Maria", "Alice");
    }

    @Test
    public void breadthFirstTraversal() {
        List<String> traversal = this.graph.breadthFirstTraversal("Bob");
        assertThat(traversal).containsExactly("Bob", "Alice", "Rob", "Maria", "Mark");
    }

    AdjacencyListGraph<String> createGraph() {
        AdjacencyListGraph<String> graph = new AdjacencyListGraph<String>();
        graph.addVertex("Bob");
        graph.addVertex("Alice");
        graph.addVertex("Mark");
        graph.addVertex("Rob");
        graph.addVertex("Maria");
        graph.addEdge("Bob", "Alice");
        graph.addEdge("Bob", "Rob");
        graph.addEdge("Alice", "Mark");
        graph.addEdge("Rob", "Mark");
        graph.addEdge("Alice", "Maria");
        graph.addEdge("Rob", "Maria");

        return graph;
    }
}