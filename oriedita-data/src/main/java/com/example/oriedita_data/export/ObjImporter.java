package com.example.oriedita_data.export;

import jakarta.enterprise.context.ApplicationScoped;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import oriedita.editor.export.api.FileImporter;
import origami.crease_pattern.elements.Line;
import origami.crease_pattern.elements.LineColor;
import origami.crease_pattern.elements.LineSegment;
import origami.crease_pattern.elements.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

@ApplicationScoped
public class ObjImporter implements FileImporter {
    @Override
    public Save doImport(File file) throws IOException {
        int jtok;

        Save save = SaveProvider.createInstance();

        ArrayList<Point> tL = new ArrayList<>();

        tL.add(new Point());

        int pointMax = 0;

        ArrayList<Line> lineList = new ArrayList<>();
        lineList.add(new Line());

        int lineMax = 0;

        ArrayList<Integer> itempL = new ArrayList<>();
        itempL.add(0);

        int ia;
        int ib;
        LineColor ic;
        // int id;
        double d1, d2; // d3, d4;

        double xmax = -10000.0;
        double xmin = 10000.0;
        double ymax = -10000.0;
        double ymin = 10000.0;

        String str;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String fileLine;

            while ((fileLine = reader.readLine()) != null) {
                StringTokenizer tk = new StringTokenizer(fileLine, " ");
                jtok = tk.countTokens();

                str = tk.nextToken();

                if (str.equals("v")) {
                    d1 = Double.parseDouble(tk.nextToken());
                    d2 = Double.parseDouble(tk.nextToken());
                    tk.nextToken(); // d3 = Double.parseDouble(tk.nextToken());

                    if (d1 > xmax) {
                        xmax = d1;
                    }
                    if (d1 < xmin) {
                        xmin = d1;
                    }
                    if (d2 > ymax) {
                        ymax = d2;
                    }
                    if (d2 < ymin) {
                        ymin = d2;
                    }

                    pointMax = pointMax + 1;
                    tL.add(new Point(d1, d2));
                }
                if (str.equals("f")) {
                    itempL.clear();
                    itempL.add(0);
                    for (int i = 1; i < jtok; i++) {
                        int ite = Integer.parseInt(tk.nextToken());
                        itempL.add(ite);
                    }
                    itempL.set(0, itempL.get(jtok - 1));
                    for (int i = 0; i < jtok - 1; i++) {
                        int iflg = 0;
                        Integer I_itempL = itempL.get(i + 1);
                        Integer Im1_itempL = itempL.get(i);
                        for (Line line : lineList) {
                            if ((line.getBegin() == Im1_itempL) && (line.getEnd() == I_itempL)) {
                                iflg = iflg + 1;
                            }
                            if ((line.getBegin() == I_itempL) && (line.getEnd() == Im1_itempL)) {
                                iflg = iflg + 1;
                            }
                        }
                        if (iflg == 0) {
                            lineMax = lineMax + 1;
                            lineList.add(new Line(Im1_itempL, I_itempL, LineColor.BLACK_0));
                        }
                    }
                }

                if (str.equals("#e")) {
                    ia = Integer.parseInt(tk.nextToken());
                    ib = Integer.parseInt(tk.nextToken());
                    ic = LineColor.from(tk.nextToken());
                    tk.nextToken(); // id = Integer.parseInt(tk.nextToken());
                    for (Line line : lineList) {
                        if ((line.getBegin() == ia) && (line.getEnd() == ib)) {
                            line.setColor(ic);
                        }
                        if ((line.getBegin() == ib) && (line.getEnd() == ia)) {
                            line.setColor(ic);
                        }
                    }
                }
            }
        }

        for (Line line : lineList) {
            LineColor icol = LineColor.fromNumber(line.getColor().getNumber() - 1);
            line.setColor(icol);
            if (line.getColor() == LineColor.RED_1) {
                icol = LineColor.BLUE_2;
            }
            if (line.getColor() == LineColor.BLUE_2) {
                icol = LineColor.RED_1;
            }

            if (icol != LineColor.BLACK_0) {
                line.setColor(icol);
            }

            LineSegment s = new LineSegment(tL.get(line.getBegin()), tL.get(line.getEnd()), line.getColor());

            save.addLineSegment(s.clone());
        }

        return save;
    }

    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".obj");
    }
}
