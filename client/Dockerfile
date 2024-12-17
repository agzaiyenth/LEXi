# Use Node.js to build the frontend
FROM node:18-alpine
WORKDIR /app

# Install dependencies and build the web version
COPY package.json yarn.lock ./
RUN npm install
COPY . .
RUN npx expo export:web

# Serve the web build using a lightweight web server
FROM nginx:alpine
COPY --from=0 /app/web-build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
