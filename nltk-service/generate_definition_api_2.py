import nltk
nltk.download('punkt')
from flask import Flask, request, jsonify
from nltk.corpus import wordnet
from nltk import pos_tag
from nltk.tokenize import word_tokenize

app = Flask(__name__)

def get_word_info(word):
    # Get synsets for the given word
    synsets = wordnet.synsets(word)

    if not synsets:
        return f"No information found for the word '{word}'"

    # Extract information from the first synset
    first_synset = synsets[0]

    # Definition
    definition = first_synset.definition()

    # Parts of speech
    pos_tags = pos_tag(word_tokenize(word))
    pos = pos_tags[0][1] if pos_tags else None

    # Word category (lexical category)
    word_category = first_synset.pos()

    # Pronunciation
    pronunciation = first_synset.lexname()

    # Synonyms and antonyms
    synonyms = set()
    antonyms = set()

    for lemma in first_synset.lemmas():
        synonyms.add(lemma.name())
        for antonym in lemma.antonyms():
            antonyms.add(antonym.name())

    # Remove the original word from synonyms
    synonyms.discard(word)

    # Example sentences
    examples = [example.replace('_', ' ') for example in first_synset.examples()]

    # Additional information as needed

    return {
        "Word": word,
        "Definition": definition,
        "Pos": word_category,
        "Synonyms": list(synonyms),
        "Antonyms": list(antonyms),
        "Examples": examples
        # Add more fields as needed
    }

@app.route('/get_word_info', methods=['GET'])
def word_info():
    word = request.args.get('word')

    if not word:
        return jsonify({"error": "Missing 'word' parameter"}), 400

    result = get_word_info(word)
    return jsonify(result)

if __name__ == '__main__':
    app.run(debug=True,port=5700)
