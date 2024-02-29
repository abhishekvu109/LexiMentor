# Use an official Python runtime as a parent image
FROM python:3.8-slim

# Set the working directory in the container
WORKDIR /app

# COPY requirements.txt /app/
COPY . /app
# Install any needed packages specified in requirements.txt
# RUN pip install --trusted-host pypi.python.org -r requirements.txt
# Copy the current directory contents into the container at /app
# COPY . /app

# Install any needed packages specified in requirements.txt
RUN pip install --trusted-host pypi.python.org flask nltk

# Make port 5600 available to the world outside this container
EXPOSE 5600

# Define environment variable
ENV NAME World

# Run app.py when the container launches
CMD ["python", "nltk.py"]
