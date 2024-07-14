FROM nginx:alpine

# Remove the default configuration file
RUN rm /etc/nginx/conf.d/default.conf

# Copy the custom nginx.conf to the container
COPY ./nginx.conf /etc/nginx/nginx.conf

# Copy the custom default.conf to the container
COPY ./default.conf /etc/nginx/conf.d/default.conf

# Copy the backend-not-found.html to the container
COPY ./backend-not-found.html /var/www/html/backend-not-found.html

# Copy the includes directory to the container
COPY ./includes/ /etc/nginx/includes/

# Expose port 6060
EXPOSE 6060

# Start NGINX
CMD ["nginx", "-g", "daemon off;"]
