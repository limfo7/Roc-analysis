package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class Controller extends Window implements Initializable {
    public Label aucLabel;
    public Roc roc;
    public Pane pane;
    public TableView<DataStructureTable> aucTable;
    public TableColumn<DataStructureTable, String> modelQuality;
    public TableColumn<DataStructureTable, String> aucInterval;

    public TableView<CurveCoordinates> tableValueChart;
    public TableColumn<CurveCoordinates,String> x_axis;
    public TableColumn<CurveCoordinates,String> y_axis;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aucInterval.setCellValueFactory(new PropertyValueFactory<>("aucInterval"));
        modelQuality.setCellValueFactory(new PropertyValueFactory<>("modelQuality"));

        x_axis.setCellValueFactory(new PropertyValueFactory<>("XAxis"));
        y_axis.setCellValueFactory(new PropertyValueFactory<>("YAxis"));
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

            roc = new Roc(file.getName());
            drawChart(roc.computeRocPointsAndGenerateCurve());
            aucLabel.setText("AUC = " + roc.computeAUC());

            double auc = roc.computeAUC();

            aucTable.setRowFactory(row -> new TableRow<DataStructureTable>() {
                @Override
                public void updateItem(DataStructureTable item, boolean empty) {
                    super.updateItem(item, empty);
                    //item.getAucInterval().equals("0.8-0.7")
                    if (item == null || empty) {
                        setStyle("");
                    } else {
                        //Now 'item' has all the info of the Person in this row
                        if (auc < item.getAucIntervalMax() && auc > item.getAucIntervalMin()) {
                            //We apply now the changes in all the cells of the row
                            for (int i = 0; i < getChildren().size(); i++) {
                                ((Labeled) getChildren().get(i)).setTextFill(Color.RED);
                                (getChildren().get(i)).setStyle("-fx-background-color: yellow");
                            }
                        } else {
                            if (getTableView().getSelectionModel().getSelectedItems().contains(item)) {
                                for (int i = 0; i < getChildren().size(); i++) {
                                    ((Labeled) getChildren().get(i)).setTextFill(Color.WHITE);
                                    ;
                                }
                            } else {
                                for (int i = 0; i < getChildren().size(); i++) {
                                    ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                                    ;
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void generalTable() {
        DataStructureTable dataStructureTable1 = new DataStructureTable(0.9, 1.0, "Отличное");
        DataStructureTable dataStructureTable2 = new DataStructureTable(0.8, 0.9, "Очень хорошее");
        DataStructureTable dataStructureTable3 = new DataStructureTable(0.7, 0.8, "Хорошее");
        DataStructureTable dataStructureTable4 = new DataStructureTable(0.6, 0.7, "Среднее");
        DataStructureTable dataStructureTable5 = new DataStructureTable(0.1, 0.6, "Неудовлетворительное");

        ArrayList<DataStructureTable> dataStructureTableArrayList = new ArrayList<>();
        dataStructureTableArrayList.add(dataStructureTable1);
        dataStructureTableArrayList.add(dataStructureTable2);
        dataStructureTableArrayList.add(dataStructureTable3);
        dataStructureTableArrayList.add(dataStructureTable4);
        dataStructureTableArrayList.add(dataStructureTable5);

        ObservableList aucList = FXCollections.observableArrayList(dataStructureTableArrayList);


        aucTable.setItems(aucList);
    }

    public void tableChart(List<CurveCoordinates> points,LineChart<Number, Number> chart,XYChart.Series series,XYChart.Series CriticalLane){
        ObservableList<CurveCoordinates> pointsList = FXCollections.observableArrayList(points);
        tableValueChart.setItems(pointsList);

        tableValueChart.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CurveCoordinates>() {
            @Override
            public void changed(ObservableValue<? extends CurveCoordinates> observable, CurveCoordinates oldValue, CurveCoordinates newValue) {

                if(newValue!=null){
                    XYChart.Series focusTable = new XYChart.Series<>();
                    focusTable.getData().add(new XYChart.Data<>(newValue.getXAxis(), newValue.getYAxis()));

                    chart.getData().addAll(focusTable);
                    pane.getChildren().setAll(chart);
                }
                if(oldValue!=null){
                    chart.getData().clear();
                    XYChart.Series CriticalLane = new XYChart.Series<>();

                    CriticalLane.getData().add(new XYChart.Data<>(0, 0));
                    CriticalLane.getData().add(new XYChart.Data<>(1, 1));

                    final NumberAxis xAxis = new NumberAxis();
                    xAxis.setLabel("False Alarm");
                    final NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("True Detection");

                    final LineChart<Number, Number> chart =
                            new LineChart<Number, Number>(xAxis, yAxis);
                    XYChart.Series series = new XYChart.Series();
                    series.setName("Roc-curve");
                    CriticalLane.setName("Critical Lane");
                    for (CurveCoordinates coordinates : points) {
                        series.getData().add(new XYChart.Data(coordinates.getXAxis(), coordinates.getYAxis()));
                    }
                    XYChart.Series focusTable = new XYChart.Series<>();
                    focusTable.getData().add(new XYChart.Data<>(newValue.getXAxis(), newValue.getYAxis()));

                    chart.getData().addAll(series, CriticalLane,focusTable);
                    pane.getChildren().setAll(chart);
                }
            }
        });
    }

    private void drawChart(List<CurveCoordinates> points) {
        XYChart.Series CriticalLane = new XYChart.Series<>();

        CriticalLane.getData().add(new XYChart.Data<>(0, 0));
        CriticalLane.getData().add(new XYChart.Data<>(1, 1));

        final NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("False Alarm");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("True Detection");

        final LineChart<Number, Number> chart =
                new LineChart<Number, Number>(xAxis, yAxis);
        XYChart.Series series = new XYChart.Series();
        series.setName("Roc-curve");
        CriticalLane.setName("Critical Lane");
        for (CurveCoordinates coordinates : points) {
            series.getData().add(new XYChart.Data(coordinates.getXAxis(), coordinates.getYAxis()));
        }
        chart.getData().addAll(series, CriticalLane);
        pane.getChildren().setAll(chart);

        generalTable();
        tableChart(points,chart,series, CriticalLane);

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

        roc = new Roc(score, true_alert);
        drawChart(roc.computeRocPointsAndGenerateCurve());

        aucLabel.setText("AUC = " + roc.computeAUC());

        double auc = roc.computeAUC();

        aucTable.setRowFactory(row -> new TableRow<DataStructureTable>() {
            @Override
            public void updateItem(DataStructureTable item, boolean empty) {
                super.updateItem(item, empty);
                //item.getAucInterval().equals("0.8-0.7")
                if (item == null || empty) {
                    setStyle("");
                } else {
                    //Now 'item' has all the info of the Person in this row
                    if (auc < item.getAucIntervalMax() && auc > item.getAucIntervalMin()) {
                        //We apply now the changes in all the cells of the row
                        for (int i = 0; i < getChildren().size(); i++) {
                            ((Labeled) getChildren().get(i)).setTextFill(Color.RED);
                            (getChildren().get(i)).setStyle("-fx-background-color: yellow");
                        }
                    } else {
                        if (getTableView().getSelectionModel().getSelectedItems().contains(item)) {
                            for (int i = 0; i < getChildren().size(); i++) {
                                ((Labeled) getChildren().get(i)).setTextFill(Color.WHITE);
                                ;
                            }
                        } else {
                            for (int i = 0; i < getChildren().size(); i++) {
                                ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                                ;
                            }
                        }
                    }
                }
            }
        });

    }


    public void saveImage(ActionEvent actionEvent) {

        roc.computeRocPointsAndGenerateCurveSaveImage("Data.png");

    }
}
