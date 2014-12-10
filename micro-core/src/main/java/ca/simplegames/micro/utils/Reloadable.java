package ca.simplegames.micro.utils;

/**
 * Classes implementing this interface can be reloaded for updating their configurations on the fly
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-08-28 9:16 PM)
 */
public interface Reloadable {
  /**
   * Reload the class's configuration.
   */
  public void reload() throws Exception;
}
