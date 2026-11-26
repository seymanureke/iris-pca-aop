package com.irispca.core;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

public class PcaAnalyzer {

    private double[] explainedVarianceRatio;

    public double[][] applyPca(double[][] standardizedData, int numComponents) {
        RealMatrix matrix = MatrixUtils.createRealMatrix(standardizedData);
        Covariance covariance = new Covariance(matrix);
        RealMatrix covMatrix = covariance.getCovarianceMatrix();

        EigenDecomposition eigen = new EigenDecomposition(covMatrix);
        double[] eigenValues = eigen.getRealEigenvalues();

        double totalVariance = 0;
        for (double val : eigenValues) totalVariance += val;

        explainedVarianceRatio = new double[numComponents];
        RealMatrix projectionMatrix = MatrixUtils.createRealMatrix(standardizedData[0].length, numComponents);

        for (int i = 0; i < numComponents; i++) {
            explainedVarianceRatio[i] = (eigenValues[i] / totalVariance) * 100.0;
            projectionMatrix.setColumnVector(i, eigen.getEigenvector(i));
        }

        RealMatrix pcaResult = matrix.multiply(projectionMatrix);
        return pcaResult.getData();
    }

    public double[] getExplainedVarianceRatio() {
        return explainedVarianceRatio;
    }
}