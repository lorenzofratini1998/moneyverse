FROM krakend:2.10 as builder
ARG ENV=demo

COPY krakend.tmpl .
COPY config .

## Save temporary file to /tmp to avoid permission errors
RUN FC_ENABLE=1 \
    FC_OUT=/tmp/krakend.json \
    FC_PARTIALS="/etc/krakend/partials/$ENV" \
    FC_SETTINGS="/etc/krakend/settings/$ENV" \
    FC_TEMPLATES="/etc/krakend/templates" \
    krakend check -d -t -c krakend.tmpl --lint

FROM krakend:2.10
# Keep operating system updated with security fixes between releases
RUN apk upgrade --no-cache --no-interactive

COPY --from=builder --chown=krakend:nogroup /tmp/krakend.json .

EXPOSE 8080