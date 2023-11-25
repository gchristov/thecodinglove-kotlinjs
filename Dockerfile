# Step 1: Install Node.js dependencies
FROM node:16-slim AS npm
WORKDIR /app
COPY /build/bin/package.json ./bin/
RUN npm install --prefix ./bin/ --only=production
COPY /build/bin/. ./bin/

# Step 2: Run binaries
FROM npm AS run
WORKDIR ./bin/
CMD exec node --enable-source-maps thecodinglove-kmp-appBackend.js
