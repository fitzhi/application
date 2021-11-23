FROM mwizner/sonarqube:8.7.1-community
LABEL Frederic VIDAL (frederic.vidal@fitzhi.com)

RUN sed -i 's:#sonar.web.context=:sonar.web.context=/sonar:g' conf/sonar.properties
