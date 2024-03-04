import re
import csv
import json
import os


def main(csv_file_path):
    csv_file_path = os.path.normpath(csv_file_path)
    words = []
    with open(csv_file_path, 'r') as csv_file:
        # Create a CSV reader
        csv_reader = csv.reader(csv_file)

        for row in csv_reader:
            word = row[0]
            meaning = row[1]
            pos_list = extract_pos(meaning)
            print(pos_list)
            pos_data = []
            if pos_list is not None:
                for p in pos_list:
                    pos_data.append({

                        "pos": p,
                        "source": "Manhattan"

                    })
            word = {
                "word": word,
                "source": 'Manhattan',
                "language": "en-us",
                "meanings": [
                    {
                        "meaning": meaning,
                        "source": 'Manhattan'
                    }
                ],
                "partsOfSpeeches": pos_data
            }
            words.append(word)
        json_file_path = 'data/manhattan.json'
        with open(json_file_path, 'w') as json_file:
            # Use json.dump to write the list to the file
            json.dump(words, json_file)


def extract_pos(input_string):
    # Extract content inside parentheses
    match = re.match(r'\((.*?)\)', input_string)
    if match:
        content_inside_parentheses = match.group(1)

        # Extract strings separated by commas
        extracted_strings = re.findall(r'\b\w+\b', content_inside_parentheses)

        return extracted_strings

    return None


if __name__ == '__main__':
    csv_file_path = 'data/manhattan_500.csv'
    main(csv_file_path=csv_file_path)
