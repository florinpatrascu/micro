/*
 * Copyright (c)2014 Florin T.Pătraşcu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.simplegames.micro.viewers;

import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2014-11-29 4:28 PM)
 */
public class TemplateEngineWrapper {
  private ViewRenderer templateEngine;
  private final MicroContext context;
  private final SiteContext site;

  public TemplateEngineWrapper(MicroContext context) {
    this.context = context;
    this.site = context.getSiteContext();
  }

  public TemplateEngineWrapper(ViewRenderer templateEngine, MicroContext context) {
    this.templateEngine = templateEngine;
    this.context = context;
    this.site = context.getSiteContext();
  }

  public TemplateEngineWrapper use(String engineName) {
    return new TemplateEngineWrapper(site.getTemplateEnginesManager().getEngine(engineName), this.context);
  }

  public String evaluate(String text) {
    try {
      return templateEngine.evaluate(this.context, text);
    } catch (ViewException e) {
      e.printStackTrace();
      return e.getMessage();
    }
  }
}
