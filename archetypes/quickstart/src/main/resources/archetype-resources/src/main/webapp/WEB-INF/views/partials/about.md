### About this app

This is a Micro web application created using the: `quickstart` [Maven archetype]().

This particular page is a [Markdown]() document, rendered from the `partials` repository.

Excerpt from the `WEB-INF/config/micro-config.yml` config file:

    repositories:
      content: {path: views/content, cache: memCache, config: config, default: true}
      partials: {path: views/partials, cache: partials, config: config}
      templates: {path: views/templates, config: config, cache: memCache}

Learn more about the Micro **repositories** here: [Repositories](http://micro-docs.simplegames.ca/repositories.md)

This page also demonstrates how you can take complete control over the method used to render the dynamic content. See the simple controller responsible for this. You can find it here: `WEB-INF/controllers/About.bsh` and it looks like this (excerpt):


    aboutContent=context.get("partials").get("markdown", "about.md");
    githubStyle = "<div class=\"github container\">"+aboutContent+"</div>";
    context.put("yield", githubStyle);
    defaultTemplate = context.get("templates").get("default.html");

    context.setRackResponse(RackResponseUtils.standardHtml(defaultTemplate));
    context.halt();

There is so much more to Micro, and you're encouraged to discover it by reading the [docs](http://micro-docs.simplegames.ca) or the [sources](https://github.com/florinpatrascu/micro).

If you find bugs or have new ideas about improving Micro, please let us know. Thank you!

Good luck!

