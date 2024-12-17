# Build the Expo Web app
FROM node:18-alpine
WORKDIR /app
COPY package.json yarn.lock ./
RUN npm install
COPY . .
RUN npx expo export:web

# Serve the web build with NGINX
FROM nginx:alpine
COPY --from=0 /app/web-build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
