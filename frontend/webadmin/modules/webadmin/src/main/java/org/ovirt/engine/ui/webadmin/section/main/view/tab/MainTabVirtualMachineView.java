package org.ovirt.engine.ui.webadmin.section.main.view.tab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.ovirt.engine.core.common.businessentities.GraphicsInfo;
import org.ovirt.engine.core.common.businessentities.GraphicsType;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.businessentities.VMStatus;
import org.ovirt.engine.core.searchbackend.VmConditionFieldAutoCompleter;
import org.ovirt.engine.ui.common.idhandler.ElementIdHandler;
import org.ovirt.engine.ui.common.uicommon.model.MainModelProvider;
import org.ovirt.engine.ui.common.widget.action.ActionButtonDefinition;
import org.ovirt.engine.ui.common.widget.action.CommandLocation;
import org.ovirt.engine.ui.common.widget.action.DropdownActionButton;
import org.ovirt.engine.ui.common.widget.action.UiCommandButtonDefinition;
import org.ovirt.engine.ui.common.widget.table.cell.Cell;
import org.ovirt.engine.ui.common.widget.table.cell.StatusCompositeCell;
import org.ovirt.engine.ui.common.widget.table.column.AbstractColumn;
import org.ovirt.engine.ui.common.widget.table.column.AbstractEnumColumn;
import org.ovirt.engine.ui.common.widget.table.column.AbstractLinkColumn;
import org.ovirt.engine.ui.common.widget.table.column.AbstractTextColumn;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicommonweb.models.vms.UnitVmModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.VmListModel;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.gin.AssetProvider;
import org.ovirt.engine.ui.webadmin.section.main.presenter.tab.MainTabVirtualMachinePresenter;
import org.ovirt.engine.ui.webadmin.section.main.view.AbstractMainTabWithDetailsTableView;
import org.ovirt.engine.ui.webadmin.widget.action.WebAdminButtonDefinition;
import org.ovirt.engine.ui.webadmin.widget.action.WebAdminImageButtonDefinition;
import org.ovirt.engine.ui.webadmin.widget.table.column.AbstractUptimeColumn;
import org.ovirt.engine.ui.webadmin.widget.table.column.ColumnResizeTableLineChartProgressBar;
import org.ovirt.engine.ui.webadmin.widget.table.column.CommentColumn;
import org.ovirt.engine.ui.webadmin.widget.table.column.ImportProgressColumn;
import org.ovirt.engine.ui.webadmin.widget.table.column.MigrationProgressColumn;
import org.ovirt.engine.ui.webadmin.widget.table.column.ReasonColumn;
import org.ovirt.engine.ui.webadmin.widget.table.column.VmStatusColumn;
import org.ovirt.engine.ui.webadmin.widget.table.column.VmTypeColumn;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.inject.Inject;

