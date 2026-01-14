package ru.artem.papyan.ragbot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.artem.papyan.ragbot.preprocessing.*;
import ru.artem.papyan.ragbot.processing.DocumentProcessingService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotDataScheduler {

    private final RawDataCollector rawDataCollector;
    private final RawDataCleaner rawDataCleaner;
    private final CleanDataDereferencer cleanDataDereferencer;
    private final CleanDataFictionalFilterer fictionalFilterer;
    private final FilteredDataMasker filteredDataMasker;
    private final CleanDataReplacer cleanDataReplacer;
    private final DocumentProcessingService processingService;

    @Scheduled(cron = "${config.data-cron}")
    public void schedule() {
        try {
            rawDataCollector.collectAndSaveRawData();
            long cleanedRaw = rawDataCleaner.processRawDataFiles();
            if (cleanedRaw == 0) {
                log.info("No updates");
                return;
            }
            cleanDataDereferencer.transform();
            fictionalFilterer.filter();
            filteredDataMasker.transform();
            cleanDataReplacer.transform();
            processingService.processDocuments();
        } catch (Exception e) {
            log.warn("error while performing scheduling task: {}", e.getMessage());
        }
    }
}
