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

/**
 *
 * @author Matti J. Frind
 */
class Settings {
    static String title = "Robot Training"; //chart title
    static File inputFile = new File("train2.txt"); //Input File
    static int lowerBoundary = 0; //lower bound of the charts
    static int higherBoundary = 60000; //higher bound of the charts
    static int ticks = 1000; //ticks of the chart
    static boolean visLoss = true; //visualize Loss
    static boolean visBBoxLoss = true; //visualize Bounding Box Loss
    static boolean visClsLoss = true; //visualize Classification Loss
    static boolean visAccuracy = true; //visualize Accuracy
    static boolean visAccuracyRegression = true; //visualize Accuracy Regression
    static boolean visLearningRate = true; //visualize Learning Rate
    static final int ACCURACY_REGRESSION_ORDER = 1; // Order of the Accuracy regression

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        Settings.title = title;
    }

    public static File getInputFile() {
        return inputFile;
    }

    public static void setInputFile(File inputFile) {
        Settings.inputFile = inputFile;
    }

    public static int getLowerBoundary() {
        return lowerBoundary;
    }

    public static void setLowerBoundary(int lowerBoundary) {
        Settings.lowerBoundary = lowerBoundary;
    }

    public static int getHigherBoundary() {
        return higherBoundary;
    }

    public static void setHigherBoundary(int higherBoundary) {
        Settings.higherBoundary = higherBoundary;
    }

    public static int getTicks() {
        return ticks;
    }

    public static void setTicks(int ticks) {
        Settings.ticks = ticks;
    }

    public static boolean isVisLoss() {
        return visLoss;
    }

    public static void setVisLoss(boolean visLoss) {
        Settings.visLoss = visLoss;
    }

    public static boolean isVisBBoxLoss() {
        return visBBoxLoss;
    }

    public static void setVisBBoxLoss(boolean visBBoxLoss) {
        Settings.visBBoxLoss = visBBoxLoss;
    }

    public static boolean isVisClsLoss() {
        return visClsLoss;
    }

    public static void setVisClsLoss(boolean visClsLoss) {
        Settings.visClsLoss = visClsLoss;
    }

    public static boolean isVisAccuracy() {
        return visAccuracy;
    }

    public static void setVisAccuracy(boolean visAccuracy) {
        Settings.visAccuracy = visAccuracy;
    }

    public static boolean isVisAccuracyRegression() {
        return visAccuracyRegression;
    }

    public static void setVisAccuracyRegression(boolean visAccuracyRegression) {
        Settings.visAccuracyRegression = visAccuracyRegression;
    }
    
    public static boolean isVisLearningRate() {
        return visLearningRate;
    }

    public static void setVisLearningRate(boolean visLearningRate) {
        Settings.visLearningRate = visLearningRate;
    }

    
}
