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
            definition = row[1]
            word = {
                "word": word,
                "source": 'Barron',
                "language": "en-us",
                "meanings": [
                    {
                        "meaning": definition,
                        "source": 'Barron'
                    }
                ]
            }
            words.append(word)
        json_file_path = 'data/barron.json'
        with open(json_file_path, 'w') as json_file:
            # Use json.dump to write the list to the file
            json.dump(words, json_file)


if __name__ == '__main__':
    csv_file_path = 'data/barron_333.csv'
    main(csv_file_path=csv_file_path)
