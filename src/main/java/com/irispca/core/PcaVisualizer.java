package com.irispca.core;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PcaVisualizer {

    public void savePcaPlot(double[][] pcaData, String[] labels, String outputPath) throws IOException {
        new File("outputs").mkdirs();

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Iris Veri Seti - PCA 2D İndirgeme Sonucu")
                .xAxisTitle("Temel Bileşen 1 (PC1)")
                .yAxisTitle("Temel Bileşen 2 (PC2)")
                .build();

        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setMarkerSize(8);

        String[] classes = {"Iris-setosa", "Iris-versicolor", "Iris-virginica"};
        for (String cls : classes) {
            List<Double> xList = new ArrayList<>();
            List<Double> yList = new ArrayList<>();
            for (int i = 0; i < pcaData.length; i++) {
                if (labels[i].equals(cls)) {
                    xList.add(pcaData[i][0]);
                    yList.add(pcaData[i][1]);
                }
            }
            chart.addSeries(cls, xList, yList);
        }

        BitmapEncoder.saveBitmap(chart, outputPath, BitmapEncoder.BitmapFormat.PNG);
    }
}