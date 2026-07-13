package backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "word_examples")
public class WordExample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(nullable = false, length = 1500)
    private String sentence;

    @Column(nullable = false, length = 1500)
    private String translation;

    protected WordExample() {}

    public String getSentence() { return sentence; }
    public String getTranslation() { return translation; }
}
