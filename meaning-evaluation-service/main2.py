import spacy
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from nltk.corpus import wordnet

# Load pre-trained word embeddings
nlp = spacy.load("en_core_web_md")


def get_synonyms(word):
    synonyms = set()
    for syn in wordnet.synsets(word):
        for lemma in syn.lemmas():
            synonyms.add(lemma.name().lower())
    return synonyms


def compute_similarity(candidate_answer, correct_answer):
    # Tokenize and preprocess candidate's answer
    candidate_tokens = [token.text.lower() for token in nlp(candidate_answer) if
                        not token.is_stop and not token.is_punct]

    # Tokenize and preprocess correct answer
    correct_tokens = [token.text.lower() for token in nlp(correct_answer) if not token.is_stop and not token.is_punct]

    # Compute word embeddings
    candidate_embeddings = np.mean([token.vector for token in nlp(" ".join(candidate_tokens))], axis=0)
    correct_embeddings = np.mean([token.vector for token in nlp(" ".join(correct_tokens))], axis=0)

    # Compute cosine similarity
    similarity_score = cosine_similarity([candidate_embeddings], [correct_embeddings])[0][0]

    return similarity_score


def evaluate_answer(candidate_answer, correct_answer):
    # Compute similarity score using embeddings
    similarity_score = compute_similarity(candidate_answer, correct_answer)

    # Get synonyms for words in correct answer
    correct_synonyms = set()
    for word in correct_answer.split():
        correct_synonyms.update(get_synonyms(word))

    # Compute weighted similarity score
    weighted_similarity_score = similarity_score + (0.1 * len(set(candidate_answer.lower().split()) & correct_synonyms))

    # Adjust confidence based on weighted similarity score
    confidence_threshold = 0.7
    if weighted_similarity_score >= confidence_threshold:
        return "Correct", weighted_similarity_score
    else:
        return "Incorrect", weighted_similarity_score


# Example usage
candidate_answer = "Honest"
correct_answer = "A person who is truthful, and moral."
result, confidence = evaluate_answer(candidate_answer, correct_answer)
print("Result:", result)
print("Confidence Score:", confidence)
# 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