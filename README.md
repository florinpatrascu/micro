![](http://micro-docs.simplegames.ca/images/micro-logo.png)

**Micro**, for short: **(μ)** or **[Mu](http://en.wikipedia.org/wiki/Mu_\(letter\))**, is a modular Model View Controller framework ([MVC Pull](http://en.wikipedia.org/wiki/Web_application_framework#Push-based_vs._pull-based)) for web development, and it was designed with simplicity in mind. Compared with other Java web frameworks, Micro doesn't force you to use the Java language for creating dynamic content, nor does it pigment your code with Java [syntactic metadata](http://en.wikipedia.org/wiki/Java_annotation) or anything like that. With Micro you can start developing your web application right away even if the only content your site has is plain text or [Markdown](http://daringfireball.net/projects/markdown/) documents; you don't need Java for that. Micro uses Java under the hood, providing you the support that is specific to the web development: localization, template languages, scripting support for more advanced use, and a modular way to extend your dynamic content with controllers written in Java or using scripting, such as: [Beanshell](http://www.beanshell.org/), server side [Javascript(Rhino)](http://www.mozilla.org/rhino/), [JRuby](http://jruby.org/) and [more](http://commons.apache.org/bsf/).

We hope Micro will help you develop web applications while increasing the fun quotient of programming as well. Inspired from [Sinatra](http://www.sinatrarb.com/), Micro will help you create web applications in Java with very little effort. Before going forward please check the few **[prerequisites](http://micro-docs.simplegames.ca/misc/check_java.md)** and follow the simple steps there to prepare your environment for running Micro. 

#### Installing Micro
Micro can be downloaded from Github and you will need just a few commands to make it available to your console. For the examples below we presume you're in your user home folder: `~/`. Get the code:

        $ git clone https://github.com/florinpatrascu/micro

Build the framework:

        $ cd micro
        $ ant dist

Add the framework installation folder to your current path and define the `MICRO_HOME` environment variable. For OSX, this means editing your `~/.profile` file and adding the following: 

        $ export MICRO_HOME="~/micro"
        $ export PATH=$PATH:$MICRO_HOME/bin:

Reload your profile:

        $ source ~/.profile

Check if the Micro command line interface is available:

        $ cd
        $ micro -v

If everything is in place and properly installed, you should see: `Micro x.y.z`


#### Creating a new Micro web application
Micro provides a simple command line interface [CLI](http://micro-docs.simplegames.ca/cli.md) to help you create new applications, start the server and deploy the web application<sup>(1)</sup>. Provided you have installed the Micro command line tools properly, the following few commands will create a new micro web application and start Micro with the embedded [web server](http://docs.codehaus.org/display/JETTY/About+Jetty):

        $ micro new hello_world
        $ cd hello_world
        $ micro start

You will see something like this almost immediately:

         _ __ ___ ( ) ___ _ __ ___ 
        | '_ ` _ \| |/ __| '__/ _ \ 
        | | | | | | | (__| | | (_) |
        |_| |_| |_|_|\___|_|  \___/ 
        = a modular micro MVC Java framework

and you can visit your web application by pointing your browser to: [http://localhost:8080](http://localhost:8080)

We hope you'll enjoy writing web applications with **Micro**.

Thank you!    

### Documentation
The documentation is a work in progress and can be found here: [micro-docs.simplegames.ca](http://micro-docs.simplegames.ca). It is hosted at Heroku, using Micro itself for publishing. You can fork the documentation site and send pull requests. This is the Github repo for the docs: [micro-docs](https://github.com/florinpatrascu/micro-docs)

Feedback and contributions to the project, no matter what kind, are always very welcome. 

### Submitting an Issue
We use the [GitHub issue tracker](https://github.com/florinpatrascu/micro/issues) to track bugs and features. Before submitting a bug report or feature request, check to make sure it hasn't already been submitted. When submitting a bug report, please include a [Gist](https://gist.github.com/) that includes a stack trace and any other details that may be necessary to reproduce the bug, including your Java version and operating system. Ideally, a bug report should include a pull request with failing specs.

### Special thanks
  - to my [wife](http://twitter.com/simonuta), for understanding my endless passion for programming.
  - [JPublish.org](http://jpublish.org/) - a rusty but trusty framework. There are core concepts in Micro designed as continuations of the ideas developed for JPublish; Templates and Repositories, for example.
  - Many thanks to [Anthony Eden](https://github.com/aeden) for being an inspiring developer and a model for many of us.
  - Many thanks to [Frank Carver](https://github.com/efficacy) for contributing ideas to the [JRack](https://github.com/florinpatrascu/jrack), many of these being ported back into JRack and used by Micro.
  - [Spring framework](http://www.springsource.org/) - the localization support in Micro was extracted from selected classes originally developed for the early Spring framework.
  - [Apache Wink](http://en.wikipedia.org/wiki/Apache_Wink) - used as a future support for [JSR-311](http://www.jcp.org/en/jsr/detail?id=311).
  - to all our **contributors** and **supporters**. Cheers!

<hr>
<sub>*Notes:*</sub>

<sup>1</sup> - deploying applications from the command line not yet finalized.

### License

    Copyright (c) 2012-2013 Florin T.Pătraşcu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

