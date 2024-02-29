import nltk
nltk.download('punkt')
from flask import Flask, request, jsonify
from nltk.corpus import wordnet
from nltk import pos_tag
from nltk.tokenize import word_tokenize

app = Flask(__name__)

import spacy
from spacy import displacy

# Load the English language model from spaCy
nlp = spacy.load("en_core_web_sm")

def get_word_info(word):
    # Perform POS tagging using spaCy
    doc = nlp(word)

    # Get the first token in the document
    first_token = doc[0]

    # Extract information from the token
    pos = first_token.pos_
    word_category = first_token.dep_
    pronunciation = word  # spaCy doesn't provide pronunciation information

    # Synonyms and antonyms (You may need a different approach for this in spaCy)
    synonyms = set()
    antonyms = set()

    # Example sentences
    examples = []

    # Additional information as needed

    return {
        "Word": word,
        "Pos": pos,
        "Category": word_category,
        "Pronunciation": pronunciation,
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
    app.run(debug=True,host='0.0.0.0',port=5700)
