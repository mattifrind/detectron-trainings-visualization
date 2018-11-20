/*
 * Copyright (C) 2018 Matti J. Frind
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package en.frind.visualizedetectron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.json.JSONObject;

import static thorwin.math.Math.polyfit;
import static thorwin.math.Math.polynomial;

/**
 *
 * @author Matti J. Frind
 */
public class VisualizeTraining extends Application {
        
    private final StackPane rootMain = new StackPane();
    private final StackPane rootSecond = new StackPane();
    
    /**
     * Parse data from File.
     * @param reader input file
     * @param loss output data list
     * @param accuracy output data list
     * @param lsBbox output data list
     * @param lsCls output data list
     * @param lr output data list
     * @throws IOException
     */
    private void parse(BufferedReader reader, List<String[]> loss, List<String[]> accuracy, List<String[]> lsBbox, List<String[]> lsCls, List<String[]> lr) throws IOException {
        String zeile;
        while ((zeile = reader.readLine()) != null) {
            if (zeile.contains("json_stats:")) {
                
                zeile = zeile.substring(12, zeile.length());
                JSONObject entry = new JSONObject(zeile);    
                String[] obj1 = new String[]{"" + entry.get("iter"), "" + entry.get("loss")},
                        obj2 = new String[]{"" + entry.get("iter"), "" + entry.get("accuracy_cls")},
                        obj3 = new String[]{"" + entry.get("iter"), "" + entry.get("loss_bbox")},
                        obj4 = new String[]{"" + entry.get("iter"), "" + entry.get("loss_cls")},
                        obj5 = new String[]{"" + entry.get("iter"), "" + entry.get("lr")};
                loss.add(obj1);
                accuracy.add(obj2);
                lsBbox.add(obj3);
                lsCls.add(obj4);
                lr.add(obj5);
            }
        }
    }
    
    /**
     * Creates a chart with series as configured.
     * @param lst lists from the parse function
     * @return chart with training values
     */
    private LineChart<Number,Number> createDiagram(List<String[]>... lst) {
        //configuring axis
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLabel("Iteration");
        xAxis.setLowerBound(Settings.getLowerBoundary());
        xAxis.setUpperBound(Settings.getHigherBoundary());
        xAxis.setTickUnit(Settings.getTicks());
        yAxis.setLabel("Value");
        
        //setup chart
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setTitle(Settings.getTitle());
        lineChart.setAnimated(true);
        lineChart.setCreateSymbols(false);
        lineChart.getStyleClass().add("chart-series-line");
        
        //add data
        if (Settings.isVisLoss()) {
            XYChart.Series series = new XYChart.Series();
            series.setName("Loss");
            for (int i = 0; i < lst[0].size(); i++) {
                if (Float.parseFloat(lst[0].get(i)[1]) < 4) {
                    series.getData().add(new XYChart.Data(Integer.parseInt(lst[0].get(i)[0]), Float.parseFloat(lst[0].get(i)[1])));
                }
            }
            lineChart.getData().addAll(series);
        }
                
        if (Settings.isVisBBoxLoss()) {
            XYChart.Series lsBBox = new XYChart.Series();
            lsBBox.setName("loss bbox");
            for (int i = 0; i < lst[2].size(); i++) {
                if (Float.parseFloat(lst[2].get(i)[1]) < 4) {
                    lsBBox.getData().add(new XYChart.Data(Integer.parseInt(lst[2].get(i)[0]), Float.parseFloat(lst[2].get(i)[1])));
                }
            }
            lineChart.getData().addAll(lsBBox);
        }
        
        if (Settings.isVisClsLoss()) {
            XYChart.Series lsCls = new XYChart.Series();
            lsCls.setName("loss cls");
            for (int i = 0; i < lst[3].size(); i++) {
                if (Float.parseFloat(lst[3].get(i)[1]) < 4) {
                    lsCls.getData().add(new XYChart.Data(Integer.parseInt(lst[3].get(i)[0]), Float.parseFloat(lst[3].get(i)[1])));
                }
            }
            lineChart.getData().addAll(lsCls);
        }
        
        if (Settings.isVisAccuracy()) {
            XYChart.Series accuracy = new XYChart.Series();
            accuracy.setName("Accuracy");
            for (int i = 0; i < lst[1].size(); i++) {
                if (Float.parseFloat(lst[1].get(i)[1]) < 4) {
                    accuracy.getData().add(new XYChart.Data(Integer.parseInt(lst[1].get(i)[0]), Float.parseFloat(lst[1].get(i)[1])));
                }
            }
            lineChart.getData().addAll(accuracy);
        }
                
        if (Settings.isVisAccuracyRegression()) {
            XYChart.Series accuracyRegression = getRegression(lst[1], 1);
            accuracyRegression.setName("accuracy regression");
            lineChart.getData().addAll(accuracyRegression);    
        }
        
        return lineChart;
    }
    
