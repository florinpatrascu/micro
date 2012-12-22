package ca.simplegames.micro.helpers;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.Helper;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 10:33 PM)
 */
public class HelperManager {
    private Logger log = LoggerFactory.getLogger(getClass());

    private SiteContext site;
    private List<Helper> helpers = new ArrayList<Helper>();
    private Map<String, Helper> helpersMap = new HashMap<String, Helper>();
    private List<Helper> beforeHelpers = new ArrayList<Helper>();
    private List<Helper> afterHelpers = new ArrayList<Helper>();

    public HelperManager(SiteContext site) {
        this.site = site;
        List<Object> helpersConfig = (List<Object>) site.getAppConfig()
                .get(Globals.MICRO_HELPERS_CONFIG);

        if(!CollectionUtils.isEmpty(helpersConfig)){
            //load helpers from config
            log.warn("TODO: implement me - ca.simplegames.micro.helpers.HelperManager#HelperManager");
        }
    }

    public Helper addHelper(Map<String, Object> model){
        if(model!= null && !model.isEmpty()){
            Helper helper = null;

            return addHelper(helper);
        }else{
            return null;
        }
    }

    public Helper addHelper(Helper helper){
        if(helper!= null){
            if(helper.isBefore()){
                beforeHelpers.add(helper);
            }else if(helper.isAfter()){
                afterHelpers.add(helper);
            }else{
                helpers.add(helper);
            }
            helpersMap.put(helper.getName().toLowerCase(), helper);
        }
        return helper;
    }

    public List<Helper> getHelpers(){
        return helpers;
    }

    public Map<String, Helper> getHelpersMap() {
        return helpersMap;
    }

    public List<Helper> getBeforeHelpers() {
        return beforeHelpers;
    }

    public List<Helper> getAfterHelpers() {
        return afterHelpers;
    }
}
