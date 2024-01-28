FROM nginx:1.25.3

ARG APP_SERVICE_HOST
ARG APP_SERVICE_ROOT
# Need to forward the nginx env variables manually for production. Locally we use Docker Compose which does this automatically.
ENV APP_SERVICE_HOST=${APP_SERVICE_HOST}
ENV APP_SERVICE_ROOT=${APP_SERVICE_ROOT}

COPY ./default.conf.template /etc/nginx/templates/default.conf.template
COPY ./bin/ /usr/share/nginx/html
