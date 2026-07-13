package backend.service;

import backend.api.PageResponse;
import backend.api.WordDtos;
import backend.domain.*;
import backend.exception.BusinessException;
import backend.repository.UserWordProgressRepository;
import backend.repository.WordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WordService {
    private final WordRepository words;
    private final UserWordProgressRepository progress;

    public WordService(WordRepository words, UserWordProgressRepository progress) {
        this.words = words;
        this.progress = progress;
    }

    @Transactional(readOnly = true)
    public PageResponse<WordDtos.SummaryResponse> list(long userId, String keyword, WordLevel level,
                                                       MasteryLevel mastery, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BusinessException(10001, HttpStatus.BAD_REQUEST, "分页参数不合法");
        }
        String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        Page<Word> result = words.search(userId, normalizedKeyword, level, mastery,
                PageRequest.of(page, size, Sort.by("word").ascending()));
        Map<Long, UserWordProgress> progressMap = progress.findByUserIdAndWordIdIn(userId,
                        result.getContent().stream().map(Word::getId).toList()).stream()
                .collect(Collectors.toMap(item -> item.getWord().getId(), Function.identity()));
        return PageResponse.from(result, word -> toSummary(word, progressMap.get(word.getId())));
    }

    @Transactional(readOnly = true)
    public WordDtos.DetailResponse detail(long userId, long wordId) {
        Word word = requireWord(wordId);
        return toDetail(word, progress.findByUserIdAndWordId(userId, wordId).orElse(null));
    }

    @Transactional(readOnly = true)
    public Word requireWord(long wordId) {
        return words.findById(wordId)
                .orElseThrow(() -> new BusinessException(12001, HttpStatus.NOT_FOUND, "单词不存在"));
    }

    public WordDtos.SummaryResponse toSummary(Word word, UserWordProgress value) {
        return new WordDtos.SummaryResponse(word.getId(), word.getWord(), word.getPhonetic(),
                word.getLevels().stream().sorted().toList(), word.getBriefDefinition(),
                value == null ? MasteryLevel.NEW : value.getMastery(),
                value == null ? null : DateTimes.offset(value.getNextReviewAt()));
    }

    public WordDtos.DetailResponse toDetail(Word word, UserWordProgress value) {
        return new WordDtos.DetailResponse(word.getId(), word.getWord(), word.getPhonetic(),
                word.getPronunciationUrl(), word.getLevels().stream().sorted().toList(),
                word.getBriefDefinition(), value == null ? MasteryLevel.NEW : value.getMastery(),
                word.getSenses().stream().map(s -> new WordDtos.SenseResponse(
                        s.getPartOfSpeech(), s.getDefinitionCn(), s.getDefinitionEn())).toList(),
                word.getExamples().stream().map(e -> new WordDtos.ExampleResponse(
                        e.getSentence(), e.getTranslation())).toList(),
                List.copyOf(word.getCollocations()), word.getUsageNote(), word.getMemoryTip(), word.getSource());
    }
}
