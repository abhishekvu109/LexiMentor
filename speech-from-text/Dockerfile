# Use the official Python image as the base image
FROM python:3.9-slim

# Set the working directory in the container
# WORKDIR /app

# Copy the requirements file into the container at /app
RUN pip install --no-cache-dir Flask==2.0.1 gTTS==2.2.3 flask_cors

RUN pip install --upgrade flask werkzeug

# Copy the current directory contents into the container at /app
COPY . .

# Expose the port on which your Flask app will run
EXPOSE 8300

# Command to run the Flask application
CMD ["python", "main.py"]