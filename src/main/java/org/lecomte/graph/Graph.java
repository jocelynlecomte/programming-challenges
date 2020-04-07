package org.lecomte.graph;

import java.util.*;

public abstract class Graph<T> {
    List<T> depthFirstTraversal(T root) {
        List<T> visited = new ArrayList<>();
        Stack<T> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            T current = stack.pop();
            if (!visited.contains(current)) {
                visited.add(current);
                getAdjacentVertices(current).stream()
                        .filter(neighbour -> !visited.contains(neighbour))
                        .forEach(stack::push);
            }
        }

        return visited;
    }

    List<T> breadthFirstTraversal(T root) {
        List<T> visited = new ArrayList<>();
        Queue<T> queue = new LinkedList<>();
        visited.add(root);
        queue.add(root);
        while (!queue.isEmpty()) {
            T current = queue.poll();
            getAdjacentVertices(current).stream()
                    .filter(neighbour -> !visited.contains(neighbour))
                    .forEach(neighbour -> {
                        visited.add(neighbour);
                        queue.add(neighbour);
                    });
        }

        return visited;
    }

    public abstract List<T> getAdjacentVertices(T value);
}
