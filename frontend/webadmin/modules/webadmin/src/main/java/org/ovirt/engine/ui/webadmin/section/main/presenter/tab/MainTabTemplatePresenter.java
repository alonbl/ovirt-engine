package org.ovirt.engine.ui.webadmin.section.main.presenter.tab;

import java.util.List;

import org.ovirt.engine.core.common.businessentities.VmTemplate;
import org.ovirt.engine.ui.common.place.PlaceRequestFactory;
import org.ovirt.engine.ui.common.uicommon.model.MainModelProvider;
import org.ovirt.engine.ui.common.widget.OvirtBreadCrumbs;
import org.ovirt.engine.ui.common.widget.tab.ModelBoundTabData;
import org.ovirt.engine.ui.uicommonweb.models.templates.TemplateListModel;
import org.ovirt.engine.ui.uicommonweb.place.WebAdminApplicationPlaces;
import org.ovirt.engine.ui.webadmin.section.main.presenter.AbstractMainTabWithDetailsPresenter;
import org.ovirt.engine.ui.webadmin.section.main.presenter.MainTabPanelPresenter;
import org.ovirt.engine.ui.webadmin.section.main.presenter.SearchPanelPresenterWidget;
import org.ovirt.engine.ui.webadmin.widget.tab.MenuLayoutMenuDetails;
import org.ovirt.engine.ui.webadmin.widget.tab.WebadminMenuLayout;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.annotation.GenEvent;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class MainTabTemplatePresenter extends AbstractMainTabWithDetailsPresenter<VmTemplate, TemplateListModel, MainTabTemplatePresenter.ViewDef, MainTabTemplatePresenter.ProxyDef> {

    @GenEvent
    public class TemplateSelectionChange {

        List<VmTemplate> selectedItems;

    }

    @ProxyCodeSplit
    @NameToken(WebAdminApplicationPlaces.templateMainTabPlace)
    public interface ProxyDef extends TabContentProxyPlace<MainTabTemplatePresenter> {
    }

    public interface ViewDef extends AbstractMainTabWithDetailsPresenter.ViewDef<VmTemplate> {
    }

    @TabInfo(container = MainTabPanelPresenter.class)
    static TabData getTabData(
            MainModelProvider<VmTemplate, TemplateListModel> modelProvider, WebadminMenuLayout menuLayout) {
        MenuLayoutMenuDetails menuTabDetails =
                menuLayout.getDetails(WebAdminApplicationPlaces.templateMainTabPlace);
        return new ModelBoundTabData(menuTabDetails.getSecondaryTitle(), menuTabDetails.getSecondaryPriority(),
                menuTabDetails.getPrimaryTitle(), menuTabDetails.getPrimaryPriority(), modelProvider,
                menuTabDetails.getIcon());
    }

    @Inject
    public MainTabTemplatePresenter(EventBus eventBus, ViewDef view, ProxyDef proxy,
            PlaceManager placeManager, MainModelProvider<VmTemplate, TemplateListModel> modelProvider,
            SearchPanelPresenterWidget<VmTemplate, TemplateListModel> searchPanelPresenterWidget,
            OvirtBreadCrumbs<VmTemplate, TemplateListModel> breadCrumbs) {
        super(eventBus, view, proxy, placeManager, modelProvider, searchPanelPresenterWidget, breadCrumbs);
    }

    @Override
    protected void fireTableSelectionChangeEvent() {
        TemplateSelectionChangeEvent.fire(this, getSelectedItems());
    }

    @Override
    protected PlaceRequest getMainTabRequest() {
        return PlaceRequestFactory.get(WebAdminApplicationPlaces.templateMainTabPlace);
    }

}
