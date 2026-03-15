FROM python:3.11-slim

WORKDIR /app

# Install system deps needed by some ML libs
RUN apt-get update && apt-get install -y --no-install-recommends \
        gcc \
        libgomp1 \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Create artefacts directory (model files will be volume-mounted in prod)
RUN mkdir -p /app/artefacts

EXPOSE 8001

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001"]
