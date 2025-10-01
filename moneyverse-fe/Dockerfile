FROM node:24-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist/moneyverse-fe/browser /usr/share/nginx/html
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY nginx/sse-proxy.conf /etc/nginx/sse-proxy.conf
COPY nginx/api-proxy.conf /etc/nginx/api-proxy.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
