INSERT INTO words (id, word, phonetic, brief_definition, usage_note, memory_tip, source, created_at, updated_at)
VALUES (101, 'contemplate', '/test/', '沉思；仔细考虑', '后接动名词。', '测试记忆提示', 'WordHarbor integration test fixture', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO word_levels (word_id, level) VALUES (101, 'CET6');
INSERT INTO word_senses (word_id, sort_order, part_of_speech, definition_cn, definition_en)
VALUES (101, 0, 'v.', '深思，仔细考虑', 'to think deeply about something');
INSERT INTO word_examples (word_id, sort_order, sentence, translation)
VALUES (101, 0, 'She contemplated the problem.', '她仔细考虑了这个问题。');
INSERT INTO word_collocations (word_id, sort_order, collocation)
VALUES (101, 0, 'contemplate doing sth.');
