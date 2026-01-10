package ru.artem.papyan.ragbot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
public class EnvConfig {

    @Value("${config.fandom-id}")
    private String fandomId;

    @Value("${config.exclusions}")
    private List<String> exclusions;

    @Value("${config.content-exclusions}")
    private List<String> contentExclusions;

    @Value("${config.raw-data-dir}")
    private String rawDataDir;

    @Value("${config.clean-data-dir}")
    private String cleanDataDir;

    @Value("${config.dereferenced-data-dir}")
    private String dereferencedDataDir;

    @Value("${config.filtered-data-dir}")
    private String filteredDataDir;

    @Value("${config.masked-data-dir}")
    private String maskedDataDir;

    @Value("${config.masked-mapping-file}")
    private String maskedMappingFile;

    @Value("${config.replaced-data-dir}")
    private String replacedDataDir;
}
