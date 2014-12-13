package ${package};

import ca.simplegames.micro.Controller;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.controllers.ControllerException;

import java.util.Map;

/**
 * A simple Micro Controller
 *
 */
public class Hello implements Controller {
  @Override
  public void execute(MicroContext context, Map configuration) throws ControllerException {
    context.put("hello", getClass());
  }
}
