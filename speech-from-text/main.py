from flask import Flask, request, send_file, Response
from gtts import gTTS
from flask_cors import CORS
import io

app = Flask(__name__)
CORS(app)


@app.route('/text-to-speech', methods=['POST'])
def text_to_speech():
    data = request.get_json()
    text = data.get('text')

    # Create a gTTS object
    tts = gTTS(text=text, lang='en', slow=False)

    # Save the audio file to a BytesIO object
    audio_data = io.BytesIO()
    tts.write_to_fp(audio_data)
    audio_data.seek(0)

    # Return the audio file with correct content type and filename in Content-Disposition header
    return send_file(audio_data, mimetype='audio/mp3', as_attachment=True, download_name='output.mp3')


if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=8300)
