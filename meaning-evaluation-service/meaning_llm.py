import os
import urllib.request
from llama_cpp import Llama
from flask import Flask, request, jsonify
import requests
import json
import re
import logging

app = Flask(__name__)

ggml_model_path = "https://huggingface.co/TheBloke/zephyr-7B-beta-GGUF/resolve/main/zephyr-7b-beta.Q4_0.gguf"
filename = "zephyr-7b-beta.Q4_0.gguf"
logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)


def download_file(file_link, filename):
    # Checks if the file already exists before downloading
    if not os.path.isfile(filename):
        urllib.request.urlretrieve(file_link, filename)
        print("File downloaded successfully.")
    else:
        print("File already exists.")


def generate_text(prompt, llm):
    max_tokens = 256
    temperature = 0.1
    top_p = 0.5
    echo = False
    stop = ["#"]

    output = llm(
        prompt,
        max_tokens=max_tokens,
        temperature=temperature,
        top_p=top_p,
        echo=echo,
        stop=stop,
    )
    output_text = output["choices"][0]["text"].strip()
    return output_text


def generate_prompt_from_template(input):
    chat_prompt_template = f"""<|im_start|>system
You are a helpful chatbot.<|im_end|>
<|im_start|>user
{input}<|im_end|>"""
    return chat_prompt_template


def main(text):
    download_file(ggml_model_path, filename)
    llm = Llama(model_path=filename, n_ctx=512, n_batch=126)
    prompt = generate_prompt_from_template(text)
    return generate_text(
        prompt=prompt,
        llm=llm
    )


@app.route('/evaluation/meaning/llm', methods=['POST'])
def handle_post_request():
    data = request.json
    logger.info(data)
    print(data["text"])
    logger.info('Data is ' + data["text"])
    logger.info(data["text"])
    response = main(data["text"])
    return format(response)


def format(response_string):
    logger.info('I am inside the format ' + response_string)
    match = re.search(r'{.*?}', response_string)
    if match:
        json_string = match.group()
        response_dict = json.loads(json_string)

        is_correct = response_dict["isCorrect"]
        confidence = response_dict["confidence"]
        explanation = response_dict["explanation"]

        data = {
            "isCorrect": is_correct,
            "confidence": confidence,
            "explanation": explanation
        }
        return json.dumps(data)
    else:
        data = {
            "error": "No JSON substring found in the input string."
        }
        return json.dumps(data)


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=7300)
#
# if __name__ == '__main__':
#     text = """
#     Consider you are an evaluator of an exam. Your task is to judge if the student has answered correctly for the meaning of a word.
#     As an input you will get the word itself, the parts of speech, the original meaning,and the meaning given my the student.
#     Generate only a JSON like the below
#     {
#         "isCorrect": true/false,
#         "confidence": 0-100,
#         "explanation": "Something"
#     }
#
#     isCorrect means your opinion if the answer is correct, confidence is your confidence in judging the answer, and explanation is why you feel the answer is correct or incorrect.
#
#     Below is the drill:
#
#     word: honest
#     original meaning: Someone is moral, and someone uncorrupted.
#
#     Student answer: A person with high values, and never does anything wrong or lies.
#     """
#
#     response = main(text)
#     print(response)
