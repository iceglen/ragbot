package ru.artem.papyan.ragbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.OkHttpClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.artem.papyan.ragbot.messenger.TelegramApi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BeanConfig {

    private final EnvConfig envConfig;

    @Autowired
    public BeanConfig(EnvConfig envConfig) {
        this.envConfig = envConfig;
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public OkHttpClient okHttpClient() {
        var client = new OkHttpClient();

        client.dispatcher().setMaxRequestsPerHost(5);
        client.dispatcher().setMaxRequests(5);

        return client;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .build();
    }

    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return TokenTextSplitter.builder()
                .withMinChunkSizeChars(10)
                .withKeepSeparator(true)
                .build();
    }

    @Bean(name = "messengerTaskScheduler")
    public ThreadPoolTaskScheduler messengerTaskScheduler() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("messenger-");
        return scheduler;
    }

    @Bean
    public TelegramApi telegramApi() {
        return TelegramApi.getInstance(
                envConfig.getTelegramAccessToken(),
                envConfig.getUpdatesLimit(),
                envConfig.getUpdatesTimeout()
        );
    }
}
