package org.ovirt.engine.core.bll.gluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.ovirt.engine.core.bll.AbstractQueryTest;
import org.ovirt.engine.core.bll.utils.GlusterUtil;
import org.ovirt.engine.core.common.action.gluster.GlusterVolumeRemoveBricksQueriesParameters;
import org.ovirt.engine.core.common.asynctasks.gluster.GlusterAsyncTask;
import org.ovirt.engine.core.common.asynctasks.gluster.GlusterTaskParameters;
import org.ovirt.engine.core.common.asynctasks.gluster.GlusterTaskType;
import org.ovirt.engine.core.common.businessentities.Cluster;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VDSStatus;
import org.ovirt.engine.core.common.businessentities.gluster.GlusterBrickEntity;
import org.ovirt.engine.core.common.businessentities.gluster.GlusterServer;
import org.ovirt.engine.core.common.businessentities.gluster.GlusterVolumeEntity;
import org.ovirt.engine.core.common.businessentities.gluster.GlusterVolumeTaskStatusDetail;
import org.ovirt.engine.core.common.businessentities.gluster.GlusterVolumeTaskStatusEntity;
import org.ovirt.engine.core.common.businessentities.gluster.GlusterVolumeTaskStatusForHost;
import org.ovirt.engine.core.common.businessentities.gluster.GlusterVolumeType;
import org.ovirt.engine.core.common.job.JobExecutionStatus;
import org.ovirt.engine.core.common.job.Step;
import org.ovirt.engine.core.common.vdscommands.VDSCommandType;
import org.ovirt.engine.core.common.vdscommands.VDSParametersBase;
import org.ovirt.engine.core.common.vdscommands.VDSReturnValue;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dao.ClusterDao;
import org.ovirt.engine.core.dao.StepDao;
import org.ovirt.engine.core.dao.VdsDao;
import org.ovirt.engine.core.dao.gluster.GlusterServerDao;
import org.ovirt.engine.core.dao.gluster.GlusterVolumeDao;

