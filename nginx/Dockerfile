FROM nginx:alpine

RUN rm /etc/nginx/conf.d/default.conf

COPY ./default.conf /etc/nginx/conf.d/default.conf

COPY ./backend-not-found.html /var/www/html/backend-not-found.html

COPY ./includes/ /etc/nginx/includes/

# Expose ports 80 and 443
EXPOSE 80 443

# Start NGINX
CMD ["nginx", "-g", "daemon off;"]