from sentence_transformers import SentenceTransformer, util

# Load a pre-trained Sentence Transformers model
model = SentenceTransformer('paraphrase-MiniLM-L6-v2')


def compute_similarity(candidate_answer, correct_answer):
    # Encode the candidate and correct answers
    candidate_embedding = model.encode(candidate_answer, convert_to_tensor=True)
    correct_embedding = model.encode(correct_answer, convert_to_tensor=True)

    # Compute cosine similarity between the embeddings
    similarity_score = util.pytorch_cos_sim(candidate_embedding, correct_embedding)

    return similarity_score.item()


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
