FROM alpine
USER root

RUN apk add bash curl git make openjdk8-jre
RUN apk add librdkafka --repository=http://dl-cdn.alpinelinux.org/alpine/edge/testing/

WORKDIR /tmp/clojure-install

RUN curl -O https://download.clojure.org/install/linux-install-1.10.1.469.sh
RUN chmod +x linux-install-1.10.1.469.sh
RUN ./linux-install-1.10.1.469.sh

WORKDIR /usr/local/bin
RUN curl -O https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && chmod +x lein && ./lein

WORKDIR /workspaces/fahrenheit-lang

CMD ["bash"]
