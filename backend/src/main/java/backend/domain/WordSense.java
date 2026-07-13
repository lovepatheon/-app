package backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "word_senses")
public class WordSense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "part_of_speech", nullable = false, length = 30)
    private String partOfSpeech;

    @Column(name = "definition_cn", nullable = false, length = 1000)
    private String definitionCn;

    @Column(name = "definition_en", nullable = false, length = 1000)
    private String definitionEn;

    protected WordSense() {}

    public String getPartOfSpeech() { return partOfSpeech; }
    public String getDefinitionCn() { return definitionCn; }
    public String getDefinitionEn() { return definitionEn; }
}
