##(µ)Micro Archetypes

Micro's archetypes directory is a collection of Apache Maven project archetypes designed for the (µ)Micro framework.


### Requirements
To install and use these archetypes [Apache Maven](http://maven.apache.org) needs to be present.


### Getting started
Installation:

    cd archetypes/quickstart
    mvn install

### Usage

    mvn archetype:generate \
      -DarchetypeGroupId=ca.simplegames.micro\
      -DarchetypeArtifactId=micro-quickstart \
      -DarchetypeVersion=0.2.2 \
      -DgroupId=com.mycompany \
      -DartifactId=myproject

See: http://maven.apache.org/archetype/maven-archetype-plugin/plugin-info.html, for more details


### Launching Generated Application Using the embedded Jetty web server

    >cd myproject
    >mvn jetty:run

    Browse to http://localhost:8080/

### Using IDEA

Open IntelliJ. Choose File/Import/Existing Project and point it to myproject directory

### Notes

    keytool -selfcert -alias micro -genkey -keystore keystore -keyalg RSA  \
     -validity 3650 -storepass microsecret -keypass microsecret -dname "CN=simplegames.ca"