public class GetGlusterVolumeRemoveBricksStatusQueryTest extends
        AbstractQueryTest<GlusterVolumeRemoveBricksQueriesParameters, GetGlusterVolumeRemoveBricksStatusQuery<GlusterVolumeRemoveBricksQueriesParameters>> {

    private static final Guid CLUSTER_ID = new Guid("b399944a-81ab-4ec5-8266-e19ba7c3c9d1");
    private static final String SERVER_1 = "server1";
    private static final String SERVER_2 = "server2";
    private static final Guid VOLUME_ID = Guid.newGuid();
    private static final Guid SERVER_ID = Guid.newGuid();
    private static final Guid STEP_ID = Guid.newGuid();
    private static final Guid SERVER_UUID_1 = Guid.newGuid();
    private GlusterVolumeTaskStatusEntity expectedVolumeStatusDetails;

    @Mock
    private VdsDao vdsDao;

    @Mock
    private GlusterVolumeDao volumeDao;

    @Mock
    private ClusterDao clusterDao;

    @Mock
    private StepDao stepDao;

    @Mock
    private GlusterServerDao glusterServerDao;

    @Mock
    private GlusterUtil glusterUtils;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setupExpectedVolume();
        setupMock();
    }

    private void setupExpectedVolume() {
        expectedVolumeStatusDetails = new GlusterVolumeTaskStatusEntity();
        expectedVolumeStatusDetails.setHostwiseStatusDetails(getHostwiseStatusDetails());
        expectedVolumeStatusDetails.setStatusSummary(getStatusSummary());
        expectedVolumeStatusDetails.setStartTime(new Date());
        expectedVolumeStatusDetails.setStatusTime(new Date());
    }

    private List<GlusterVolumeTaskStatusForHost> getHostwiseStatusDetails() {
        List<GlusterVolumeTaskStatusForHost> statusList = new ArrayList<>();

        GlusterVolumeTaskStatusForHost status1 = new GlusterVolumeTaskStatusForHost();
        status1.setHostName(SERVER_1);
        status1.setHostUuid(Guid.newGuid());
        status1.setFilesScanned(100);
        status1.setFilesMoved(100);
        status1.setFilesFailed(0);
        status1.setFilesScanned(100);
        status1.setFilesSkipped(0);
        status1.setRunTime(20);
        status1.setStatus(JobExecutionStatus.FINISHED);
        status1.setTotalSizeMoved(1024);
        status1.setHostUuid(SERVER_UUID_1);
        statusList.add(status1);

        GlusterVolumeTaskStatusForHost status2 = new GlusterVolumeTaskStatusForHost();
        status2.setHostName(SERVER_2);
        status2.setHostUuid(Guid.newGuid());
        status2.setFilesScanned(100);
        status2.setFilesMoved(100);
        status2.setFilesFailed(0);
        status2.setFilesScanned(100);
        status2.setFilesSkipped(0);
        status2.setRunTime(20);
        status2.setStatus(JobExecutionStatus.FINISHED);
        status2.setTotalSizeMoved(1024);
        status2.setHostUuid(SERVER_UUID_1);
        statusList.add(status2);

        return statusList;
    }

    private GlusterVolumeTaskStatusDetail getStatusSummary() {
        GlusterVolumeTaskStatusDetail summary = new GlusterVolumeTaskStatusDetail();
        summary.setFilesMoved(200);
        summary.setFilesFailed(0);
        summary.setFilesScanned(200);
        summary.setFilesSkipped(0);
        summary.setRunTime(40);
        summary.setStatus(JobExecutionStatus.FINISHED);
        summary.setTotalSizeMoved(2048);

        return summary;
    }

    private List<GlusterBrickEntity> getBricks() {
        GlusterBrickEntity brick1 = new GlusterBrickEntity();
        brick1.setId(Guid.newGuid());
        brick1.setServerId(SERVER_ID);
        brick1.setVolumeId(VOLUME_ID);

        GlusterBrickEntity brick2 = new GlusterBrickEntity();
        brick2.setId(Guid.newGuid());
        brick2.setServerId(SERVER_ID);
        brick2.setVolumeId(VOLUME_ID);

        List<GlusterBrickEntity> bricksList = new ArrayList<>();
        bricksList.add(brick1);
        bricksList.add(brick2);

        return bricksList;
    }

    private GlusterVolumeEntity getVolume() {
        GlusterVolumeEntity volume = new GlusterVolumeEntity();
        volume.setName("Vol1");
        volume.setId(VOLUME_ID);
        volume.setReplicaCount(2);
        volume.setBricks(getBricks());
        volume.setVolumeType(GlusterVolumeType.DISTRIBUTE);
        volume.setAsyncTask(getAsyncTask());
        return volume;
    }

    private VDS getVds(VDSStatus status) {
        VDS vds = new VDS();
        vds.setId(Guid.newGuid());
        vds.setVdsName("gfs1");
        vds.setClusterId(CLUSTER_ID);
        vds.setStatus(status);
        return vds;
    }

    private Cluster getCluster() {
        Cluster cluster = new Cluster();
        cluster.setId(CLUSTER_ID);
        return cluster;
    }

    private List<Step> getStepsList() {
        List<Step> stepsList = new ArrayList<>();
        Step stp = new Step();
        stp.setId(STEP_ID);
        stp.setStartTime(new Date());
        stepsList.add(stp);

        return stepsList;
    }

    private GlusterAsyncTask getAsyncTask() {
        GlusterAsyncTask asyncTask = new GlusterAsyncTask();
        asyncTask.setStepId(STEP_ID);
        asyncTask.setType(GlusterTaskType.REBALANCE);
        asyncTask.setStatus(JobExecutionStatus.FINISHED);
        asyncTask.setMessage("test_msg");
        asyncTask.setTaskId(Guid.newGuid());
        GlusterTaskParameters params = new GlusterTaskParameters();
        params.setVolumeName("volume1");
        asyncTask.setTaskParameters(params);

        return asyncTask;
    }

    private GlusterServer getGlusterServer() {
        GlusterServer glusterServer = new GlusterServer();
        glusterServer.setId(SERVER_ID);
        glusterServer.setGlusterServerUuid(SERVER_UUID_1);

        return glusterServer;
    }

    private void setupMock() {
        doReturn(CLUSTER_ID).when(getQueryParameters()).getClusterId();
        doReturn(VOLUME_ID).when(getQueryParameters()).getVolumeId();
        when(volumeDao.getById(VOLUME_ID)).thenReturn(getVolume());
        when(stepDao.getStepsByExternalId(any(Guid.class))).thenReturn(getStepsList());
        when(glusterServerDao.getByGlusterServerUuid(any(Guid.class))).thenReturn(getGlusterServer());
        when(vdsDao.get(any(Guid.class))).thenReturn(getVds(VDSStatus.Up));
        when(clusterDao.get(any(Guid.class))).thenReturn(getCluster());

        VDSReturnValue returnValue = new VDSReturnValue();
        returnValue.setSucceeded(true);
        returnValue.setReturnValue(expectedVolumeStatusDetails);

        doReturn(returnValue).when(getQuery()).runVdsCommand(eq(VDSCommandType.GetGlusterVolumeRemoveBricksStatus),
                any(VDSParametersBase.class));
    }

    @Test
    public void testQueryForStatusDetails() {
        doReturn(VOLUME_ID).when(getQueryParameters()).getVolumeId();
        when(vdsDao.get(SERVER_ID)).thenReturn(getVds(VDSStatus.Up));
        when(glusterUtils.getUpServer(CLUSTER_ID)).thenReturn(getVds(VDSStatus.Up));

        getQuery().executeQueryCommand();
        GlusterVolumeTaskStatusEntity volumeStatusDetails = getQuery().getQueryReturnValue().getReturnValue();

        assertNotNull(volumeStatusDetails);
        assertEquals(expectedVolumeStatusDetails, volumeStatusDetails);

        verify(volumeDao, times(1)).getById(VOLUME_ID);
    }

    @Test(expected = RuntimeException.class)
    public void testQueryForInvalidVolumeId() {
        doReturn(Guid.Empty).when(getQueryParameters()).getVolumeId();

        getQuery().executeQueryCommand();
    }
}
