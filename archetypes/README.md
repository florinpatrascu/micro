##(µ)Micro Archetypes

Micro's archetypes directory is a collection of Apache Maven project archetypes designed for the (µ)Micro framework.

 - `quickstart`; archetype to build a small Micro web application having two "Hello" demo Controllers, and using two different Template Engines that can be used side by side for rendering the dynamic content; Velocity and Markdown.
 - `bare`; archetype to build a minimalist Micro web application

### Requirements
To install and use these archetypes [Apache Maven](http://maven.apache.org) needs to be present.


### Getting started
Installation:

    cd archetypes/quickstart
    # or:
    # cd archetypes/bare
    mvn install

### Usage from command line

    mvn archetype:generate \
      -DarchetypeGroupId=ca.simplegames.micro\
      -DarchetypeArtifactId=micro-quickstart \
      -DarchetypeVersion=0.2.2 \
      -DgroupId=com.mycompany \
      -DartifactId=myproject

or:

    mvn archetype:generate \
      -DarchetypeGroupId=ca.simplegames.micro\
      -DarchetypeArtifactId=bare \
      -DarchetypeVersion=0.2.2 \
      -DgroupId=com.mycompany \
      -DartifactId=myproject

### Launching the generated application using the embedded Jetty web server

    cd myproject
    mvn compile install exec:java

    #or: mvn exec:java, if you already built the project and you only need to restart the webapp

Browse to http://localhost:8080/

### Using IDEA

Open IntelliJ. Choose File/Import/Existing Project and point it to `myproject` directory

