package sample;

import com.opencsv.CSVReader;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Roc {

    private List<Point> points;
    private int positive_examples_number;
    private int negative_examples_number;

    public Roc(final double[] score, final boolean[] true_alert) {
        points = new ArrayList<>();
        if (score.length != true_alert.length) {
            throw new IllegalStateException(
                    "Score array and true alert array must be the same size");
        }
        for (int i = 0; i < score.length; i++) {
            Point point = new Point(score[i], true_alert[i]);
            points.add(point);
        }
        Collections.sort(points);
        positive_examples_number = Utils.countPositiveExamples(this.points);
        negative_examples_number = Utils.countNegativeExamples(this.points);
    }

    public Roc(final String file_path) {
        points = new ArrayList<>();
        CSVReader reader;
        try {
            reader = new CSVReader(new InputStreamReader(
                    new FileInputStream(file_path), StandardCharsets.UTF_8));
            String[] next_record = null;
            while (true) {
                try {
                    next_record = reader.readNext();
                    if (!(next_record != null)) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Point point;
                if (next_record[1].equals("0")
                        || next_record[1].equals("0.0")) {
                    point = new Point(Double.parseDouble(next_record[0]),
                            false);
                } else if (next_record[1].equals("1")
                        || next_record[1].equals("1.0")) {
                    point = new Point(Double.parseDouble(next_record[0]),
                            true);
                } else {
                    throw new IllegalArgumentException(
                            "Cannot determine if example is pos or neg"
                                    + "Must be 1, 1.0, 0, 0.0");
                }

                points.add(point);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Collections.sort(points);
        positive_examples_number = Utils.countPositiveExamples(this.points);
        negative_examples_number = Utils.countNegativeExamples(this.points);
    }


    public final List<CurveCoordinates> computeRocPoints() {
        int true_positive = 0;
        int false_positive = 0;
        List<CurveCoordinates> roc_points = new ArrayList<>();
        double previous_score = Double.NEGATIVE_INFINITY;
        for (Point point : this.points) {
            if (point.getScore() != previous_score) {
                double true_detection = (double) true_positive
                        / positive_examples_number;
                double false_alarm = (double) false_positive
                        / negative_examples_number;
                roc_points.add(
                        new CurveCoordinates(false_alarm, true_detection));
                previous_score = point.getScore();
            }
            if (point.isTrueAlert()) {
                true_positive++;
            } else {
                false_positive++;
            }
        }
        double true_detection = true_positive
                / (double) positive_examples_number;
        double false_alarm = false_positive / (double) negative_examples_number;
        roc_points.add(new CurveCoordinates(true_detection, false_alarm));
        return roc_points;
    }

    public final List<CurveCoordinates> computeRocPointsAndGenerateCurve() {
        List<CurveCoordinates> roc_coordinates = this.computeRocPoints();
        double[] true_detection = new double[roc_coordinates.size()];
        double[] false_alarm = new double[roc_coordinates.size()];
        for (int i = 0; i < roc_coordinates.size(); i++) {
            true_detection[i] = roc_coordinates.get(i).getYAxis();
            false_alarm[i] = roc_coordinates.get(i).getXAxis();
        }
        XYChart chart = QuickChart.getChart("Roc curve",
                "False Alarm",
                "True Detection",
                null,
                false_alarm,
                true_detection);
        return roc_coordinates;
    }

    public final double computeAUC() {
        int false_positive = 0;
        int true_positive = 0;
        int previous_false_positive = 0;
        int previous_true_positive = 0;
        double area = 0;
        double previous_score = Double.NEGATIVE_INFINITY;
        for (Point point : this.points) {
            if (point.getScore() != previous_score) {
                area = area + this.trapezoidArea(
                        false_positive,
                        previous_false_positive,
                        true_positive,
                        previous_true_positive);
                previous_score = point.getScore();
                previous_false_positive = false_positive;
                previous_true_positive = true_positive;
            }
            if (point.isTrueAlert()) {
                true_positive++;
            } else {
                false_positive++;
            }
        }
        area = area + this.trapezoidArea(
                false_positive,
                previous_false_positive,
                true_positive,
                previous_true_positive);
        return (area / (positive_examples_number * negative_examples_number));
    }

    private double trapezoidArea(final double x1,
                                 final double x2,
                                 final double y1,
                                 final double y2) {
        double base = Math.abs(x1 - x2);
        double height_average = (y1 + y2) / 2;
        return (base * height_average);
    }

    public final List<CurveCoordinates> computeRocPointsAndGenerateCurveSaveImage(
            final String filename) {
        List<CurveCoordinates> roc_coordinates = this.computeRocPoints();
        double[] true_detection = new double[roc_coordinates.size()];
        double[] false_alarm = new double[roc_coordinates.size()];
        for (int i = 0; i < roc_coordinates.size(); i++) {
            true_detection[i] = roc_coordinates.get(i).getYAxis();
            false_alarm[i] = roc_coordinates.get(i).getXAxis();
        }
        XYChart chart = QuickChart.getChart("Roc curve",
                "False Alarm",
                "True Detection",
                null,
                false_alarm,
                true_detection);
        try {
            BitmapEncoder.saveBitmapWithDPI(chart,
                    filename,
                    BitmapEncoder.BitmapFormat.PNG,
                    300);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return roc_coordinates;
    }


}
