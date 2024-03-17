from llama_cpp import Llama

from IPython.display import display, HTML
import json
import time
import pathlib

MODEL_Q2_K = Llama(
    model_path="../models/llama-2-7b-chat.ggmlv3.q2_K.bin",
    n_ctx=2048)


def query(model, question):
    model_name = pathlib.Path(model.model_path).name
    time_start = time.time()
    prompt = f"Q: {question} A:"
    output = model(prompt=prompt, max_tokens=0)  # if max tokens is zero, depends on n_ctx
    response = output["choices"][0]["text"]
    time_elapsed = time.time() - time_start
    display(HTML(f'<code>{model_name} response time: {time_elapsed:.02f} sec</code>'))
    display(HTML(f'<strong>Question:</strong> {question}'))
    display(HTML(f'<strong>Answer:</strong> {response}'))
    print(json.dumps(output, indent=2))


if __name__ == '__main__':
    query(MODEL_Q2_K, "Why are Jupyter notebooks difficult to maintain?")
