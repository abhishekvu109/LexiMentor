# Use an official Python runtime as a parent image
FROM python:3.8-slim

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Install any needed packages specified in requirements.txt
RUN pip install --trusted-host pypi.python.org flask nltk spacy requests
RUN python -m spacy download en_core_web_sm

# Make port 5600 available to the world outside this container
EXPOSE 7700

# Define environment variable
ENV NAME World

# Run app.py when the container launches
CMD ["python", "datamuse.py"]