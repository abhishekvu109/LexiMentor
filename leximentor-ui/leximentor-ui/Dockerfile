# Use official Node.js image as the base image
FROM node:alpine

# Set the working directory in the container
#WORKDIR /app

# Copy package.json and package-lock.json to the working directory
#COPY package*.json /app
COPY . /

# Install dependencies
RUN npm install

# Copy the rest of the application code

# Build the Next.js application
RUN npm run build

# Expose the port on which Next.js application will run
EXPOSE 3000

# Command to run the Next.js application
CMD ["npm", "start"]