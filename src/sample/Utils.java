package sample;

import java.util.List;

public final class Utils {

    private Utils() {
    }
    public static int countPositiveExamples(final List<Point> points) {
        int positive_examples = 0;
        for (Point point : points) {
            if (point.isTrueAlert()) {
                positive_examples++;
            }
        }
        return positive_examples;
    }

    public static int countNegativeExamples(final List<Point> points) {
        int negative_examples = 0;
        for (Point point : points) {
            if (!point.isTrueAlert()) {
                negative_examples++;
            }
        }
        return negative_examples;
    }

}