public class MainTabVirtualMachineView extends AbstractMainTabWithDetailsTableView<VM, VmListModel<Void>>
    implements MainTabVirtualMachinePresenter.ViewDef {

    private final EventBus eventBus;

    interface ViewIdHandler extends ElementIdHandler<MainTabVirtualMachineView> {
        ViewIdHandler idHandler = GWT.create(ViewIdHandler.class);
    }

    private static final ApplicationConstants constants = AssetProvider.getConstants();

    @Inject
    public MainTabVirtualMachineView(MainModelProvider<VM, VmListModel<Void>> modelProvider, EventBus eventBus) {
        super(modelProvider);

        this.eventBus = eventBus;

        ViewIdHandler.idHandler.generateAndSetIds(this);
        initTable();
        initWidget(getTable());
    }

    void initTable() {
        getTable().enableColumnResizing();

        VmStatusColumn<VM> statusIconColumn = new VmStatusColumn<>();
        statusIconColumn.setContextMenuTitle(constants.statusIconVm());
        statusIconColumn.makeSortable(VmConditionFieldAutoCompleter.STATUS);
        getTable().addColumn(statusIconColumn, constants.empty(), "35px"); //$NON-NLS-1$

        VmTypeColumn typeColumn = new VmTypeColumn();
        typeColumn.setContextMenuTitle(constants.typeVm());
        typeColumn.makeSortable(VmConditionFieldAutoCompleter.TYPE);
        getTable().addColumn(typeColumn, constants.empty(), "60px"); //$NON-NLS-1$

        AbstractTextColumn<VM> nameColumn = new AbstractLinkColumn<VM>(new FieldUpdater<VM, String>() {

            @Override
            public void update(int index, VM vm, String value) {
                //The link was clicked, now fire an event to switch to details.
                transitionHandler.handlePlaceTransition();
            }

        }) {
            @Override
            public String getValue(VM object) {
                return object.getName();
            }
        };
        nameColumn.makeSortable(VmConditionFieldAutoCompleter.NAME);
        getTable().addColumn(nameColumn, constants.nameVm(), "120px"); //$NON-NLS-1$

        CommentColumn<VM> commentColumn = new CommentColumn<>();
        getTable().addColumnWithHtmlHeader(commentColumn,
                SafeHtmlUtils.fromSafeConstant(constants.commentLabel()),
                "75px"); //$NON-NLS-1$

        AbstractTextColumn<VM> hostColumn = new AbstractLinkColumn<VM>(new FieldUpdater<VM, String>(){

            @Override
            public void update(int index, VM vm, String value) {
                //The link was clicked, now find cluster and switch to it.
                getModelProvider().getModel().setSearchString("vms: host = " + value + " and status=up"); // $NON-NLS-1$ $NON-NLS-2$
                getModelProvider().getModel().search();
            }

        }) {
            @Override
            public String getValue(VM object) {
                return object.getRunOnVdsName();
            }
        };

        hostColumn.makeSortable(VmConditionFieldAutoCompleter.HOST);
        getTable().addColumn(hostColumn, constants.hostVm(), "120px"); //$NON-NLS-1$

        AbstractTextColumn<VM> ipColumn = new AbstractTextColumn<VM>() {
            @Override
            public String getValue(VM object) {
                return object.getIp();
            }
        };
        ipColumn.makeSortable(VmConditionFieldAutoCompleter.IP);
        getTable().addColumn(ipColumn, constants.ipVm(), "120px"); //$NON-NLS-1$

        AbstractTextColumn<VM> fqdnColumn = new AbstractTextColumn<VM>() {
            @Override
            public String getValue(VM object) {
                return object.getFqdn();
            }

        };
        fqdnColumn.makeSortable(VmConditionFieldAutoCompleter.FQDN);
        getTable().addColumn(fqdnColumn, constants.fqdn(), "120px"); //$NON-NLS-1$

        AbstractTextColumn<VM> clusterColumn = new AbstractLinkColumn<VM>(new FieldUpdater<VM, String>(){

            @Override
            public void update(int index, VM vm, String value) {
                //The link was clicked, now find cluster and switch to it.
                getModelProvider().getModel().setSearchString("vms: cluster = " + value); // $NON-NLS-1$
            }

        }) {
            @Override
            public String getValue(VM object) {
                return object.getClusterName();
            }
        };

        clusterColumn.makeSortable(VmConditionFieldAutoCompleter.CLUSTER);
        getTable().addColumn(clusterColumn, constants.clusterVm(), "120px"); //$NON-NLS-1$

        AbstractTextColumn<VM> dcColumn = new AbstractTextColumn<VM>() {
            @Override
            public String getValue(VM object) {
                return object.getStoragePoolName();
            }
        };
        dcColumn.makeSortable(VmConditionFieldAutoCompleter.DATACENTER);
        getTable().addColumn(dcColumn, constants.dcVm(), "120px"); //$NON-NLS-1$

        getTable().addColumn(new ColumnResizeTableLineChartProgressBar<VM>(
                getTable(),
                VmConditionFieldAutoCompleter.MEM_USAGE) {
            @Override
            protected List<Integer> getProgressValues(VM object) {
                return object.getMemoryUsageHistory();
            }
        }, constants.memoryVm(), "80px"); //$NON-NLS-1$

        getTable().addColumn(new ColumnResizeTableLineChartProgressBar<VM>(
                getTable(),
                VmConditionFieldAutoCompleter.CPU_USAGE) {
            @Override
            protected List<Integer> getProgressValues(VM object) {
                return object.getCpuUsageHistory();
            }
        }, constants.cpuVm(), "80px"); //$NON-NLS-1$

        getTable().addColumn(new ColumnResizeTableLineChartProgressBar<VM>(
                getTable(),
                VmConditionFieldAutoCompleter.NETWORK_USAGE) {
            @Override
            protected List<Integer> getProgressValues(VM object) {
                return object.getNetworkUsageHistory();
            }
        }, constants.networkVm(), "80px"); //$NON-NLS-1$

        AbstractTextColumn<VM> graphicsColumn = new AbstractEnumColumn<VM, UnitVmModel.GraphicsTypes>() {
            @Override
            protected UnitVmModel.GraphicsTypes getRawValue(VM vm) {
                if ((vm.getStatus() == VMStatus.Down) || (vm.getStatus() == VMStatus.ImageLocked)) {
                    return UnitVmModel.GraphicsTypes.NONE;
                }

                Map<GraphicsType, GraphicsInfo> graphicsInfos = vm.getGraphicsInfos();
                return UnitVmModel.GraphicsTypes.fromGraphicsTypes(graphicsInfos.keySet());
            }
        };
        getTable().addColumn(graphicsColumn, constants.graphicsVm(), "70px"); //$NON-NLS-1$

        AbstractTextColumn<VM> statusColumn = new AbstractEnumColumn<VM, VMStatus>() {
            @Override
            public VMStatus getRawValue(VM object) {
                return object.getStatus();
            }

            @Override
            public String getValue(VM vm) {
                if (vm.getStatus() == VMStatus.MigratingFrom) {
                    // will be rendered by progress column
                    return null;
                }

                if (vm.getBackgroundOperationDescription() != null) {
                    // will be rendered by progress column
                    return null;
                }

                return super.getValue(vm);
            }
        };

        MigrationProgressColumn migrationProgressColumn = new MigrationProgressColumn();
        ImportProgressColumn importProgressColumn = new ImportProgressColumn();

        ReasonColumn<VM> reasonColumn = new ReasonColumn<VM>() {

            @Override
            protected String getReason(VM value) {
                return value.getStopReason();
            }

        };

        List<HasCell<VM, ?>> list = new ArrayList<>();
        list.add(statusColumn);
        list.add(reasonColumn);
        list.add(migrationProgressColumn);
        list.add(importProgressColumn);

        Cell<VM> compositeCell = new StatusCompositeCell<>(list);

        AbstractColumn<VM, VM> statusTextColumn = new AbstractColumn<VM, VM>(compositeCell) {
            @Override
            public VM getValue(VM object) {
                return object;
            }

            @Override
            public SafeHtml getTooltip(VM value) {
                String stopReason = value.getStopReason();
                if (stopReason != null && !stopReason.trim().isEmpty()) {
                    return SafeHtmlUtils.fromString(stopReason);
                }
                return null;
            }

        };
        statusTextColumn.makeSortable(VmConditionFieldAutoCompleter.STATUS);
        getTable().addColumn(statusTextColumn, constants.statusVm(), "120px"); //$NON-NLS-1$

        AbstractTextColumn<VM> uptimeColumn = new AbstractUptimeColumn<VM>() {
            @Override
            public Double getRawValue(VM object) {
                return object.getElapsedTime();
            }
        };
        uptimeColumn.makeSortable(VmConditionFieldAutoCompleter.UPTIME);
        getTable().addColumn(uptimeColumn, constants.uptimeVm(), "100px"); //$NON-NLS-1$

        AbstractTextColumn<VM> descriptionColumn = new AbstractTextColumn<VM>() {
            @Override
            public String getValue(VM object) {
                return object.getDescription();
            }
        };
        descriptionColumn.makeSortable(VmConditionFieldAutoCompleter.DESCRIPTION);
        getTable().addColumn(descriptionColumn, constants.description(), "150px"); //$NON-NLS-1$

        //
        // Buttons/menu items
        //
        addButtonToActionGroup(
                getTable().addActionButton(new WebAdminButtonDefinition<VM>(constants.newVm()) {

            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getNewVmCommand();
            }
        }));

        addMenuItemToKebab(
                getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.restoreVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getImportVmCommand();
            }
        }));
        addButtonToActionGroup(
                getTable().addActionButton(new WebAdminButtonDefinition<VM>(constants.editVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getEditCommand();
            }
        }));
        addMenuItemToKebab(
                getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.removeVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getRemoveCommand();
            }
        }));
        addMenuItemToKebab(
                getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.cloneVm()) {

            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getCloneVmCommand();
            }
        }));
        addDividerToKebab();
        addMenuItemToKebab(
        getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.runOnceVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getRunOnceCommand();
            }
        }));
        addButtonToActionGroup(
        getTable().addActionButton(new WebAdminImageButtonDefinition<VM>(constants.runVm(),
                IconType.PLAY) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getRunCommand();
            }
        }));
        addButtonToActionGroup(
        getTable().addActionButton(new WebAdminImageButtonDefinition<VM>(constants.suspendVm(),
                IconType.MOON_O) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getPauseCommand();
            }
        }));
        addButtonToActionGroup(
        getTable().addActionButton(new WebAdminImageButtonDefinition<VM>(constants.shutDownVm(), IconType.STOP
                ) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getShutdownCommand();
            }
        }));
        addButtonToActionGroup(
        getTable().addActionButton(new WebAdminImageButtonDefinition<VM>(constants.powerOffVm(), IconType.POWER_OFF) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getStopCommand();
            }
        }));
        addButtonToActionGroup(
        getTable().addActionButton(new WebAdminImageButtonDefinition<VM>(constants.rebootVm(),
                IconType.REPEAT) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getRebootCommand();
            }
        }));

        List<ActionButtonDefinition<VM>> consoleOptionsSubActions = new LinkedList<>();
        consoleOptionsSubActions.add(new UiCommandButtonDefinition<VM>(eventBus, constants.consoleOptions()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getEditConsoleCommand();
            }
        });

        // TODO: separator
        addButtonToActionGroup(
        getTable().addActionButton(new WebAdminImageButtonDefinition<VM>(constants.consoleVm(),
                IconType.DESKTOP) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getConsoleConnectCommand();
            }
        }, new DropdownActionButton<>(consoleOptionsSubActions, new DropdownActionButton.SelectedItemsProvider<VM>() {
            @Override
            public List<VM> getSelectedItems() {
                return getMainModel().getSelectedItems();
            }
        }, true, IconType.DESKTOP)));

        addDividerToKebab();
        addMenuItemToKebab(
        getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.consoleOptions(),
                CommandLocation.OnlyFromContext) { //$NON-NLS-1$
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getEditConsoleCommand();
            }
        }));
        addDividerToKebab();
        addMenuItemToKebab(
        getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.migrateVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getMigrateCommand();
            }
        }));
        addMenuItemToKebab(
        getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.cancelMigrationVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getCancelMigrateCommand();
            }
        }));
        addMenuItemToKebab(
        getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.cancelConvertVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getCancelConvertCommand();
            }
        }));
        addDividerToKebab();
        addMenuItemToKebab(
        getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.makeTemplateVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getNewTemplateCommand();
            }
        }));
        addDividerToKebab();
        addMenuItemToKebab(
        getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.exportVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getExportCommand();
            }
        }));
        addMenuItemToKebab(
        getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.createSnapshotVM()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getCreateSnapshotCommand();
            }
        }));
        addButtonToActionGroup(
        getTable().addActionButton(new WebAdminButtonDefinition<VM>(constants.changeCdVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getChangeCdCommand();
            }
        }));
        addMenuItemToKebab(
        getTable().addMenuListItem(new WebAdminButtonDefinition<VM>(constants.assignTagsVm()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getAssignTagsCommand();
            }
        }));

        addMenuItemToKebab(
        getTable().addMenuListItem(new WebAdminImageButtonDefinition<VM>(constants.guideMeVm(),
                IconType.SUPPORT, true) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getGuideCommand();
            }
        }));
    }
}
