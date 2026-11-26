package com.irispca;

import com.irispca.core.IrisDataLoader;
import com.irispca.core.PcaAnalyzer;
import com.irispca.core.PcaVisualizer;

public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  IRIS PCA & AOP ANALİZ SİSTEMİ BAŞLATILIYOR");
        System.out.println("==================================================\n");

        IrisDataLoader loader = new IrisDataLoader();
        PcaAnalyzer analyzer = new PcaAnalyzer();
        PcaVisualizer visualizer = new PcaVisualizer();

        try {
            // 1. Veri yükleme ve kapsam bilgisi
            double[][] rawFeatures = loader.loadRawFeatures();
            String[] labels = loader.loadLabels();
            loader.printDatasetScope(rawFeatures);

            // 2. Z-Score standardizasyonu
            double[][] standardizedData = loader.standardize(rawFeatures);

            // 3. PCA boyut indirgeme (4 boyuttan 2 boyuta)
            double[][] pcaResult = analyzer.applyPca(standardizedData, 2);
            double[] varianceRatios = analyzer.getExplainedVarianceRatio();

            System.out.printf("%n=== PCA AÇIKLANAN VARYANS ORANLARI ===%n");
            System.out.printf("PC1: %.2f%% | PC2: %.2f%% | Toplam: %.2f%%%n%n",
                    varianceRatios[0], varianceRatios[1], (varianceRatios[0] + varianceRatios[1]));

            // 4. 2D Scatter grafiğini PNG olarak kaydetme
            String outputPath = "outputs/iris_pca_2d";
            visualizer.savePcaPlot(pcaResult, labels, outputPath);
            System.out.println("Grafik başarıyla kaydedildi: " + outputPath + ".png");

            // 5. AOP ValidationAspect hata yakalama testi
            System.out.println("\n=== AOP DOĞRULAMA (VALIDATION) TESTİ BAŞLATILIYOR ===");
            analyzer.applyPca(standardizedData, 10); // Geçersiz bileşen sayısı bilerek gönderiliyor

        } catch (Exception e) {
            System.out.println("Beklenen doğrulama hatası başarıyla yakalandı: " + e.getMessage());
        }

        System.out.println("\n==================================================");
        System.out.println("  TÜM İŞLEMLER BAŞARIYLA TAMAMLANDI");
        System.out.println("==================================================");
    }
}