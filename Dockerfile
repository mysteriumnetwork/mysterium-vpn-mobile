FROM openjdk:8-jdk

# Just matched `app/build.gradle`
ENV ANDROID_COMPILE_SDK "28"
# Just matched `app/build.gradle`
ENV ANDROID_BUILD_TOOLS "28.0.3"
# Version from https://developer.android.com/studio/releases/sdk-tools
ENV ANDROID_SDK_TOOLS "4333796"

ENV ANDROID_HOME /android-sdk-linux
ENV PATH="${PATH}:/android-sdk-linux/platform-tools/"

# Install OS packages
RUN apt-get --quiet update --yes
RUN apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1 build-essential ruby ruby-dev
# We use this for xxd hex->binary
RUN apt-get --quiet install --yes vim-common
# Install Android SDK
RUN wget --quiet --output-document=android-sdk.zip https://dl.google.com/android/repository/sdk-tools-linux-${ANDROID_SDK_TOOLS}.zip
RUN unzip -q android-sdk.zip -d "$ANDROID_HOME/"
# Accept Android SDK licenses
RUN yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses

RUN echo y | android-sdk-linux/tools/android update sdk --no-ui --all --filter android-${ANDROID_COMPILE_SDK}
RUN echo y | android-sdk-linux/tools/android update sdk --no-ui --all --filter platform-tools
RUN echo y | android-sdk-linux/tools/android update sdk --no-ui --all --filter build-tools-${ANDROID_BUILD_TOOLS}
RUN echo y | android-sdk-linux/tools/android update sdk --no-ui --all --filter extra-android-m2repository
RUN echo y | android-sdk-linux/tools/android update sdk --no-ui --all --filter extra-google-google_play_services
RUN echo y | android-sdk-linux/tools/android update sdk --no-ui --all --filter extra-google-m2repository
# install Fastlane
COPY Gemfile.lock .
COPY Gemfile .
RUN gem install bundle
RUN bundle install
