INSERT INTO words (id, word, phonetic, pronunciation_url, brief_definition, usage_note, memory_tip, source) VALUES
(1, 'contemplate', '/ˈkɒntəmpleɪt/', NULL, '沉思；仔细考虑', '后接动名词，不接不定式：contemplate doing，而不是 contemplate to do。', 'con-（共同）+ temple（庙宇）：像在安静的庙宇里沉思。', 'WordHarbor editorial sample; CET-6 list metadata, for development use'),
(2, 'versatile', '/ˈvɜːsətaɪl/', NULL, '多才多艺的；用途广泛的', '既可以形容人能力多样，也可以形容工具或材料用途广泛。', 'verse 有“转”的感觉，能够灵活转向多种用途。', 'WordHarbor editorial sample; CET-6 list metadata, for development use'),
(3, 'deteriorate', '/dɪˈtɪəriəreɪt/', NULL, '恶化；变坏', '常作不及物动词，主语通常是情况、健康、关系或质量。', '联想“低（de）到更糟的状态”。', 'WordHarbor editorial sample; CET-6 list metadata, for development use'),
(4, 'allocate', '/ˈæləkeɪt/', NULL, '分配；拨给', '常用结构：allocate sth. to sb./sth. 或 allocate sth. for sth.。', 'al + locate：把资源“定位”到某个地方。', 'WordHarbor editorial sample; CET-4/6 list metadata, for development use'),
(5, 'ambiguous', '/æmˈbɪɡjuəs/', NULL, '含糊不清的；有歧义的', '强调语言或含义可以有多种解释；vague 更侧重信息不具体。', 'ambi- 表示“两边”，两边都说得通，所以有歧义。', 'WordHarbor editorial sample; CET-6 list metadata, for development use'),
(6, 'coherent', '/kəʊˈhɪərənt/', NULL, '连贯的；条理清楚的', '常形容论述、计划、政策或说话方式。', 'co-（一起）+ here（黏住）：观点都黏在一起，很连贯。', 'WordHarbor editorial sample; CET-6 list metadata, for development use'),
(7, 'inevitable', '/ɪnˈevɪtəbl/', NULL, '不可避免的', '常用于客观趋势或结果，名词形式为 inevitability。', 'in-（不）+ evitable（可避免的）。', 'WordHarbor editorial sample; CET-4/6 list metadata, for development use'),
(8, 'substantial', '/səbˈstænʃl/', NULL, '大量的；重大的；实质的', '比 big 正式，常搭配 amount、evidence、investment、change。', 'substance 是“实质”，有实质分量就是 substantial。', 'WordHarbor editorial sample; CET-4/6 list metadata, for development use');

INSERT INTO word_levels (word_id, level) VALUES
(1, 'CET6'), (2, 'CET6'), (3, 'CET6'), (4, 'CET4'), (4, 'CET6'),
(5, 'CET6'), (6, 'CET6'), (7, 'CET4'), (7, 'CET6'), (8, 'CET4'), (8, 'CET6');

INSERT INTO word_senses (word_id, sort_order, part_of_speech, definition_cn, definition_en) VALUES
(1, 0, 'v.', '深思，仔细考虑', 'to think deeply about something'),
(1, 1, 'v.', '考虑接受或采取', 'to consider doing or accepting something'),
(2, 0, 'adj.', '多才多艺的；多用途的', 'able to adapt or be used for many purposes'),
(3, 0, 'v.', '恶化，质量下降', 'to become worse in quality or condition'),
(4, 0, 'v.', '分配资源、时间或资金', 'to distribute something for a particular purpose'),
(5, 0, 'adj.', '有多种解释的，不明确的', 'open to more than one interpretation'),
(6, 0, 'adj.', '逻辑连贯、容易理解的', 'logical, consistent, and easy to understand'),
(7, 0, 'adj.', '必然发生、无法避免的', 'certain to happen and impossible to avoid'),
(8, 0, 'adj.', '数量或程度很大的', 'large in amount, value, or importance');

INSERT INTO word_examples (word_id, sort_order, sentence, translation) VALUES
(1, 0, 'She contemplated the problem from every angle.', '她从各个角度仔细考虑了这个问题。'),
(1, 1, 'He is contemplating a career change.', '他正在考虑转行。'),
(2, 0, 'This versatile tool works in several different ways.', '这件多用途工具有好几种用法。'),
(3, 0, 'The weather began to deteriorate rapidly.', '天气开始迅速恶化。'),
(4, 0, 'The team allocated more time to testing.', '团队为测试分配了更多时间。'),
(5, 0, 'The wording of the question is ambiguous.', '这个问题的措辞有歧义。'),
(6, 0, 'She presented a clear and coherent argument.', '她提出了清晰而连贯的论点。'),
(7, 0, 'Some degree of change is inevitable.', '某种程度的变化是不可避免的。'),
(8, 0, 'The project requires substantial investment.', '这个项目需要大量投资。');

INSERT INTO word_collocations (word_id, sort_order, collocation) VALUES
(1, 0, 'contemplate doing sth.'), (1, 1, 'contemplate the future'), (1, 2, 'seriously contemplate'),
(2, 0, 'highly versatile'), (2, 1, 'versatile performer'), (2, 2, 'versatile material'),
(3, 0, 'deteriorate rapidly'), (3, 1, 'condition deteriorates'), (3, 2, 'relations deteriorate'),
(4, 0, 'allocate resources'), (4, 1, 'allocate funds to'), (4, 2, 'allocate time for'),
(5, 0, 'ambiguous statement'), (5, 1, 'remain ambiguous'), (5, 2, 'deliberately ambiguous'),
(6, 0, 'coherent argument'), (6, 1, 'coherent strategy'), (6, 2, 'remain coherent'),
(7, 0, 'inevitable result'), (7, 1, 'seem inevitable'), (7, 2, 'almost inevitable'),
(8, 0, 'substantial amount'), (8, 1, 'substantial evidence'), (8, 2, 'substantial improvement');
