import os
import json
import re
import logging
from flask import Flask, request, jsonify

logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)


def main():
    str = "{\"isCorrect\": false,\"confidence\": 50,\"explanation\": \"While the student's definition includes the idea of breadth being wide and broad, it does not accurately reflect the official definition's emphasis on understanding a broad range of topics. The student's definition seems to focus more on physical width, rather than intellectual breadth.\"}";
    return format(str)


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
        return jsonify(data)
    else:
        data = {
            "error": "No JSON substring found in the input string."
        }
        logging.info(jsonify(data))
        return jsonify(data)


if __name__ == '__main__':
    main()
