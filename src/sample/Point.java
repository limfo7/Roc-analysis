package sample;

import java.util.Objects;

public class Point implements Comparable<Point> {

    private double score;
    private boolean true_alert;

    public Point(final double score, final boolean true_alert) {
        this.setScore(score);
        this.setTrueAlert(true_alert);
    }

    @Override
    public final int compareTo(final Point point) {
        if (Double.compare(this.score, point.score) > 0) {
            return -1;
        } else if (Double.compare(this.score, point.score) < 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Point)) {
            return false;
        }
        Point p = (Point) o;
        return score == p.score;
    }

    public final int hashCode() {
        return Objects.hash(score, true_alert);
    }

    public final void setScore(final double score) {
        if (score < 0 || score > 1) {
            throw new IllegalArgumentException("Score must be between 0 and 1");
        }
        this.score = score;
    }

    public final void setTrueAlert(final boolean true_alert) {
        this.true_alert = true_alert;
    }

    public final double getScore() {
        return score;
    }

    public final boolean isTrueAlert() {
        return true_alert;
    }
}

