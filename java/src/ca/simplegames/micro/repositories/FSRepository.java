package ca.simplegames.micro.repositories;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.cache.MicroCache;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 6:42 PM)
 */
public class FSRepository extends Repository {
    protected FSRepository(String name, MicroCache cache, SiteContext site, String path) {
        super(name, cache, site, path);
    }

    @Override
    public InputStream getInputStream(String name) {
        try {
            return new ByteArrayInputStream(read(name).getBytes(Globals.UTF8));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
