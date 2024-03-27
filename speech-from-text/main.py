from flask import Flask, request, send_file
from gtts import gTTS
import os

app = Flask(__name__)


@app.route('/text-to-speech', methods=['POST'])
def text_to_speech():
    data = request.get_json()
    text = data.get('text')
    lang = data.get('lang', 'en')

    # Create a gTTS object
    tts = gTTS(text=text, lang=lang, slow=False)

    # Save the audio file
    tts.save("output.mp3")

    # Return the audio file
    return send_file("output.mp3", as_attachment=True)


if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=8300)
