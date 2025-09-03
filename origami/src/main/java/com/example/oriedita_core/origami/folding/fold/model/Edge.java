package com.example.oriedita_core.origami.folding.fold.model;

import java.util.Objects;

public class Edge {
    private Vertex start;
    private Vertex end;
    private FoldEdgeAssignment assignment;
    private Double foldAngle;
    private Double length;

    public Edge() {
    }

    public Edge(FoldEdgeAssignment assignment, Vertex start, Vertex end) {
        this.start = start;
        this.end = end;
        this.assignment = assignment;
    }

    public Vertex getStart() {
        return this.start;
    }

    public void setStart(Vertex start) {
        this.start = start;
    }

    public Vertex getEnd() {
        return this.end;
    }

    public void setEnd(Vertex end) {
        this.end = end;
    }

    public FoldEdgeAssignment getAssignment() {
        return this.assignment;
    }

    public void setAssignment(FoldEdgeAssignment assignment) {
        this.assignment = assignment;
    }

    public Double getFoldAngle() {
        return this.foldAngle;
    }

    public void setFoldAngle(Double foldAngle) {
        this.foldAngle = foldAngle;
    }

    public Double getLength() {
        return this.length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Edge edge = (Edge)o;
            return Objects.equals(this.getStart(), edge.getStart()) && Objects.equals(this.getEnd(), edge.getEnd()) && this.getAssignment() == edge.getAssignment() && Objects.equals(this.getFoldAngle(), edge.getFoldAngle()) && Objects.equals(this.getLength(), edge.getLength());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getStart(), this.getEnd(), this.getAssignment(), this.getFoldAngle(), this.getLength()});
    }

    public String toString() {
        return "Edge{start=" + this.start + ", end=" + this.end + ", assignment=" + this.assignment + ", foldAngle=" + this.foldAngle + ", length=" + this.length + "}";
    }
}
