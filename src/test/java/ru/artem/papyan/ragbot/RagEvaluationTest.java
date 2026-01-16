package ru.artem.papyan.ragbot;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.artem.papyan.ragbot.domain.UserSearchRequest;
import ru.artem.papyan.ragbot.processing.RequestProcessor;

import java.util.List;

@Slf4j
@SpringBootTest
class RagEvaluationTest {

    private final List<String> negativeMarkers = List.of(
            "not mentioned",
            "does not mention",
            "doesn’t include",
            "do not include",
            "don’t have enough information",
            "don’t have any information",
            "can’t find",
            "isn’t included"
    );

    private final List<String> knownQuestions = List.of(
            "Who is Daniel Thompson?",
            "Who is Zoe?",
            "Who is Sly Vixen?",
            "Who is Luna?",
            "Who or what is Morphling?",
            "Who is Étienne Lefèvre?"
    );
    private final List<String> unknownQuestions = List.of(
            "Who is Ripper?",
            "Who is Joseph Zada?",
            "Who is Vaughan Reilly?",
            "What is Mockingjay Deal?"
    );

    private final RequestProcessor requestProcessor;

    @Autowired
    RagEvaluationTest(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    @Test
    void evaluateAnswers() {
        for (String question : knownQuestions) {
            log.info("testing known question: {}", question);

            String response = requestProcessor.processRequest(new UserSearchRequest(question));

            for (String marker : negativeMarkers) {
                Assertions.assertFalse(response.toLowerCase().contains(marker.toLowerCase()));
            }
        }

        for (String question : unknownQuestions) {

            log.info("testing unknown question: {}", question);

            String response = requestProcessor.processRequest(new UserSearchRequest(question)).toLowerCase();
            Assertions.assertTrue(negativeMarkers.stream().anyMatch(response::contains));
        }
    }
}
