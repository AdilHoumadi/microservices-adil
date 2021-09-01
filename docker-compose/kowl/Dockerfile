FROM quay.io/cloudhut/kowl:v1.4.0

RUN apk update && apk add --no-cache netcat-openbsd bash

ADD bin/wait-for-it.sh /app/wait-for-it.sh
ADD bin/run.sh /app/run.sh

ENTRYPOINT ["/app/run.sh"]