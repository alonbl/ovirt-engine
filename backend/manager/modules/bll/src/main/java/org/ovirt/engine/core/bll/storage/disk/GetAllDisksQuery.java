package org.ovirt.engine.core.bll.storage.disk;

import javax.inject.Inject;

import org.ovirt.engine.core.bll.QueriesCommandBase;
import org.ovirt.engine.core.bll.context.EngineContext;
import org.ovirt.engine.core.common.queries.QueryParametersBase;
import org.ovirt.engine.core.dao.DiskDao;

public class GetAllDisksQuery<P extends QueryParametersBase> extends QueriesCommandBase<P> {
    @Inject
    private DiskDao diskDao;

    public GetAllDisksQuery(P parameters, EngineContext engineContext) {
        super(parameters, engineContext);
    }

    @Override
    protected void executeQueryCommand() {
        getQueryReturnValue().setReturnValue(diskDao.getAll(getUserID(), getParameters().isFiltered()));
    }
}
