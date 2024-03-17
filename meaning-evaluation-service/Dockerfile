# Use an official Python runtime as a parent image
FROM python:3.8-slim

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app

RUN apt-get update && \
    apt-get install gcc-11 g++-11 &&\
    apt-get install -y python3-dev && \
    pip install --trusted-host pypi.python.org flask nltk spacy requests llama-cpp-python

# Make port 5600 available to the world outside this container
EXPOSE 7700

# Define environment variable
ENV NAME World

# Run app.py when the container launches
CMD ["python", "meaning_llm.py"]