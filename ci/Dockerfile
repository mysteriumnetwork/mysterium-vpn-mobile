#
# GitLab CI react-native-android v0.1
#
# https://hub.docker.com/r/webcuisine/gitlab-ci-react-native-android/
# https://github.com/cuisines/gitlab-ci-react-native-android
#

FROM ubuntu:18.04
MAINTAINER Sascha-Matthias Kulawik <sascha@kulawik.de>

RUN echo "Android SDK 26.1.1"
ENV VERSION_SDK_TOOLS "4333796"

ENV ANDROID_HOME "/sdk"
ENV PATH "$PATH:${ANDROID_HOME}/tools"
ENV DEBIAN_FRONTEND noninteractive

RUN apt-get -qq update && \
    apt-get install -qqy --no-install-recommends \
      bzip2 \
      curl \
      git-core \
      html2text \
      openjdk-8-jdk \
      libc6-i386 \
      lib32stdc++6 \
      lib32gcc1 \
      lib32z1 \
      gnupg2 \
      unzip \
    && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN rm -f /etc/ssl/certs/java/cacerts; \
    /var/lib/dpkg/info/ca-certificates-java.postinst configure

RUN curl -s https://dl.google.com/android/repository/sdk-tools-linux-${VERSION_SDK_TOOLS}.zip > /sdk.zip && \
    unzip /sdk.zip -d /sdk && \
    rm -v /sdk.zip

RUN mkdir -p $ANDROID_HOME/licenses/ \
  && echo "8933bad161af4178b1185d1a37fbf41ea5269c55\nd56f5187479451eabf01fb78af6dfcb131a6481e" > $ANDROID_HOME/licenses/android-sdk-license \
  && echo "84831b9409646a918e30573bab4c9c91346d8abd" > $ANDROID_HOME/licenses/android-sdk-preview-license

ADD packages.txt /sdk
RUN mkdir -p /root/.android && \
  touch /root/.android/repositories.cfg && \
  ${ANDROID_HOME}/tools/bin/sdkmanager --update 

RUN while read -r package; do PACKAGES="${PACKAGES}${package} "; done < /sdk/packages.txt && \
    yes | ${ANDROID_HOME}/tools/bin/sdkmanager ${PACKAGES}

RUN echo "Installing Yarn Deb Source" \
	&& curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - \
	&& echo "deb http://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list

RUN echo "Installing Node.JS" \
	&& curl -sL https://deb.nodesource.com/setup_12.x | bash -

ENV BUILD_PACKAGES git yarn nodejs build-essential imagemagick librsvg2-bin ruby ruby-dev wget libcurl4-openssl-dev
RUN echo "Installing Additional Libraries" \
	 && rm -rf /var/lib/gems \
	 && apt-get update && apt-get install $BUILD_PACKAGES -qqy --no-install-recommends

RUN echo "Installing Fastlane" \
	&& gem install fastlane badge -N \
	&& gem cleanup

ENV GRADLE_HOME /opt/gradle
ENV GRADLE_VERSION 5.4.1

RUN echo "Downloading Gradle" \
	&& wget --no-verbose --output-document=gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

RUN echo "Installing Gradle" \
	&& unzip gradle.zip \
	&& rm gradle.zip \
	&& mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
	&& ln --symbolic "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle

ENV LC_ALL "en_US.UTF-8"
ENV LANG "en_US.UTF-8"

WORKDIR /app