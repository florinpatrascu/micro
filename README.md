           _
     _ __ (_)__ _ _ ___
    | '  \| / _| '_/ _ \
    |_|_|_|_\__|_| \___/
    = a modular micro MVC Java framework

Micro is a modular Model View Controller framework for web development, and it was designed with simplicity in mind. I hope Micro will help you develop web applications while increasing the fun quotient of programming as well.

(the documentation is work in progress)

temporary **Note** about starting Micro with the embedded jetty server. This note will evolve into the proper form as soon as the documentation is finalized.

    $ cd micro
    $ ant dist
    $ ./bin/server examples/blog/

The commands above will start Jetty on 127.0.0.1:8080 and will instantiate the example web application. Mind you all of the above will change, this is for quickly running Micro against a folder containing a Micro compatible web application.   

With the server running, point your browser to: `http://localhost:8080/readme.md`

The so called `blog` web app is not a blog, it is mostly a test app. The Blog will be created from a Micro template and the current `blog` web app will be erased before the release.
