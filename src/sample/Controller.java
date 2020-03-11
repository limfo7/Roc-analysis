package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Controller extends Window {
    public Label aucLabel;
    public ImageView imageView;

    public void fromFile(ActionEvent actionEvent) throws FileNotFoundException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор файла");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Csv files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(this);
        if (file != null) {
            System.out.println("zzzz:"+file);
            Scanner scanner = new Scanner(file);

            Roc roc = new Roc(file.getName());
            roc.computeRocPointsAndGenerateCurve("Data.png");

            Image image = new Image("file:Data.png");
            imageView.setImage(image);

            aucLabel.setText("AUC = " + roc.computeAUC());
        }


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
        roc.computeRocPointsAndGenerateCurve("Data.png");

        Image image = new Image("file:Data.png");
        imageView.setImage(image);

        aucLabel.setText("AUC = " + roc.computeAUC());
    }
}
