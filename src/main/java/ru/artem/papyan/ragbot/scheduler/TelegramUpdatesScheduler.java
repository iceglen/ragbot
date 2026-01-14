package ru.artem.papyan.ragbot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.artem.papyan.ragbot.messenger.TelegramApi;
import ru.artem.papyan.ragbot.messenger.TelegramWorker;
import ru.artem.papyan.ragbot.messenger.domain.Updates;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class TelegramUpdatesScheduler {

    private static final AtomicLong offsetHolder = new AtomicLong(0L);

    private final TelegramApi api;
    private final TelegramWorker worker;

    @Autowired
    public TelegramUpdatesScheduler(TelegramApi api, TelegramWorker worker) {
        this.api = api;
        this.worker = worker;
    }

    @Scheduled(fixedDelay = 500L, scheduler = "messengerTaskScheduler")
    public void fetchUpdates() {
        long prevoiusOffset = offsetHolder.get();

        log.debug("fetching telegram updates with offset {}", prevoiusOffset);

        Updates updates = api.getUpdates(prevoiusOffset);
        worker.process(updates);

        long offset = updates.result().stream()
                .mapToLong(u -> u.updateId() + 1)
                .max().orElse(0L);

        log.debug("next offset is {}", offset);

        while (offset > prevoiusOffset && !offsetHolder.compareAndSet(prevoiusOffset, offset)) {
            prevoiusOffset = offsetHolder.get();
        }
    }
}
