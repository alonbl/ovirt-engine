package org.ovirt.engine.core.dal.dbbroker.auditloghandling;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.ovirt.engine.core.common.AuditLogType;
import org.ovirt.engine.core.compat.Guid;

public class EventKeyComposer {

    private EventKeyComposer() {
    }

    /**
     * Composes an object id from all log id's to identify uniquely each instance.
     *
     * @param event
     *            the event entity that its attributes will be used to created the key
     * @param logType
     *            the log type associated with the event
     * @return unique object id
     */
    public static String composeObjectId(AuditLogableBase event, AuditLogType logType) {
        final StringBuilder builder = new StringBuilder();

        compose(builder, "type", logType.toString());
        compose(builder, "sd", nullToEmptyString(event.getStorageDomainId()));
        compose(builder, "dc", nullToEmptyString(event.getStoragePoolId()));
        compose(builder, "user", nullToEmptyString(event.getUserId()));
        compose(builder, "cluster", event.getClusterId().toString());
        compose(builder, "vds", event.getVdsId().toString());
        compose(builder, "vm", emptyGuidToEmptyString(event.getVmId()));
        compose(builder, "template", emptyGuidToEmptyString(event.getVmTemplateId()));
        compose(builder, "customId", StringUtils.defaultString(event.getCustomId()));

        return builder.toString();
    }

    private static void compose(StringBuilder builder, String key, String value) {
        final char DELIMITER = ',';
        final char NAME_VALUE_SEPARATOR = '=';
        if (builder.length() > 0) {
            builder.append(DELIMITER);
        }

        builder.append(key).append(NAME_VALUE_SEPARATOR).append(value);
    }

    private static String emptyGuidToEmptyString(Guid guid) {
        return Guid.Empty.equals(guid) ? "" : guid.toString();
    }

    private static String nullToEmptyString(Object obj) {
        return Objects.toString(obj, "");
    }
}