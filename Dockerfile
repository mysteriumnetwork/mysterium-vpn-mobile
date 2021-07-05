FROM openjdk:11-jdk-buster

ENV ANDROID_COMPILE_SDK "29"
ENV NDK_VERSION "21.4.7075529"
ENV ANDROID_BUILD_TOOLS "30.0.3"

ENV ANDROID_HOME /android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/cmdline-tools/tools/bin
ENV PATH=$PATH:$ANDROID_HOME/platform-tools

# Install OS packages
RUN apt-get --quiet update --yes
RUN apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1 build-essential ruby ruby-dev
# We use this for xxd hex->binary
RUN apt-get --quiet install --yes vim-common

# Install Android CLI tools
RUN wget --quiet --output-document=android-commandlinetools.zip https://dl.google.com/android/repository/commandlinetools-linux-7302050_latest.zip
RUN mkdir -p $ANDROID_HOME/cmdline-tools
RUN unzip -q android-commandlinetools.zip -d "$ANDROID_HOME/cmdline-tools"
RUN mv $ANDROID_HOME/cmdline-tools/cmdline-tools $ANDROID_HOME/cmdline-tools/tools

# Accept Android SDK licenses
RUN yes | sdkmanager --licenses

# Install SDK packages for the build
RUN yes | sdkmanager --install "platforms;android-${ANDROID_COMPILE_SDK}"
RUN yes | sdkmanager --install "platform-tools"
RUN yes | sdkmanager --install "build-tools;${ANDROID_BUILD_TOOLS}"
RUN yes | sdkmanager --install "extras;android;m2repository"
RUN yes | sdkmanager --install "extras;google;m2repository"
RUN yes | sdkmanager --install "extras;google;google_play_services"
RUN yes | sdkmanager --install "ndk;${NDK_VERSION}"

# install Fastlane
COPY Gemfile.lock .
COPY Gemfile .
RUN gem install bundle
RUN bundle install
