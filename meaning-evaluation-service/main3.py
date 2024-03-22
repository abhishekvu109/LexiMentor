import openai

# Set up your OpenAI API key
openai.api_key = ''


def compute_similarity(candidate_answer, correct_answer):
    # Use the GPT-3 model to compute semantic similarity
    response = openai.Completion.create(
        engine="davinci-002",
        prompt=f"Similarity between \"{candidate_answer}\" and \"{correct_answer}\":",
        max_tokens=50
    )
    similarity_score = float(response.choices[0].text)
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
