import json
import re

from flask import Flask, request, jsonify

app = Flask(__name__)


def validate_json(raw_json: str):
    object_matches = re.findall(r'\{[^{}]+}', raw_json)

    valid_objects = []
    for obj_str in object_matches:
        try:
            # Try parsing each object to ensure it's valid JSON
            parsed = json.loads(obj_str)
            required_keys = {"id", "type", "subType", "incorrectText", "correctedText", "explanation"}
            # Only keep the object if it has all required keys
            if required_keys.issubset(parsed.keys()):
                valid_objects.append(parsed)
        except json.JSONDecodeError:
            continue  # Skip invalid JSON fragments

    # Step 2: Reconstruct a clean JSON
    clean_json = {"errorList": valid_objects}
    return clean_json


def add_start_end_indexes(clean_json):
    for error in clean_json["errorList"]:
        error["start"] = -1
        error["end"] = -1
    return clean_json


def update_start_end_indexes(json_str, error_list):
    # Track visited positions to handle duplicates
    visited_positions = []

    # Process each error in the errorList
    for error in error_list:
        incorrect_text = error['incorrectText']
        # Escape special characters in incorrect_text for regex
        escaped_text = re.escape(incorrect_text)

        # Find all matches of incorrect_text in json_str
        matches = [(m.start(), m.end()) for m in re.finditer(escaped_text, json_str)]

        # Filter out already visited matches
        for start, end in matches:
            if start not in visited_positions:
                error['start'] = start
                error['end'] = end
                visited_positions.append(start)
                break
        else:
            # If no unvisited match is found, keep start and end as -1
            error['start'] = -1
            error['end'] = -1

    return error_list


@app.route('/api/writewise/util/v1/low-level-evaluation/fix-indexes', methods=['POST'])
def validate_update_json():
    data = request.get_json()
    json_str = data.get("text", "")
    error_list = data.get("errorList", "")
    validated_json = validate_json(error_list)
    json_with_indexes = add_start_end_indexes(validated_json)
    updated_indexes = update_start_end_indexes(json_str, json_with_indexes)
    return jsonify(updated_indexes)


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=4545)
