package org.lecomte.graph;

import java.util.*;

import static java.util.stream.Collectors.toList;

class AdjacencyListGraph<T> extends Graph<T> {
    private Map<Vertex<T>, Set<Vertex<T>>> adjacentVertices = new HashMap<>();

    public void addVertex(T value) {
        adjacentVertices.putIfAbsent(new Vertex<>(value), new HashSet<>());
    }

    public void removeVertex(T value) {
        Vertex<T> v = new Vertex<>(value);

        adjacentVertices.values().forEach(adjacentVertices -> adjacentVertices.remove(v));
        adjacentVertices.remove(v);
    }

    public void addEdge(T value1, T value2) {
        Vertex<T> v1 = new Vertex<>(value1);
        Vertex<T> v2 = new Vertex<>(value2);

        adjacentVertices.get(v1).add(v2);
    }

    public void removeEdge(T value1, T value2) {
        Vertex<T> v1 = new Vertex<>(value1);
        Vertex<T> v2 = new Vertex<>(value2);

        Set<Vertex<T>> adjList1 = adjacentVertices.get(v1);
        if (adjList1 != null) {
            adjList1.remove(v2);
        }
    }

    public List<T> getAdjacentVertices(T value) {
        return adjacentVertices.get(new Vertex<>(value)).stream()
                .map(vertex -> vertex.value)
                .sorted()
                .collect(toList());
    }
}
