package view;

import com.bspl.ws.WS_Transaction_DispatchCollection_Api;
import com.bspl.ws.WS_CallFarmerCollectionAegitekApi;
import com.bspl.ws.WS_Transaction_FarmerCollection_Api;
import com.bspl.ws.WS_CallRMRDAegitekApi;
import com.bspl.ws.WS_Transaction_RMRD_Api;
import com.bspl.ws.WS_MasterDetails;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
@ApplicationPath("resources")
public class GenericApplication extends Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        // Register root resources.
        classes.add(WS_CallFarmerCollectionAegitekApi.class);
        classes.add(WS_CallRMRDAegitekApi.class);
        classes.add(WS_MasterDetails.class);
        classes.add(WS_Transaction_DispatchCollection_Api.class);
        classes.add(WS_Transaction_RMRD_Api.class);
        classes.add(WS_Transaction_FarmerCollection_Api.class);
        // Register provider classes.
        return classes;
    }
}
