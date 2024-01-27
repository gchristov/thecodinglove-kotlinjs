FROM nginx:1.25.3

ARG APP_SERVICE_HOST
ARG APP_SERVICE_ROOT
ENV APP_SERVICE_HOST=${APP_SERVICE_HOST}
ENV APP_SERVICE_ROOT=${APP_SERVICE_ROOT}

COPY ./default.conf.template /etc/nginx/templates/default.conf.template
COPY ./bin/ /usr/share/nginx/html
