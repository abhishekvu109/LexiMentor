update  inv_synonym set synonym=CONCAT(UPPER(SUBSTRING(synonym, 1, 1)), LOWER(SUBSTRING(synonym, 2)));
update  inv_antonym set antonym=CONCAT(UPPER(SUBSTRING(antonym, 1, 1)), LOWER(SUBSTRING(antonym, 2)));
update  inv_meaning set definition=CONCAT(UPPER(SUBSTRING(definition, 1, 1)), LOWER(SUBSTRING(definition, 2)));
update  inv_word_metadata set word=CONCAT(UPPER(SUBSTRING(word, 1, 1)), LOWER(SUBSTRING(word, 2)));
update  inv_example set example=CONCAT(UPPER(SUBSTRING(example, 1, 1)), LOWER(SUBSTRING(example, 2)));
update  inv_parts_of_speech set pos=CONCAT(UPPER(SUBSTRING(pos, 1, 1)), LOWER(SUBSTRING(pos, 2)));
commit ;