package view;

import com.bspl.ws.WS_CallDispatchCollectionKanhaApi;
import com.bspl.ws.WS_CallFarmerCollectionAegitekApi;
import com.bspl.ws.WS_CallFarmerCollectionKanhaApi;

import com.bspl.ws.WS_CallRMRDKanhaApi;
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
        classes.add(WS_CallFarmerCollectionAegitekApi.class);
        classes.add(WS_MasterDetails.class);
        classes.add(WS_FarmerTransaction.class);
        classes.add(WS_CallDispatchCollectionKanhaApi.class);
        classes.add(WS_CallRMRDKanhaApi.class);
        classes.add(WS_RmrdTrnsaction.class);
        classes.add(WS_CallFarmerCollectionKanhaApi.class);

        // Register provider classes.

        return classes;
    }
}
