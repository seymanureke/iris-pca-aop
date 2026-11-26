package com.irispca.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
public class MonitoringAspect {

    private static final Map<String, MethodMetrics> METRICS_MAP = new ConcurrentHashMap<>();

    @Pointcut("execution(* com.irispca.core..*(..))")
    public void monitoredMethods() {}

    @Around("monitoredMethods()")
    public Object measureExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Runtime runtime = Runtime.getRuntime();

        long beforeUsedMemory = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            long durationNs = System.nanoTime() - startTime;
            long afterUsedMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryDeltaBytes = afterUsedMemory - beforeUsedMemory;

            recordMetric(methodName, durationNs, memoryDeltaBytes, true);

            double durationMs = durationNs / 1_000_000.0;
            System.out.printf("[MonitoringAspect] %s tamamlandi | Sure: %.3f ms | Bellek Farki: %d byte%n",
                    methodName, durationMs, memoryDeltaBytes);

            return result;
        } catch (Throwable ex) {
            long durationNs = System.nanoTime() - startTime;
            long afterUsedMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryDeltaBytes = afterUsedMemory - beforeUsedMemory;

            recordMetric(methodName, durationNs, memoryDeltaBytes, false);
            throw ex;
        }
    }

    private void recordMetric(String methodName, long durationNs, long memoryDeltaBytes, boolean success) {
        METRICS_MAP.compute(methodName, (key, existing) -> {
            if (existing == null) {
                existing = new MethodMetrics();
            }
            existing.addSample(durationNs, memoryDeltaBytes, success);
            return existing;
        });
    }

    public static void printPerformanceReport() {
        System.out.println("\n================ PERFORMANS METRIKLERI RAPORU ================");
        if (METRICS_MAP.isEmpty()) {
            System.out.println("Henuz olculmus metot cagrisi bulunmuyor.");
            System.out.println("=============================================================\n");
            return;
        }

        for (Map.Entry<String, MethodMetrics> entry : METRICS_MAP.entrySet()) {
            String method = entry.getKey();
            MethodMetrics m = entry.getValue();
            double totalMs = m.totalDurationNs.get() / 1_000_000.0;
            double avgMs = m.callCount.get() > 0 ? totalMs / m.callCount.get() : 0.0;
            double minMs = m.minDurationNs.get() == Long.MAX_VALUE ? 0.0 : m.minDurationNs.get() / 1_000_000.0;
            double maxMs = m.maxDurationNs.get() / 1_000_000.0;

            System.out.printf("- Metot: %s%n", method);
            System.out.printf("  Cagri Sayisi: %d (Basarili: %d, Hatali: %d)%n",
                    m.callCount.get(), m.successCount.get(), m.failureCount.get());
            System.out.printf("  Toplam Sure: %.3f ms | Ort. Sure: %.3f ms | Min: %.3f ms | Max: %.3f ms%n",
                    totalMs, avgMs, minMs, maxMs);
            System.out.printf("  Toplam Bellek Degisimi: %d byte%n", m.totalMemoryDeltaBytes.get());
        }
        System.out.println("=============================================================\n");
    }

    public static void resetMetrics() {
        METRICS_MAP.clear();
    }

    private static class MethodMetrics {
        final AtomicLong callCount = new AtomicLong(0);
        final AtomicLong successCount = new AtomicLong(0);
        final AtomicLong failureCount = new AtomicLong(0);
        final AtomicLong totalDurationNs = new AtomicLong(0);
        final AtomicLong minDurationNs = new AtomicLong(Long.MAX_VALUE);
        final AtomicLong maxDurationNs = new AtomicLong(0);
        final AtomicLong totalMemoryDeltaBytes = new AtomicLong(0);

        void addSample(long durationNs, long memoryDeltaBytes, boolean success) {
            callCount.incrementAndGet();
            if (success) {
                successCount.incrementAndGet();
            } else {
                failureCount.incrementAndGet();
            }
            totalDurationNs.addAndGet(durationNs);
            totalMemoryDeltaBytes.addAndGet(memoryDeltaBytes);

            minDurationNs.accumulateAndGet(durationNs, Math::min);
            maxDurationNs.accumulateAndGet(durationNs, Math::max);
        }
    }
}