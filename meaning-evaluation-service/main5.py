from transformers import BertTokenizer, BertModel
import torch
from scipy.spatial.distance import cosine

# Load pre-trained BERT model and tokenizer
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
model = BertModel.from_pretrained('bert-base-uncased')


def compute_similarity(candidate_answer, correct_answer):
    # Tokenize the input text
    inputs = tokenizer(candidate_answer, correct_answer, return_tensors='pt', padding=True, truncation=True)

    # Forward pass through the model
    with torch.no_grad():
        outputs = model(**inputs)

    # Extract the embeddings for the [CLS] tokens
    candidate_embedding = outputs.last_hidden_state[0][0]
    correct_embedding = outputs.last_hidden_state[0][1]

    # Compute cosine similarity between the embeddings
    similarity_score = 1 - cosine(candidate_embedding, correct_embedding)

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
