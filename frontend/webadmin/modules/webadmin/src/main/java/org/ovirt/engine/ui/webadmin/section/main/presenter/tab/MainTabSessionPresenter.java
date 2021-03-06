package org.ovirt.engine.ui.webadmin.section.main.presenter.tab;

import java.util.List;

import org.ovirt.engine.core.common.businessentities.UserSession;
import org.ovirt.engine.ui.common.place.PlaceRequestFactory;
import org.ovirt.engine.ui.common.uicommon.model.MainModelProvider;
import org.ovirt.engine.ui.common.widget.OvirtBreadCrumbs;
import org.ovirt.engine.ui.common.widget.tab.ModelBoundTabData;
import org.ovirt.engine.ui.uicommonweb.models.SessionListModel;
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

public class MainTabSessionPresenter extends AbstractMainTabWithDetailsPresenter<UserSession,
    SessionListModel, MainTabSessionPresenter.ViewDef, MainTabSessionPresenter.ProxyDef> {

    @GenEvent
    public class SessionSelectionChange {

        List<UserSession> selectedItems;

    }

    @ProxyCodeSplit
    @NameToken(WebAdminApplicationPlaces.sessionMainTabPlace)
    public interface ProxyDef extends TabContentProxyPlace<MainTabSessionPresenter> {
    }

    public interface ViewDef extends AbstractMainTabWithDetailsPresenter.ViewDef<UserSession> {
    }

    @Inject
    public MainTabSessionPresenter(EventBus eventBus,
            ViewDef view,
            ProxyDef proxy,
            PlaceManager placeManager,
            MainModelProvider<UserSession, SessionListModel> modelProvider,
            SearchPanelPresenterWidget<UserSession, SessionListModel> searchPanelPresenterWidget,
            OvirtBreadCrumbs<UserSession, SessionListModel> breadCrumbs) {
        super(eventBus, view, proxy, placeManager, modelProvider, searchPanelPresenterWidget, breadCrumbs);
    }

    @TabInfo(container = MainTabPanelPresenter.class)
    static TabData getTabData(
            MainModelProvider<UserSession, SessionListModel> modelProvider, WebadminMenuLayout menuLayout) {
        MenuLayoutMenuDetails menuTabDetails =
                menuLayout.getDetails(WebAdminApplicationPlaces.sessionMainTabPlace);
        return new ModelBoundTabData(menuTabDetails.getSecondaryTitle(), menuTabDetails.getSecondaryPriority(),
                menuTabDetails.getPrimaryTitle(), menuTabDetails.getPrimaryPriority(), modelProvider,
                menuTabDetails.getIcon());
    }

    @Override
    protected void fireTableSelectionChangeEvent() {
        SessionSelectionChangeEvent.fire(this, getSelectedItems());
    }

    @Override
    protected PlaceRequest getMainTabRequest() {
        return PlaceRequestFactory.get(WebAdminApplicationPlaces.sessionMainTabPlace);
    }

    @Override
    protected boolean hasSelectionDetails() {
        return false;
    }

}

