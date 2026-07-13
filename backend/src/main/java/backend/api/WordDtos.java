package backend.api;

import backend.domain.MasteryLevel;
import backend.domain.WordLevel;
import java.time.OffsetDateTime;
import java.util.List;

public final class WordDtos {
    private WordDtos() {}

    public record SenseResponse(String partOfSpeech, String definitionCn, String definitionEn) {}
    public record ExampleResponse(String sentence, String translation) {}

    public record SummaryResponse(long id, String word, String phonetic, List<WordLevel> level,
                                  String briefDefinition, MasteryLevel mastery, OffsetDateTime nextReviewAt) {}

    public record DetailResponse(long id, String word, String phonetic, String pronunciationUrl,
                                 List<WordLevel> level, String briefDefinition, MasteryLevel mastery,
                                 List<SenseResponse> senses, List<ExampleResponse> examples,
                                 List<String> collocations, String usageNote, String memoryTip, String source) {}
}
