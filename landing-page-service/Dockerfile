FROM nginx:1.25.3

# Needed because locally we build containers with Docker Compose but in production we build images individually and the
# ENV properties can only be set when building containers, so we do that extra step here.
ARG APP_SERVICE_HOST
ARG APP_SERVICE_ROOT
ENV APP_SERVICE_HOST=${APP_SERVICE_HOST}
ENV APP_SERVICE_ROOT=${APP_SERVICE_ROOT}

COPY ./default.conf.template /etc/nginx/templates/default.conf.template
COPY ./bin/ /usr/share/nginx/html
