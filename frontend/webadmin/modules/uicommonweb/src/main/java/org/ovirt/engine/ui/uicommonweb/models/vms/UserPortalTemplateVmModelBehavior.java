package org.ovirt.engine.ui.uicommonweb.models.vms;

import java.util.Arrays;
import java.util.List;

import org.ovirt.engine.core.common.businessentities.Cluster;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VmTemplate;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.ui.frontend.AsyncQuery;
import org.ovirt.engine.ui.uicommonweb.dataprovider.AsyncDataProvider;

public class UserPortalTemplateVmModelBehavior extends TemplateVmModelBehavior {

    public UserPortalTemplateVmModelBehavior(VmTemplate template) {
        super(template);
    }

    @Override
    public void initialize() {
        super.initialize();

        // The custom properties tab should be hidden on the User Portal
        getModel().setIsCustomPropertiesTabAvailable(false);
    }

    /**
     * Fills the default host according to the selected host set in webadmin. Since this value can be set only in
     * webadmin and can be set also to host, which is not visible to the user in userportal, this fakes the VDS value in
     * a way, that the rest of the code can use it normally and send it back to the server as-is (like Null Object
     * Pattern).
     */
    @Override
    protected void doChangeDefaultHost(List<Guid> hostGuids) {
        if (hostGuids != null && hostGuids.size() == 1) {
            VDS vds = new VDS();
            vds.setId(hostGuids.get(0));
            getModel().getDefaultHost().setItems(Arrays.asList(vds));
        }

        super.doChangeDefaultHost(hostGuids);
    }

    @Override
    protected void getHostListByCluster(Cluster cluster, AsyncQuery<List<VDS>> query) {
        AsyncDataProvider.getInstance().getHostListByClusterId(query, cluster.getId());
    }
}
