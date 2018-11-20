/*
 * Copyright (C) 2018 matti
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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Matti J. Frind
 */
public class OpenDialog extends Application {
    
    GridPane settingsGrid = null;
    
    @Override
    public void start(Stage primaryStage) {
        //title setup
        AnchorPane titlePane = new AnchorPane();
        Label lblTitle = new Label("Detectron Trainings Visualization");
        lblTitle.setFont(new Font("Arial", 35));
        lblTitle.setAlignment(Pos.CENTER);
        lblTitle.setMaxWidth(Double.MAX_VALUE);
        titlePane.getChildren().add(lblTitle);
        AnchorPane.setTopAnchor(lblTitle, 20.0);
        AnchorPane.setLeftAnchor(lblTitle, 0.0);
        AnchorPane.setRightAnchor(lblTitle, 0.0);
        
        Label author = new Label("by Matti J. Frind, 2018");
        author.setAlignment(Pos.CENTER);
        author.setMaxWidth(Double.MAX_VALUE);
        titlePane.getChildren().add(author);
        AnchorPane.setTopAnchor(author, 70.0);
        AnchorPane.setLeftAnchor(author, 0.0);
        AnchorPane.setRightAnchor(author, 0.0);
        
        Label help = new Label("This visualization tool is able to create charts containing the loss, the bounding-box loss, the classification loss, the "
                + "learning rate, the accuracy and a linear regression of the accuracy. As input file you need the training output from training as a text file. "
                + "You can configure everthing underneath.");
        help.setWrapText(true);
        help.setMaxWidth(650);
        help.setFont(new Font(15));
        help.setAlignment(Pos.CENTER);
        titlePane.getChildren().add(help);
        AnchorPane.setTopAnchor(help, 100.0);
        AnchorPane.setLeftAnchor(help, 40.0);
        AnchorPane.setRightAnchor(help, 40.0);
        
        //settings form
        settingsGrid = getForm(primaryStage);
        settingsGrid.getColumnConstraints().add(new ColumnConstraints(180));

        //button setup
        Button btnGenerate = new Button();
        btnGenerate.setText("Generate");
        btnGenerate.setMinWidth(200);
        btnGenerate.disableProperty().bind(fileChoosenProperty().not());
        btnGenerate.setOnAction((ActionEvent event) -> {
            try {
                setSettings();
                new VisualizeTraining().start(primaryStage);
                primaryStage.close();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(OpenDialog.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Hinweis");
                alert.setHeaderText(null);
                alert.setContentText("The file couldn't be found.");
                alert.showAndWait();
            }
        });
        HBox hbBottom = new HBox(btnGenerate);
        hbBottom.setAlignment(Pos.CENTER);
        hbBottom.setPadding(new Insets(10));
        
        //root setup
        
        BorderPane rootBorder = new BorderPane();
        rootBorder.setTop(titlePane);
        rootBorder.setBottom(hbBottom);
        rootBorder.setCenter(settingsGrid);
        rootBorder.setMinWidth(800);
        ScrollPane root = new ScrollPane(rootBorder);
        
        //scene setup
        Scene scene = new Scene(root, 800, 870);
        primaryStage.setTitle("Detectron Trainings Visualization - Chart Configuration");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(850);
        primaryStage.setMaxWidth(850);
        primaryStage.show();
    }
    
    /**
     * Creates a GridPane containing all configuration GUI items.
     * @param primaryStage
     * @return form
     */
    private GridPane getForm(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        //title
        Label lblChartName = new Label("Chart name:");
        grid.add(lblChartName, 0, 1);
        TextField tfChartName = new TextField("Example Chart");
        titleProperty().bind(tfChartName.textProperty());
        grid.add(tfChartName, 1, 1);
        
        //data file
        Label lblData = new Label("Data file:");
        grid.add(lblData, 0, 2);
        grid.add(getFileChooser(primaryStage), 1, 2);
        
        //chartconf-boundary
        Label lblHighBoundTitle = new Label("Higher Boundary:");
        grid.add(lblHighBoundTitle, 0, 3);
        grid.add(getHighBoundSlider(), 1, 3);
        
        //chartconf-ticks
        Label lblTicksTitle = new Label("Ticks:");
        grid.add(lblTicksTitle, 0, 4);
        grid.add(getTicksSlider(), 1, 4);
        
        //chartconf-visLoss
        Label lblVisLossTitle = new Label("Visualize Loss:");
        grid.add(lblVisLossTitle, 0, 5);
        CheckBox lossBox = new CheckBox();
        lossProperty().bind(lossBox.selectedProperty());
        lossBox.setSelected(true);
        grid.add(lossBox, 1, 5);
        
        //chartconf-visBBox
        Label lblBBoxLossTitle = new Label("Visualize BBox-Loss:");
        grid.add(lblBBoxLossTitle, 0, 6);
        CheckBox bboxBox = new CheckBox();
        bboxLossProperty().bind(bboxBox.selectedProperty());
        bboxBox.setSelected(true);
        grid.add(bboxBox, 1, 6);
        
        //chartconf-visCls
        Label lblClsLossTitle = new Label("Visualize Cls-Loss:");
        grid.add(lblClsLossTitle, 0, 7);
        CheckBox clsBox = new CheckBox();
        clsLossProperty().bind(clsBox.selectedProperty());
        clsBox.setSelected(true);
        grid.add(clsBox, 1, 7);
        
        //chartconf-visAcc
        Label lblAccTitle = new Label("Visualize Accuracy:");
        grid.add(lblAccTitle, 0, 8);
        CheckBox accBox = new CheckBox();
        accProperty().bind(accBox.selectedProperty());
        accBox.setSelected(true);
        grid.add(accBox, 1, 8);
        
        //chartconf-visAcc
        Label lblAccRegTitle = new Label("Visualize Accuracy Regression:");
        grid.add(lblAccRegTitle, 0, 9);
        CheckBox accRegBox = new CheckBox();
        AccRegProperty().bind(accRegBox.selectedProperty());
        accRegBox.setSelected(true);
        grid.add(accRegBox, 1, 9);
        
        //chartconf-vis rate
        Label lblLearnRateTitle = new Label("Visualize Learning Rate:");
        grid.add(lblLearnRateTitle, 0, 10);
        CheckBox learnRateBox = new CheckBox();
        learnRateProperty().bind(learnRateBox.selectedProperty());
        learnRateBox.setSelected(true);
        grid.add(learnRateBox, 1, 10);
        
        return grid;
    }
    
    /**
     * Creates a HBox containing a label with the file name and button which opens a file chooser.
     * @param primaryStage
     * @return Hbox with button and label
     */
    private HBox getFileChooser(Stage primaryStage) {
        Label lblFile = new Label("");
        lblFile.setMaxHeight(Double.MAX_VALUE);
        Button btnFile = new Button("Open File");
        btnFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Data File");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                Settings.setInputFile(selectedFile);
                lblFile.setText(selectedFile.getName());
                fileProperty().set(selectedFile.getAbsolutePath());
                fileChoosenProperty().set(true);
            }
        });
        HBox boxFile = new HBox(btnFile, lblFile);
        boxFile.setFillHeight(true);
        boxFile.setSpacing(10);
        return boxFile;
    }
    
    /**
     * Creates the Slider for the selection of the higher bound of the chart.
     * @return VBox with slider, label and textfield
     */
    private VBox getHighBoundSlider() {
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(100000);
        slider.setValue(60000);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(10000);
        slider.setPrefWidth(500);
        Label lblBound = new Label("Higher Boundary: ");
        lblBound.setMaxHeight(Double.MAX_VALUE);
        TextField tfHighBound = new TextField("60000");
        slider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            try {
                tfHighBound.setText("" + Math.round(newValue.intValue()));
            } catch (Exception ex) {}
        });
        tfHighBound.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                slider.setValue(Integer.parseInt(tfHighBound.getText()));
            } catch (Exception ex) {}
        });
        HBox detailedValue = new HBox(lblBound, tfHighBound);
        VBox higherBoundaryBox = new VBox(slider,detailedValue);
        higherBoundaryBox.setAlignment(Pos.CENTER);
        higherBoundaryProperty().bind(slider.valueProperty());
        return higherBoundaryBox;
    }
    
    /**
     * Creates the Slider for the selection of the ticks size of the chart.
     * @return VBox with slider, label and textfield
     */
    private VBox getTicksSlider() {
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(2000);
        slider.setValue(1000);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(500);
        slider.setPrefWidth(500);
        Label lblTicks = new Label("Ticks: ");
        lblTicks.setMaxHeight(Double.MAX_VALUE);
        TextField tfTicks = new TextField("1000");
        slider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            try {
                tfTicks.setText("" + Math.round(newValue.intValue()));
            } catch (Exception ex) {}
        });
        tfTicks.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                slider.setValue(Integer.parseInt(tfTicks.getText()));
            } catch (Exception ex) {}
        });
        HBox detailedValue = new HBox(lblTicks, tfTicks);
        VBox ticksBox = new VBox(slider,detailedValue);
        ticksBox.setAlignment(Pos.CENTER);
        ticksProperty().bind(slider.valueProperty());
        return ticksBox;
    }

    /**
     * Configures the settings in the Settings class according to the JavaFX Properties.
     */
    private void setSettings() {
        Settings.setTitle(titleProperty().get());
        Settings.setInputFile(new File(fileProperty().get()));
        Settings.setHigherBoundary(higherBoundaryProperty().get());
        Settings.setTicks(ticksProperty().get());
        Settings.setVisLoss(isLoss());
        Settings.setVisBBoxLoss(isBboxLoss());
        Settings.setVisClsLoss(isClsLoss());
        Settings.setVisAccuracy(isAcc());
        Settings.setVisAccuracyRegression(isAccReg());
        Settings.setVisLearningRate(isTrainRate());
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    //<editor-fold defaultstate="collapsed" desc="used JavaFX Properties">
    private final StringProperty title = new SimpleStringProperty();
    
    private String getTitle() {
        return title.get();
    }
    
    private void setTitle(String value) {
        title.set(value);
    }
    
    private StringProperty titleProperty() {
        return title;
    }
    private final StringProperty file = new SimpleStringProperty();
    
    private String getFile() {
        return file.get();
    }
    
    private void setFile(String value) {
        file.set(value);
    }
    
    private StringProperty fileProperty() {
        return file;
    }
    private final IntegerProperty higherBoundary = new SimpleIntegerProperty();
    
    private int getHigherBoundary() {
        return higherBoundary.get();
    }
    
    private void setHigherBoundary(int value) {
        higherBoundary.set(value);
    }
    
    private IntegerProperty higherBoundaryProperty() {
        return higherBoundary;
    }
    private final IntegerProperty ticks = new SimpleIntegerProperty();
    
    private int getTicks() {
        return ticks.get();
    }
    
    private void setTicks(int value) {
        ticks.set(value);
    }
    
    private IntegerProperty ticksProperty() {
        return ticks;
    }
    private final BooleanProperty loss = new SimpleBooleanProperty();
    
    private boolean isLoss() {
        return loss.get();
    }
    
    private void setLoss(boolean value) {
        loss.set(value);
    }
    
    private BooleanProperty lossProperty() {
        return loss;
    }
    private final BooleanProperty bboxLoss = new SimpleBooleanProperty();
    
    private boolean isBboxLoss() {
        return bboxLoss.get();
    }
    
    private void setBboxLoss(boolean value) {
        bboxLoss.set(value);
    }
    
    private BooleanProperty bboxLossProperty() {
        return bboxLoss;
    }
    private final BooleanProperty clsLoss = new SimpleBooleanProperty();
    
    private boolean isClsLoss() {
        return clsLoss.get();
    }
    
    private void setClsLoss(boolean value) {
        clsLoss.set(value);
    }
    
    private BooleanProperty clsLossProperty() {
        return clsLoss;
    }
    private final BooleanProperty acc = new SimpleBooleanProperty();
    
    private boolean isAcc() {
        return acc.get();
    }
    
    private void setAcc(boolean value) {
        acc.set(value);
    }
    
    private BooleanProperty accProperty() {
        return acc;
    }
    private final BooleanProperty AccReg = new SimpleBooleanProperty();
    
    private boolean isAccReg() {
        return AccReg.get();
    }
    
    private void setAccReg(boolean value) {
        AccReg.set(value);
    }
    
    private BooleanProperty AccRegProperty() {
        return AccReg;
    }
    private final BooleanProperty trainRate = new SimpleBooleanProperty();
    
    private boolean isTrainRate() {
        return trainRate.get();
    }
    
    private void setTrainRate(boolean value) {
        trainRate.set(value);
    }
    
    private BooleanProperty learnRateProperty() {
        return trainRate;
    }
    private final BooleanProperty fileChoosen = new SimpleBooleanProperty(false);
    
    private boolean isFileChoosen() {
        return fileChoosen.get();
    }
    
    private void setFileChoosen(boolean value) {
        fileChoosen.set(value);
    }
    
    private BooleanProperty fileChoosenProperty() {
        return fileChoosen;
    }
//</editor-fold>

}
