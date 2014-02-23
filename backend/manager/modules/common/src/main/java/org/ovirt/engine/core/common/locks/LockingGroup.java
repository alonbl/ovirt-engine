package org.ovirt.engine.core.common.locks;

public enum LockingGroup {

    POOL,
    VDS,
    VDS_INIT,
    VDS_FENCE,
    VM,
    TEMPLATE,
    DISK,
    VM_DISK_BOOT,
    VM_NAME,
    NETWORK,
    STORAGE,
    STORAGE_CONNECTION,
    REGISTER_VDS,
    VM_SNAPSHOTS,
    GLUSTER,
    USER_VM_POOL,
    /** This group is used to lock template which is in export domain */
    REMOTE_TEMPLATE,
    /** This group is used to lock VM which is in export domain */
    REMOTE_VM,
    /** This group is used for indication that an operation is executed using the specified host */
    VDS_EXECUTION;

}
