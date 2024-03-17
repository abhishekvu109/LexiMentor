import spacy
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity

# Load pre-trained word embeddings
nlp = spacy.load("en_core_web_md")


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
    similarity_score = compute_similarity(candidate_answer, correct_answer)

    # Adjust confidence threshold based on your requirements
    confidence_threshold = 0.7

    if similarity_score >= confidence_threshold:
        return "Correct", similarity_score
    else:
        return "Incorrect", similarity_score


# Example usage
candidate_answer = "A person who is genuine."
correct_answer = "A person who is truthful, and moral."
result, confidence = evaluate_answer(candidate_answer, correct_answer)
print("Result:", result)
print("Confidence Score:", confidence)