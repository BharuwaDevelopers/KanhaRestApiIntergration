package view;

import com.bspl.ws.WS_CallFarmerCollectionKanhaApi;

import com.bspl.ws.WS_MasterDetails;
import com.bspl.ws.WS_FarmerTransaction;

import com.bspl.ws.WS_RmrdTrnsaction;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("resources")
public class GenericApplication extends Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        // Register root resources.
        classes.add(WS_MasterDetails.class);
        classes.add(WS_FarmerTransaction.class);
      
        classes.add(WS_CallFarmerCollectionKanhaApi.class);
        classes.add(WS_RmrdTrnsaction.class);

        // Register provider classes.

        return classes;
    }
}
