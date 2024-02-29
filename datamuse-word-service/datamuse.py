import nltk
nltk.download('punkt')
from flask import Flask, request, jsonify
from nltk.corpus import wordnet
from nltk import pos_tag
from nltk.tokenize import word_tokenize

app = Flask(__name__)

import spacy
import requests

nlp = spacy.load("en_core_web_sm")

def get_synonyms_antonyms_examples(word):
    # Datamuse API endpoint for suggestions
    api_url = "https://api.datamuse.com/words"

    # Get suggestions for the input word
    response = requests.get(api_url, params={"ml": word, "md": "dfp", "ipa": "1"})
    
    if response.status_code == 200:
        data = response.json()

        # Extract synonyms, antonyms, and examples from Datamuse response
        synonyms = [result["word"] for result in data if "syn" in result.get("tags", [])]
        antonyms = [result["word"] for result in data if "ant" in result.get("tags", [])]
        examples = [result["word"] for result in data if "example" in result.get("tags", [])]

        return synonyms, antonyms, examples
    else:
        return [], [], []

def get_word_info(word):
    doc = nlp(word)

    first_token = doc[0]

    pos = first_token.pos_
    word_category = first_token.dep_
    pronunciation = word  # spaCy doesn't provide pronunciation information

    # Get synonyms, antonyms, and examples using Datamuse API
    synonyms, antonyms, examples = get_synonyms_antonyms_examples(word)

    return {
        "Word": word,
        "Pos": pos,
        "Category": word_category,
        "Pronunciation": pronunciation,
        "Synonyms": synonyms,
        "Antonyms": antonyms,
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
    app.run(debug=True,host='0.0.0.0',port=7700)
