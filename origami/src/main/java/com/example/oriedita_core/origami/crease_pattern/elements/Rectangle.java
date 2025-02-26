package com.example.oriedita_core.origami.crease_pattern.elements;

public class Rectangle extends Polygon {
    private Point p1 = new Point();
    private Point p2 = new Point();
    private Point p3 = new Point();
    private Point p4 = new Point();

    public Rectangle() {
        super();

        add(p1);
        add(p2);
        add(p3);
        add(p4);
    }

    public Rectangle(Point p1, Point p2, Point p3, Point p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3  =p3;
        this.p4 = p4;

        add(this.p1);
        add(this.p2);
        add(this.p3);
        add(this.p4);
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public Point getP3() {
        return p3;
    }

    public Point getP4() {
        return p4;
    }
}

