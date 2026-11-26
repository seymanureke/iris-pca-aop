package com.irispca.core;

import org.apache.commons.math3.stat.StatUtils;

public class IrisDataLoader {

    private static final String[] FEATURE_NAMES = {
            "Sepal Length (cm)", "Sepal Width (cm)", "Petal Length (cm)", "Petal Width (cm)"
    };
    private static final String[] TARGET_CLASSES = {"Iris-setosa", "Iris-versicolor", "Iris-virginica"};

    public double[][] loadRawFeatures() {
        return new double[][]{
                // Iris-setosa
                {5.1, 3.5, 1.4, 0.2}, {4.9, 3.0, 1.4, 0.2}, {4.7, 3.2, 1.3, 0.2}, {4.6, 3.1, 1.5, 0.2}, {5.0, 3.6, 1.4, 0.2},
                {5.4, 3.9, 1.7, 0.4}, {4.6, 3.4, 1.4, 0.3}, {5.0, 3.4, 1.5, 0.2}, {4.4, 2.9, 1.4, 0.2}, {4.9, 3.1, 1.5, 0.1},
                // Iris-versicolor
                {7.0, 3.2, 4.7, 1.4}, {6.4, 3.2, 4.5, 1.5}, {6.9, 3.1, 4.9, 1.5}, {5.5, 2.3, 4.0, 1.3}, {6.5, 2.8, 4.6, 1.5},
                {5.7, 2.8, 4.5, 1.3}, {6.3, 3.3, 4.7, 1.6}, {4.9, 2.4, 3.3, 1.0}, {6.6, 2.9, 4.6, 1.3}, {5.2, 2.7, 3.9, 1.4},
                // Iris-virginica
                {6.3, 3.3, 6.0, 2.5}, {5.8, 2.7, 5.1, 1.9}, {7.1, 3.0, 5.9, 2.1}, {6.3, 2.9, 5.6, 1.8}, {6.5, 3.0, 5.8, 2.2},
                {7.6, 3.0, 6.6, 2.1}, {4.9, 2.5, 4.5, 1.7}, {7.3, 2.9, 6.3, 1.8}, {6.7, 2.5, 5.8, 1.8}, {7.2, 3.6, 6.1, 2.5}
        };
    }

    public String[] loadLabels() {
        String[] labels = new String[30];
        for (int i = 0; i < 10; i++) labels[i] = TARGET_CLASSES[0];
        for (int i = 10; i < 20; i++) labels[i] = TARGET_CLASSES[1];
        for (int i = 20; i < 30; i++) labels[i] = TARGET_CLASSES[2];
        return labels;
    }

    public void printDatasetScope(double[][] data) {
        System.out.println("=== IRIS VERİ SETİ KAPSAMI ===");
        System.out.println("Örnek Sayısı: " + data.length + " | Öznitelik Sayısı: " + data[0].length);
        System.out.println("Öznitelikler: " + String.join(", ", FEATURE_NAMES));
        System.out.println("Hedef Sınıflar: " + String.join(", ", TARGET_CLASSES));
    }

    public double[][] standardize(double[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        double[][] standardized = new double[rows][cols];

        for (int j = 0; j < cols; j++) {
            double[] column = new double[rows];
            for (int i = 0; i < rows; i++) column[i] = data[i][j];

            double mean = StatUtils.mean(column);
            double std = Math.sqrt(StatUtils.variance(column));

            for (int i = 0; i < rows; i++) {
                standardized[i][j] = (data[i][j] - mean) / std;
            }
        }
        return standardized;
    }
}