import spacy

# Load the English language model in spaCy
nlp = spacy.load("en_core_web_md")


def calculate_similarity(original_meaning, student_answer):
    # Tokenize and parse the original meaning and student's answer
    original_doc = nlp(original_meaning)
    student_doc = nlp(student_answer)

    # Compute semantic similarity using spaCy's similarity function
    similarity_score = original_doc.similarity(student_doc)

    return similarity_score


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
student_answer = "A person who is honest"

result = judge_answer(original_meaning, student_answer)
print(result)
