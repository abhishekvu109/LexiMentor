import spacy

# Load the English language model in spaCy
nlp = spacy.load("en_core_web_md")


def preprocess_text(text):
    # Tokenize and preprocess the text
    doc = nlp(text)

    # Remove stopwords and punctuation, and lemmatize the tokens
    processed_tokens = [token.lemma_.lower() for token in doc if not token.is_stop and not token.is_punct]

    # Join the tokens back into a string
    processed_text = " ".join(processed_tokens)

    return processed_text


def calculate_similarity(original_meaning, student_answer):
    # Preprocess the original meaning and student's answer
    original_meaning = preprocess_text(original_meaning)
    student_answer = preprocess_text(student_answer)

    # Compute semantic similarity using spaCy's similarity function
    original_doc = nlp(original_meaning)
    student_doc = nlp(student_answer)
    similarity_score = original_doc.similarity(student_doc)

    return similarity_score


def judge_answer(original_meaning, student_answer, threshold=0.7):
    try:
        similarity_score = calculate_similarity(original_meaning, student_answer)
    except Exception as e:
        print(f"Error occurred during similarity calculation: {e}")
        return {"isCorrect": False, "confidence": 0}

    # Judge based on similarity score
    if similarity_score >= threshold:
        is_correct = True
    else:
        is_correct = False

    # Adjust confidence based on similarity score
    confidence = int(similarity_score * 100)

    return {"isCorrect": is_correct, "confidence": confidence}


# Example usage:
original_meaning = "honest (adjective): someone who is morally upright and uncorrupted; synonyms: truthful, sincere; example sentence: She is known for her honest and straightforward approach."
student_answer = "Honest"

result = judge_answer(original_meaning, student_answer)
print(result)
