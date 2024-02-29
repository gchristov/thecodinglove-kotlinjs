FROM node:16-slim AS npm

ARG PORT
# Args get wiped on multistage builds so we persist them in the environment
ENV PORT=${PORT}

WORKDIR /app

# Step 1: Install Node.js dependencies
COPY /productionExecutable/package.json .
RUN npm install --only=production

# Step 2: Copy app files
COPY /productionExecutable .

# Step 3: Run binaries
FROM npm AS run
WORKDIR /app
CMD exec node --enable-source-maps slack-web-service.js -port ${PORT}
