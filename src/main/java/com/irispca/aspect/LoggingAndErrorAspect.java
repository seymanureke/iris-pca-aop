package com.irispca.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Aspect
public class LoggingAndErrorAspect {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Pointcut("execution(* com.irispca.core..*(..))")
    public void loggedCoreMethods() {}

    @Before("loggedCoreMethods()")
    public void logStepStart(JoinPoint joinPoint) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String methodName = joinPoint.getSignature().toShortString();
        String argsSummary = summarizeArgs(joinPoint.getArgs());

        System.out.printf("[%s] [LOG - BASLADI] %s | Parametreler: %s%n",
                timestamp, methodName, argsSummary);
    }

    @AfterReturning(pointcut = "loggedCoreMethods()", returning = "result")
    public void logStepSuccess(JoinPoint joinPoint, Object result) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String methodName = joinPoint.getSignature().toShortString();
        String resultSummary = summarizeResult(result);

        System.out.printf("[%s] [LOG - TAMAMLANDI] %s | Sonuc: %s%n",
                timestamp, methodName, resultSummary);
    }

    @AfterThrowing(pointcut = "loggedCoreMethods()", throwing = "ex")
    public void logStepError(JoinPoint joinPoint, Throwable ex) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String methodName = joinPoint.getSignature().toShortString();
        String argsSummary = summarizeArgs(joinPoint.getArgs());

        System.err.printf("[%s] [HATA YAKALANDI] %s | Hata Tipi: %s | Mesaj: %s | Parametreler: %s%n",
                timestamp,
                methodName,
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                argsSummary);
    }

    private String summarizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "yok";
        }
        return Arrays.stream(args)
                .map(this::describeObject)
                .toList()
                .toString();
    }

    private String summarizeResult(Object result) {
        if (result == null) {
            return "void / null";
        }
        return describeObject(result);
    }

    private String describeObject(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof double[][] matrix) {
            int cols = (matrix.length > 0 && matrix[0] != null) ? matrix[0].length : 0;
            return "double[" + matrix.length + "][" + cols + "]";
        }
        if (obj instanceof double[] arr) {
            return "double[" + arr.length + "]";
        }
        if (obj instanceof String[] strArr) {
            return "String[" + strArr.length + "]";
        }
        String text = obj.toString();
        return text.length() > 80 ? text.substring(0, 77) + "..." : text;
    }
}
