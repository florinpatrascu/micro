![](http://micro-docs.simplegames.ca/images/micro-logo.png)

**Micro**, for short: **(μ)** or **[Mu](http://en.wikipedia.org/wiki/Mu_\(letter\))**, is a modular Model View Controller framework ([MVC Pull](http://en.wikipedia.org/wiki/Web_application_framework#Push-based_vs._pull-based)) for rapid web development. Flexible and powerful at the same time, Micro lets you choose your tools from a decently sized pool of well known products:

 - [Velocity](http://micro-docs.simplegames.ca/views/engines.md#Velocity), [Markdown](http://micro-docs.simplegames.ca/views/engines.md#Markdown), [Freemarker](http://micro-docs.simplegames.ca/views/engines.md#Freemarker), [Mustache (java)](http://micro-docs.simplegames.ca/views/engines.md#Mustache), [Stringtemplate](http://micro-docs.simplegames.ca/views/engines.md#StringTemplate); for designing the dynamic content of your web pages.
 - [Beanshell](http://www.beanshell.org/), server side [Javascript(Rhino)](http://www.mozilla.org/rhino/), [JRuby](http://jruby.org/) and [more](http://commons.apache.org/bsf/); for prototyping controllers before converting them to Java classes, if need be.

#### Using Micro

The easiest way to start developing with Micro is to download the source code from [Github](https://github.com/florinpatrascu/micro)

    git clone https://github.com/florinpatrascu/micro

Build it:

    cd micro
    mvn clean install test -Dtest=MicroGenericTest
    # or:
    mvn clean install -DskipTests=true  # to skip the tests

#### Creating a new Micro web application
Create a new webapp using the [Micro quickstart Maven archetype](archetypes/README.md)

    cd archetypes/quickstart
    mvn install

#### Usage from command line

    mvn archetype:generate \
      -DarchetypeGroupId=ca.simplegames.micro\
      -DarchetypeArtifactId=micro-quickstart \
      -DarchetypeVersion=0.2.2 \
      -DgroupId=com.mycompany \
      -DartifactId=myproject

#### Usage from [IntelliJ IDEA](https://www.jetbrains.com/idea/)
Open IntelliJ. Choose `File/Import/Existing Project` and point it to `myproject` directory. Or, you can add the `quickstart` archetype in IntelliJ by simply following the few next steps:

- `File/New Project`
- select `Create from archetype` and click the `Add Archetype` button
- choose `ca.simplegames.micro:micro-quickstart` from the list

And simply follow the dialog prompted by IntelliJ :)

#### Maven depedency

```` xml
  <dependencies>
    <dependency>
      <groupId>ca.simplegames.micro</groupId>
      <artifactId>micro-core</artifactId>
      <version>0.2.2</version>
    </dependency>
  </dependencies>
````

### Start your web application from command line
Launching the generated application using the embedded Jetty web server is very easy:

    cd myproject
    mvn compile install exec:java

    #or: mvn exec:java
You can also easily start your web application from IntelliJ.


When the Micro web app is started, you will almost immediately see something like this:

         _ __ ___ ( ) ___ _ __ ___
        | '_ ` _ \| |/ __| '__/ _ \
        | | | | | | | (__| | | (_) |
        |_| |_| |_|_|\___|_|  \___/
        = a modular micro MVC Java framework

and you can visit your web application by pointing your browser to: [http://localhost:8080](http://localhost:8080)

We hope Micro will help you develop web applications while increasing the fun quotient of programming as well.

Have fun!
µ

### Documentation
The documentation is a work in progress and can be found here: [micro-docs.simplegames.ca](http://micro-docs.simplegames.ca). It is hosted at Heroku, using Micro itself for publishing. You can fork the documentation site and send pull requests. This is the Github repo for the docs: [micro-docs](https://github.com/florinpatrascu/micro-docs)

Feedback and contributions to the project, no matter what kind, are always very welcome.

### Submitting an Issue
We use the [GitHub issue tracker](https://github.com/florinpatrascu/micro/issues) to track bugs and features. Before submitting a bug report or feature request, check to make sure it hasn't already been submitted. When submitting a bug report, please include a [Gist](https://gist.github.com/) that includes a stack trace and any other details that may be necessary to reproduce the bug, including your Java version and operating system. Ideally, a bug report should include a pull request with failing specs.

### Special thanks
  - to my [wife](http://twitter.com/simonuta), for understanding my endless passion for programming.
  - [JPublish.org](http://jpublish.org/) - a rusty but trusty framework. There are core concepts in Micro designed as continuations of the ideas developed for JPublish; Templates and Repositories, for example.
  - Many thanks to [Anthony Eden](https://github.com/aeden) for being an inspiring developer and a model for many of us.
  - Many thanks to [Frank Carver](https://github.com/efficacy) for contributing ideas to the [JRack](https://github.com/florinpatrascu/jrack), many of these being ported back into JRack and used by Micro.<p></p>
  - **[JetBrains](http://www.jetbrains.com/)** is kindly supporting open source projects with its **full-featured IntelliJ IDEA**. JetBrains is the world's leading vendor of professional development tools for **Java**, **Ruby** and **.NET** applications. Take a look at JetBrains software products: [**http://www.jetbrains.com/products.html**](http://www.jetbrains.com/products.html)<p></p>
  - [Spring framework](http://www.springsource.org/) - the localization support in Micro was extracted from selected classes originally developed for the early Spring framework.
  - [Apache Wink](http://en.wikipedia.org/wiki/Apache_Wink) - used as a future support for [JSR-311](http://www.jcp.org/en/jsr/detail?id=311).
  - to all our **contributors** and **supporters**. Cheers!

<hr>

### License

    Copyright (c) 2012-2015 Florin T.Pătraşcu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

