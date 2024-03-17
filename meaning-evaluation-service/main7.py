import spacy
from sklearn.metrics.pairwise import cosine_similarity

# Load the English language model in spaCy
nlp = spacy.load("en_core_web_md")


def calculate_similarity(original_meaning, student_answer):
    # Tokenize the original meaning and student's answer
    original_tokens = nlp(original_meaning)
    student_tokens = nlp(student_answer)

    # Compute word embeddings for each token
    original_embeddings = [token.vector for token in original_tokens]
    student_embeddings = [token.vector for token in student_tokens]

    # Calculate cosine similarity between the embeddings
    similarity_matrix = cosine_similarity(original_embeddings, student_embeddings)

    # Average the similarities to get an overall similarity score
    overall_similarity = similarity_matrix.mean()

    return overall_similarity


def judge_answer(original_meaning, student_answer):
    similarity_score = calculate_similarity(original_meaning, student_answer)

    # Adjust confidence based on similarity score
    confidence = min(int(similarity_score * 100) + 30, 100)

    # Judge based on confidence level
    if similarity_score >= 0.7:
        is_correct = True
    else:
        is_correct = False

    return {"isCorrect": is_correct, "confidence": confidence}


# Example usage:
original_meaning = "Someone is moral, and someone uncorrupted."
student_answer = "An ethical person, and true to everyone."

result = judge_answer(original_meaning, student_answer)
print(result)
