package sample;

public class CurveCoordinates {

    private double x_axis;
    private double y_axis;

    public CurveCoordinates(final double x_axis,
                            final double y_axis) {
        this.setXAxis(x_axis);
        this.setYAxis(y_axis);
    }

    public final double getXAxis() {
        return x_axis;
    }

    public final void setXAxis(final double x_axis) {
        if (x_axis < 0 || x_axis > 1) {
            throw new IllegalArgumentException(""
                    + "Value must be between 0 and 1");
        }
        this.x_axis = x_axis;
    }

    public final double getYAxis() {
        return y_axis;
    }

    public final void setYAxis(final double y_axis) {
        if (y_axis < 0 || y_axis > 1) {
            throw new IllegalArgumentException(
                    "Value must be between 0 and 1");
        }
        this.y_axis = y_axis;
    }
}

