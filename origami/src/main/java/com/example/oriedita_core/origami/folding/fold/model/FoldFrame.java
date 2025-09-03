package com.example.oriedita_core.origami.folding.fold.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FoldFrame {
    private List<Vertex> vertices = new ArrayList();
    private List<Edge> edges = new ArrayList();
    private List<Face> faces = new ArrayList();
    private List<FaceOrder> faceOrders = new ArrayList();
    private List<EdgeOrder> edgeOrders = new ArrayList();
    private String author;
    private String title;
    private String description;
    private List<String> classes = new ArrayList();
    private List<String> attributes = new ArrayList();
    private String unit;
    private Integer parent;
    private Boolean inherit;

    public FoldFrame() {
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

    public void addEdge(Edge edge) {
        this.edges.add(edge);
        this.vertices.add(edge.getStart());
        this.vertices.add(edge.getEnd());
    }

    public List<Face> getFaces() {
        return this.faces;
    }

    public void setFaces(List<Face> faces) {
        this.faces = faces;
    }

    public List<FaceOrder> getFaceOrders() {
        return this.faceOrders;
    }

    public void setFaceOrders(List<FaceOrder> faceOrders) {
        this.faceOrders = faceOrders;
    }

    public List<EdgeOrder> getEdgeOrders() {
        return this.edgeOrders;
    }

    public void setEdgeOrders(List<EdgeOrder> edgeOrders) {
        this.edgeOrders = edgeOrders;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getClasses() {
        return this.classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public List<String> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getParent() {
        return this.parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Boolean getInherit() {
        return this.inherit;
    }

    public void setInherit(Boolean inherit) {
        this.inherit = inherit;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            FoldFrame frame = (FoldFrame)o;
            return Objects.equals(this.getVertices(), frame.getVertices()) && Objects.equals(this.getEdges(), frame.getEdges()) && Objects.equals(this.getFaces(), frame.getFaces()) && Objects.equals(this.getFaceOrders(), frame.getFaceOrders()) && Objects.equals(this.getEdgeOrders(), frame.getEdgeOrders()) && Objects.equals(this.getAuthor(), frame.getAuthor()) && Objects.equals(this.getTitle(), frame.getTitle()) && Objects.equals(this.getDescription(), frame.getDescription()) && Objects.equals(this.getClasses(), frame.getClasses()) && Objects.equals(this.getAttributes(), frame.getAttributes()) && Objects.equals(this.getUnit(), frame.getUnit()) && Objects.equals(this.getParent(), frame.getParent()) && Objects.equals(this.getInherit(), frame.getInherit());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getVertices(), this.getEdges(), this.getFaces(), this.getFaceOrders(), this.getEdgeOrders(), this.getAuthor(), this.getTitle(), this.getDescription(), this.getClasses(), this.getAttributes(), this.getUnit(), this.getParent(), this.getInherit()});
    }

    public static class EdgeOrder {
        private Edge edge1;
        private Edge edge2;
        private Boolean edge1AboveEdge2;

        public EdgeOrder() {
        }

        public Edge getEdge1() {
            return this.edge1;
        }

        public void setEdge1(Edge edge1) {
            this.edge1 = edge1;
        }

        public Edge getEdge2() {
            return this.edge2;
        }

        public void setEdge2(Edge edge2) {
            this.edge2 = edge2;
        }

        public Boolean getEdge1AboveEdge2() {
            return this.edge1AboveEdge2;
        }

        public void setEdge1AboveEdge2(Boolean edge1AboveEdge2) {
            this.edge1AboveEdge2 = edge1AboveEdge2;
        }
    }

    public static class FaceOrder {
        private Face face1;
        private Face face2;
        private Boolean face1AboveFace2;

        public FaceOrder() {
        }

        public Face getFace1() {
            return this.face1;
        }

        public void setFace1(Face face1) {
            this.face1 = face1;
        }

        public Face getFace2() {
            return this.face2;
        }

        public void setFace2(Face face2) {
            this.face2 = face2;
        }

        public Boolean getFace1AboveFace2() {
            return this.face1AboveFace2;
        }

        public void setFace1AboveFace2(Boolean face1AboveFace2) {
            this.face1AboveFace2 = face1AboveFace2;
        }
    }
}
