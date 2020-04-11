package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller extends Window implements Initializable {
    public Label aucLabel;
    //public LineChart chart;
    public Pane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void fromFile(ActionEvent actionEvent) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор файла");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Csv files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(this);
        if (file != null) {
            System.out.println("zzzz:" + file);
            Scanner scanner = new Scanner(file);

            Roc roc = new Roc(file.getName());
            drawChart(roc.computeRocPointsAndGenerateCurve());
            aucLabel.setText("AUC = " + roc.computeAUC());
        }
    }

    private void drawChart(List<CurveCoordinates> points) {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("False Alarm");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("True Detection");

        final LineChart<Number,Number> chart =
                new LineChart<Number,Number>(xAxis,yAxis);
        XYChart.Series series = new XYChart.Series();
        series.setName("Roc-curve");
        for (CurveCoordinates coordinates : points) {
            series.getData().add(new XYChart.Data(coordinates.getXAxis(), coordinates.getYAxis()));
        }
        chart.getData().add(series);

        pane.getChildren().setAll(chart);
    }

    public void generateRandom(ActionEvent actionEvent) {
        double[] score = new double[5000];
        boolean[] true_alert = new boolean[5000];

        Random random = new Random();

        for (int i = 0; i < score.length; i++) {
            double dRandom = random.nextDouble();
            score[i] = dRandom;

            boolean bRandom = random.nextBoolean();
            true_alert[i] = bRandom;
        }

        Roc roc = new Roc(score, true_alert);
        drawChart(roc.computeRocPointsAndGenerateCurve());

        aucLabel.setText("AUC = " + roc.computeAUC());
    }


}
