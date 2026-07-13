package backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String word;

    @Column(nullable = false, length = 160)
    private String phonetic;

    @Column(name = "pronunciation_url", length = 500)
    private String pronunciationUrl;

    @Column(name = "brief_definition", nullable = false, length = 500)
    private String briefDefinition;

    @Column(name = "usage_note", nullable = false, columnDefinition = "TEXT")
    private String usageNote;

    @Column(name = "memory_tip", nullable = false, columnDefinition = "TEXT")
    private String memoryTip;

    @Column(nullable = false, length = 500)
    private String source;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "word_levels", joinColumns = @JoinColumn(name = "word_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 10)
    private Set<WordLevel> levels = new LinkedHashSet<>();

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<WordSense> senses = new ArrayList<>();

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<WordExample> examples = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "word_collocations", joinColumns = @JoinColumn(name = "word_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "collocation", nullable = false, length = 300)
    private List<String> collocations = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Word() {}

    public Long getId() { return id; }
    public String getWord() { return word; }
    public String getPhonetic() { return phonetic; }
    public String getPronunciationUrl() { return pronunciationUrl; }
    public String getBriefDefinition() { return briefDefinition; }
    public String getUsageNote() { return usageNote; }
    public String getMemoryTip() { return memoryTip; }
    public String getSource() { return source; }
    public Set<WordLevel> getLevels() { return levels; }
    public List<WordSense> getSenses() { return senses; }
    public List<WordExample> getExamples() { return examples; }
    public List<String> getCollocations() { return collocations; }
}
