FROM prom/prometheus:v3.5.0

COPY prometheus-prod.yml /etc/prometheus/prometheus.yml.template

EXPOSE 9090

ENTRYPOINT ["/bin/sh", "-c", "sed \"s|__APP_SERVER_HOST__|$APP_SERVER_HOST|g\" /etc/prometheus/prometheus.yml.template > /etc/prometheus/prometheus.yml && exec /bin/prometheus --config.file=/etc/prometheus/prometheus.yml --storage.tsdb.path=/prometheus --storage.tsdb.retention.time=15d --web.enable-lifecycle"]