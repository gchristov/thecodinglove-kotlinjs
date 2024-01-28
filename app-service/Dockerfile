FROM node:16-slim AS npm

ARG PORT

WORKDIR /app

# Step 1: Install Node.js dependencies
COPY /bin/package.json .
RUN npm install --only=production

# Step 2: Copy app files
COPY /bin/. .

# Step 3: Run binaries
FROM npm AS run
WORKDIR /app
CMD exec node --enable-source-maps thecodinglove-kotlinjs-app-service.js -port ${PORT}
