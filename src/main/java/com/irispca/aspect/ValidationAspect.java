package com.irispca.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Collection;

@Aspect
public class ValidationAspect {

    @Pointcut("execution(* com.irispca.core..*(..))")
    public void coreMethods() {}

    @Before("coreMethods()")
    public void validateArguments(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        if (args == null || args.length == 0) {
            return;
        }

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];

            if (arg == null) {
                throw new IllegalArgumentException(
                        "[ValidationAspect] '" + methodName + "' metodunun " + (i + 1) + ". parametresi null olamaz."
                );
            }

            if (arg instanceof String str && str.isBlank()) {
                throw new IllegalArgumentException(
                        "[ValidationAspect] '" + methodName + "' metodunun " + (i + 1) + ". metin parametresi bos olamaz."
                );
            }

            if (arg instanceof Collection<?> col && col.isEmpty()) {
                throw new IllegalArgumentException(
                        "[ValidationAspect] '" + methodName + "' metodunun " + (i + 1) + ". koleksiyon parametresi bos olamaz."
                );
            }

            if (arg instanceof double[] arr) {
                if (arr.length == 0) {
                    throw new IllegalArgumentException(
                            "[ValidationAspect] '" + methodName + "' metodunun tek boyutlu double dizisi bos olamaz."
                    );
                }
                validateDoubleArray1D(methodName, arr);
            } else if (arg instanceof double[][] matrix) {
                if (matrix.length == 0) {
                    throw new IllegalArgumentException(
                            "[ValidationAspect] '" + methodName + "' metodunun matrisi bos olamaz."
                    );
                }
                int expectedCols = matrix[0].length;
                for (int r = 0; r < matrix.length; r++) {
                    double[] row = matrix[r];
                    if (row == null || row.length == 0) {
                        throw new IllegalArgumentException(
                                "[ValidationAspect] '" + methodName + "' metodunda " + r + ". satir null veya bos."
                        );
                    }
                    if (row.length != expectedCols) {
                        throw new IllegalArgumentException(
                                "[ValidationAspect] '" + methodName + "' metodunda duzensiz satir boyutu tespit edildi (beklenen: "
                                        + expectedCols + ", bulunan: " + row.length + ")."
                        );
                    }
                    validateDoubleArray1D(methodName, row);
                }
            } else if (arg instanceof Integer intVal && methodName.toLowerCase().contains("component")) {
                if (intVal <= 0) {
                    throw new IllegalArgumentException(
                            "[ValidationAspect] '" + methodName + "' icin bilesen sayisi pozitif olmalidir, gelen: " + intVal
                    );
                }
            }
        }
    }

    private void validateDoubleArray1D(String methodName, double[] values) {
        for (int j = 0; j < values.length; j++) {
            double v = values[j];
            if (Double.isNaN(v) || Double.isInfinite(v)) {
                throw new IllegalArgumentException(
                        "[ValidationAspect] '" + methodName + "' icinde gecersiz sayisal deger (NaN/Infinite) bulundu: " + v
                );
            }
        }
    }
}