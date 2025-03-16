package com.example.oriedita_core.origami.folding.fold.model;

import java.util.ArrayList;
import java.util.List;

public class Face {
    private List<Vertex> vertices = new ArrayList();
    private List<Edge> edges = new ArrayList();

    public Face() {
    }

    public List<Vertex> getVertices() {
        return this.vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Edge> getEdges() {
        return this.edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}
