FROM node:lts-alpine as builder

WORKDIR /usr/src/app

COPY ./package.json ./

RUN npm install

COPY . .

ARG VITE_API_URL

ENV VITE_API_URL=$VITE_API_URL

RUN npm run build

FROM nginx:stable-alpine  

COPY --from=builder /usr/src/app/dist /usr/share/nginx/html

COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
