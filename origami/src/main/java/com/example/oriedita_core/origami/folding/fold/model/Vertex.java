package com.example.oriedita_core.origami.folding.fold.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vertex {
    private Double x;
    private Double y;
    private Double z;
    private List<Vertex> vertices = new ArrayList();
    private List<Face> faces = new ArrayList();

    public Vertex() {
    }

    public Vertex(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return this.x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return this.y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return this.z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public List<Vertex> getVertices() {
        return this.vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Face> getFaces() {
        return this.faces;
    }

    public void setFaces(List<Face> faces) {
        this.faces = faces;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Vertex vertex = (Vertex)o;
            return Objects.equals(this.getX(), vertex.getX()) && Objects.equals(this.getY(), vertex.getY()) && Objects.equals(this.getZ(), vertex.getZ()) && Objects.equals(this.getVertices(), vertex.getVertices()) && Objects.equals(this.getFaces(), vertex.getFaces());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getX(), this.getY(), this.getZ(), this.getVertices(), this.getFaces()});
    }

    public String toString() {
        return "Vertex{x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", vertices=" + this.vertices + ", faces=" + this.faces + "}";
    }
}
