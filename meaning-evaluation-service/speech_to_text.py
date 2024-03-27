from gtts import gTTS
import os


def text_to_speech(text, lang='en'):
    # Create a gTTS object
    tts = gTTS(text=text, lang=lang, slow=False)

    # Save the audio file
    tts.save("output.mp3")

    # Play the audio file
    os.system("start output.mp3")  # Works on Windows


if __name__ == "__main__":
    text = input("Enter the text to convert to speech: ")
    text_to_speech(text)
