package com.skillgap.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.skillgap.service.JobOfferImportService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JobOfferImportScheduler {

    private final JobOfferImportService importService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Scheduled(cron = "${app.import.scheduler.cron}")
    public void runImport() {
        if (isRunning.compareAndSet(false, true)) {
            try {
                importService.importAll();
            } finally {
                isRunning.set(false);
            }
        }
    }

}
