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
    
    //#########################################
        String title = "Chart title"; //chart title
    
    //data configuration
    
        File inputFile = new File("exampledata.txt");
    
        boolean secondFile = false; //if you have two files and want to visualize both
        File secondInputFile = new File(""); //second file you want to visualize
        int shiftValue = 0; //value how far the second iteration values should be shifted
    
        File outputFile = new File("output.png");
        
    //chart configuration
        int lowerBoundary = 0;
        int higherBoundary = 60000;
        int ticks = 1000;
    
    //visualize settings
        boolean visLoss = true;
        boolean visBBoxLoss = true;
        boolean visClsLoss = true;
        boolean visAccuracy = true;
        boolean visAccuracyRegression = true;
        int accuracyRegressionOrder = 1;
    //#########################################

        
    StackPane root = new StackPane();
    
    public void parse(BufferedReader reader, List<String[]> loss, List<String[]> accuracy, List<String[]> lsBbox, List<String[]> lsCls) throws IOException {
        String zeile;
        while ((zeile = reader.readLine()) != null) {
            if (zeile.contains("json_stats:")) {
                
                zeile = zeile.substring(12, zeile.length());
                JSONObject entry = new JSONObject(zeile);    
                String[] obj1 = new String[]{"" + entry.get("iter"), "" + entry.get("loss")},
                        obj2 = new String[]{"" + entry.get("iter"), "" + entry.get("accuracy_cls")},
                        obj3 = new String[]{"" + entry.get("iter"), "" + entry.get("loss_bbox")},
                        obj4 = new String[]{"" + entry.get("iter"), "" + entry.get("loss_cls")};
                loss.add(obj1);
                accuracy.add(obj2);
                lsBbox.add(obj3);
                lsCls.add(obj4);
            }
        }
    }
    
    public void parseShifted(BufferedReader reader, List<String[]> loss, List<String[]> accuracy, List<String[]> lsBbox, List<String[]> lsCls, int shift) throws IOException {
        String zeile;
        while ((zeile = reader.readLine()) != null) {
            if (zeile.contains("json_stats:")) {
                
                zeile = zeile.substring(12, zeile.length());
                JSONObject entry = new JSONObject(zeile);    
                String[] obj1 = new String[]{"" + (Integer.parseInt("" + entry.get("iter")) + shift) + "", "" + entry.get("loss")},
                        obj2 = new String[]{"" + (Integer.parseInt("" + entry.get("iter")) + shift) + "", "" + entry.get("accuracy_cls")},
                        obj3 = new String[]{"" + (Integer.parseInt("" + entry.get("iter")) + shift) + "", "" + entry.get("loss_bbox")},
                        obj4 = new String[]{"" + (Integer.parseInt("" + entry.get("iter")) + shift) + "", "" + entry.get("loss_cls")};
                loss.add(obj1);
                accuracy.add(obj2);
                lsBbox.add(obj3);
                lsCls.add(obj4);
            }
        }
    }
    
    /**
     * returns a chart with series as configured in the top of the script
     * @param lst lists from the parse or parseShifted function
     * @return chart with training values
     */
    public LineChart<Number,Number> createDiagram(List<String[]>... lst) {
        //configuring axis
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLabel("Iteration");
        xAxis.setLowerBound(lowerBoundary);
        xAxis.setUpperBound(higherBoundary);
        xAxis.setTickUnit(ticks);
        yAxis.setLabel("Value");
        
        //setup chart
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setTitle(title);
        lineChart.setAnimated(true);
        lineChart.setCreateSymbols(false);
        lineChart.getStyleClass().add("chart-series-line");
        
        //add data
        if (visLoss) {
            XYChart.Series series = new XYChart.Series();
            series.setName("Loss");
            for (int i = 0; i < lst[0].size(); i++) {
                if (Float.parseFloat(lst[0].get(i)[1]) < 4) {
                    series.getData().add(new XYChart.Data(Integer.parseInt(lst[0].get(i)[0]), Float.parseFloat(lst[0].get(i)[1])));
                }
            }
            lineChart.getData().addAll(series);
        }
                
        if (visBBoxLoss) {
            XYChart.Series lsBBox = new XYChart.Series();
            lsBBox.setName("loss bbox");
            for (int i = 0; i < lst[2].size(); i++) {
                if (Float.parseFloat(lst[2].get(i)[1]) < 4) {
                    lsBBox.getData().add(new XYChart.Data(Integer.parseInt(lst[2].get(i)[0]), Float.parseFloat(lst[2].get(i)[1])));
                }
            }
            lineChart.getData().addAll(lsBBox);
        }
        
        if (visClsLoss) {
            XYChart.Series lsCls = new XYChart.Series();
            lsCls.setName("loss cls");
            for (int i = 0; i < lst[3].size(); i++) {
                if (Float.parseFloat(lst[3].get(i)[1]) < 4) {
                    lsCls.getData().add(new XYChart.Data(Integer.parseInt(lst[3].get(i)[0]), Float.parseFloat(lst[3].get(i)[1])));
                }
            }
            lineChart.getData().addAll(lsCls);
        }
        
        if (visAccuracy) {
            XYChart.Series accuracy = new XYChart.Series();
            accuracy.setName("Accuracy");
            for (int i = 0; i < lst[1].size(); i++) {
                if (Float.parseFloat(lst[1].get(i)[1]) < 4) {
                    accuracy.getData().add(new XYChart.Data(Integer.parseInt(lst[1].get(i)[0]), Float.parseFloat(lst[1].get(i)[1])));
                }
            }
            lineChart.getData().addAll(accuracy);
        }
                
        if (visAccuracyRegression) {
            XYChart.Series accuracyRegression = getRegression(lst[1], 1);
            accuracyRegression.setName("accuracy regression");
            lineChart.getData().addAll(accuracyRegression);    
        }
        
        return lineChart;
    }
    
    /**
     * returns regression series from a list with a specified order
     * @param lst list with iterations and values
     * @param order order of the regression function
     * @return series of the regression
     */
    public XYChart.Series getRegression(List<String[]> lst, int order) {
        double[] xs = new double[lst.size()];
        double[] ys = new double[lst.size()];
        for (int i = 0; i < lst.size(); i++) {
            xs[i] = Double.parseDouble(lst.get(i)[0]);
            ys[i] = Double.parseDouble(lst.get(i)[1]);
        }
        double[] coefficients = polyfit(xs, ys, order);
        XYChart.Series regSeries = new XYChart.Series();
        
        for (double x = lowerBoundary; x <= higherBoundary; x += ticks) {
            double y = polynomial(x, coefficients);
            regSeries.getData().add(new XYChart.Data<>(x,y));
        }
        regSeries.setName("Regression");
        return regSeries;
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException, InterruptedException {
        ArrayList<String[]> loss = new ArrayList<>(), accuracy = new ArrayList<>(), lossCls = new ArrayList<>(), lossBBox = new ArrayList<>();
        
        //read and parse first file
        FileReader eingabeStrom = new FileReader(inputFile);
        BufferedReader eingabe = new BufferedReader(eingabeStrom);
        parse(eingabe, loss, accuracy, lossBBox, lossCls);
        eingabe.close();
        
        //read and parse optional second file
        if (secondFile) {
            inputFile = secondInputFile;
            eingabeStrom = new FileReader(inputFile);
            eingabe = new BufferedReader(eingabeStrom);
            parseShifted(eingabe, loss, accuracy, lossBBox, lossCls, shiftValue);
            eingabe.close();
        }
        
        //Button for saving the chart as a png
        Button btn = new Button("save Image");
        btn.setOnAction(evt -> {
            WritableImage image = root.getChildren().get(0).snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
            } catch (IOException ex) {
                Logger.getLogger(VisualizeTraining.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        root.setAlignment(Pos.TOP_LEFT);
        
        LineChart<Number, Number> chart = createDiagram(loss, accuracy, lossBBox, lossCls);
        root.getChildren().addAll(chart, btn);
        Scene scene = new Scene(root, 1400, 800);
        scene.getStylesheets().add("/en/frind/visualizedetectron/style.css");
        
        primaryStage.setTitle("Detectron - training visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