    /**
     * Creates a chart with the learning rate.
     * @param lr list created by the parse function.
     * @return chart with the learning rate
     */
    private LineChart<Number,Number> createSecondDiagram(List<String[]> lr) {
        //configuring axis
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLabel("Iteration");
        xAxis.setLowerBound(Settings.getLowerBoundary());
        xAxis.setUpperBound(Settings.getHigherBoundary());
        xAxis.setTickUnit(Settings.getTicks());
        yAxis.setLabel("Value");
        
        //setup chart
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setTitle(Settings.getTitle());
        lineChart.setAnimated(true);
        lineChart.setCreateSymbols(false);
        lineChart.getStyleClass().add("chart-series-line");
        
        XYChart.Series series = new XYChart.Series();
        series.setName("learning rate");
        for (int i = 0; i < lr.size(); i++) {
            if (Float.parseFloat(lr.get(i)[1]) < 4) {
                series.getData().add(new XYChart.Data(Integer.parseInt(lr.get(i)[0]), Float.parseFloat(lr.get(i)[1])));
            }
        }
        lineChart.getData().addAll(series);

        return lineChart;
    }
    
    /**
     * returns regression series from a list with a specified order
     * @param lst list with iterations and values
     * @param order order of the regression function
     * @return series of the regression
     */
    private XYChart.Series getRegression(List<String[]> lst, int order) {
        double[] xs = new double[lst.size()];
        double[] ys = new double[lst.size()];
        for (int i = 0; i < lst.size(); i++) {
            xs[i] = Double.parseDouble(lst.get(i)[0]);
            ys[i] = Double.parseDouble(lst.get(i)[1]);
        }
        double[] coefficients = polyfit(xs, ys, order);
        System.out.println(coefficients[1]);
        XYChart.Series regSeries = new XYChart.Series();
        
        for (double x = Settings.getLowerBoundary(); x <= Settings.getHigherBoundary(); x += Settings.getTicks()) {
            double y = polynomial(x, coefficients);
            regSeries.getData().add(new XYChart.Data<>(x,y));
        }
        regSeries.setName("Regression");
        return regSeries;
    }
    
    /**
     * Creates the main chart.
     * @param loss
     * @param accuracy
     * @param lossBBox
     * @param lossCls
     * @param lr
     * @return scene with the main chart
     */
    private Scene createMainChart(ArrayList<String[]> loss, ArrayList<String[]> accuracy, ArrayList<String[]> lossBBox, ArrayList<String[]> lossCls, ArrayList<String[]> lr) {
        //Button for saving the chart as a png
        Button btn = new Button("save Image");
        btn.setOnAction(evt -> {
            WritableImage image = rootMain.getChildren().get(0).snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File("MainChart.png"));
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Your file was written to: " + System.getProperty("user.dir") + "\\MainChart.png");
                alert.showAndWait();
            } catch (IOException ex) {
                Logger.getLogger(VisualizeTraining.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        rootMain.setAlignment(Pos.TOP_LEFT);
        
        LineChart<Number, Number> chart = createDiagram(loss, accuracy, lossBBox, lossCls);
       
        rootMain.getChildren().addAll(chart, btn);
        Scene scene = new Scene(rootMain, 1400, 800);
        scene.getStylesheets().add("/en/frind/visualizedetectron/style.css");
        return scene;
    }
    
    /**
     * Creates a stage with the second chart. the learning rate chart.
     * @param lr
     * @return
     */
    private Scene createSecondStage(ArrayList<String[]> lr) {
        //Button for saving the chart as a png
        Button btn = new Button("save Image");
        btn.setOnAction(evt -> {
            WritableImage image = rootSecond.getChildren().get(0).snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File("TrainingRate.png"));
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Your file was written to: " + System.getProperty("user.dir") + "\\TrainingRate.png");
                alert.showAndWait();
            } catch (IOException ex) {
                Logger.getLogger(VisualizeTraining.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        rootSecond.setAlignment(Pos.TOP_LEFT);
        
        LineChart<Number, Number> chart = createSecondDiagram(lr);
       
        rootSecond.getChildren().addAll(chart, btn);
        Scene scene = new Scene(rootSecond, 1400, 800);
        scene.getStylesheets().add("/en/frind/visualizedetectron/style.css");
        return scene;
    }
   
    
    @Override
    public void start(Stage primaryStage) throws IOException, InterruptedException {
        ArrayList<String[]> loss = new ArrayList<>(), accuracy = new ArrayList<>(), lossCls = new ArrayList<>(), lossBBox = new ArrayList<>(), lr = new ArrayList<>();
        primaryStage = new Stage();
        
        //read and parse first file
        FileReader eingabeStrom = new FileReader(Settings.getInputFile());
        try (BufferedReader eingabe = new BufferedReader(eingabeStrom)) {
            parse(eingabe, loss, accuracy, lossBBox, lossCls, lr);
        }
        
        primaryStage.setScene(createMainChart(loss, accuracy, lossBBox, lossCls, lr));
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Detectron - training visualization - 1");
        if(Settings.visLearningRate) {
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(createSecondStage(lr));
            secondaryStage.setTitle("Detectron - training visualization - 2");
            secondaryStage.show();
            secondaryStage.setX(primaryStage.getX() + 80);
            secondaryStage.setY(primaryStage.getY() + 80);
        }
        
        primaryStage.show();
        primaryStage.requestFocus();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
