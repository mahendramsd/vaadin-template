package com.mssoft.pms.ui;

import com.mssoft.pms.GreetService;
import com.mssoft.pms.ui.components.navigation.bar.AppBar;
import com.mssoft.pms.ui.components.navigation.bar.TabBar;
import com.mssoft.pms.ui.components.navigation.drawer.NaviDrawer;
import com.mssoft.pms.ui.components.navigation.drawer.NaviItem;
import com.mssoft.pms.ui.components.navigation.drawer.NaviMenu;
import com.mssoft.pms.ui.util.UIUtils;
import com.mssoft.pms.ui.util.css.Display;
import com.mssoft.pms.ui.util.css.Overflow;
import com.mssoft.pms.ui.views.Accounts;
import com.mssoft.pms.ui.views.Home;
import com.mssoft.pms.ui.views.Payments;
import com.mssoft.pms.ui.views.Statistics;
import com.mssoft.pms.ui.views.personnel.Accountants;
import com.mssoft.pms.ui.views.personnel.Managers;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.mssoft.pms.ui.components.FlexBoxLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route
@PWA(name = "Vaadin Application",
        shortName = "Vaadin App",
        description = "This is an example Vaadin application.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@CssImport(value = "./styles/components/charts.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme")
@CssImport(value = "./styles/components/floating-action-button.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/components/grid.css", themeFor = "vaadin-grid")
@CssImport("./styles/lumo/border-radius.css")
@CssImport("./styles/lumo/icon-size.css")
@CssImport("./styles/lumo/margin.css")
@CssImport("./styles/lumo/padding.css")
@CssImport("./styles/lumo/shadow.css")
@CssImport("./styles/lumo/spacing.css")
@CssImport("./styles/lumo/typography.css")
@CssImport("./styles/misc/box-shadow-borders.css")
@CssImport(value = "./styles/styles.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge")
public class MainLayout extends FlexBoxLayout implements RouterLayout, AfterNavigationObserver {

    private static final Logger log = LoggerFactory.getLogger(MainLayout.class);
    private static final String CLASS_NAME = "root";

    private Div appHeaderOuter;

    private FlexBoxLayout row;
    private NaviDrawer naviDrawer;
    private FlexBoxLayout column;

    private Div appHeaderInner;
    private Main viewContainer;
    private Div appFooterInner;

    private Div appFooterOuter;

    private TabBar tabBar;
    private boolean navigationTabs = false;
    private AppBar appBar;



    public MainLayout() {

        VaadinSession.getCurrent()
                .setErrorHandler((ErrorHandler) errorEvent -> {
                    log.error("Uncaught UI exception",
                            errorEvent.getThrowable());
                    Notification.show(
                            "We are sorry, but an internal error occurred");
                });

        addClassName(CLASS_NAME);
        setFlexDirection(FlexDirection.COLUMN);
        setSizeFull();

        // Initialise the UI building blocks
        initStructure();

        // Populate the navigation drawer
        initNaviItems();

        // Configure the headers and footers (optional)
        initHeadersAndFooters();
    }

    private void initHeadersAndFooters() {
        // setAppHeaderOuter();
        // setAppFooterInner();
        // setAppFooterOuter();

        // Default inner header setup:
        // - When using tabbed navigation the view title, user avatar and main menu button will appear in the TabBar.
        // - When tabbed navigation is turned off they appear in the AppBar.

        appBar = new AppBar("");

        // Tabbed navigation
        if (navigationTabs) {
            tabBar = new TabBar();
            UIUtils.setTheme(Lumo.DARK, tabBar);

            // Shift-click to add a new tab
            for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
                item.addClickListener(e -> {
                    if (e.getButton() == 0 && e.isShiftKey()) {
                        tabBar.setSelectedTab(tabBar.addClosableTab(item.getText(), item.getNavigationTarget()));
                    }
                });
            }
            appBar.getAvatar().setVisible(false);
            setAppHeaderInner(tabBar, appBar);

            // Default navigation
        } else {
            UIUtils.setTheme(Lumo.DARK, appBar);
            setAppHeaderInner(appBar);
        }

    }


    private void setAppHeaderOuter(Component... components) {
        if (appHeaderOuter == null) {
            appHeaderOuter = new Div();
            appHeaderOuter.addClassName("app-header-outer");
            getElement().insertChild(0, appHeaderOuter.getElement());
        }
        appHeaderOuter.removeAll();
        appHeaderOuter.add(components);
    }

    private void setAppHeaderInner(Component... components) {
        if (appHeaderInner == null) {
            appHeaderInner = new Div();
            appHeaderInner.addClassName("app-header-inner");
            column.getElement().insertChild(0, appHeaderInner.getElement());
        }
        appHeaderInner.removeAll();
        appHeaderInner.add(components);
    }


    private void setAppFooterInner(Component... components) {
        if (appFooterInner == null) {
            appFooterInner = new Div();
            appFooterInner.addClassName("app-footer-inner");
            column.getElement().insertChild(column.getElement().getChildCount(),
                    appFooterInner.getElement());
        }
        appFooterInner.removeAll();
        appFooterInner.add(components);
    }

    private void setAppFooterOuter(Component... components) {
        if (appFooterOuter == null) {
            appFooterOuter = new Div();
            appFooterOuter.addClassName("app-footer-outer");
            getElement().insertChild(getElement().getChildCount(),
                    appFooterOuter.getElement());
        }
        appFooterOuter.removeAll();
        appFooterOuter.add(components);
    }

    private void initNaviItems() {
        NaviMenu menu = naviDrawer.getMenu();
        menu.addNaviItem(VaadinIcon.HOME, "Home", Home.class);
        menu.addNaviItem(VaadinIcon.INSTITUTION, "Accounts", Accounts.class);
        menu.addNaviItem(VaadinIcon.CREDIT_CARD, "Payments", Payments.class);
        menu.addNaviItem(VaadinIcon.CHART, "Statistics", Statistics.class);

        NaviItem personnel = menu.addNaviItem(VaadinIcon.USERS, "Personnel",
                null);
        menu.addNaviItem(personnel, "Accountants", Accountants.class);
        menu.addNaviItem(personnel, "Managers", Managers.class);

    }

    private void initStructure() {
        naviDrawer = new NaviDrawer();

        viewContainer = new Main();
        viewContainer.addClassName(CLASS_NAME + "__view-container");
        UIUtils.setDisplay(Display.FLEX, viewContainer);
        UIUtils.setFlexGrow(1, viewContainer);
        UIUtils.setOverflow(Overflow.HIDDEN, viewContainer);

        column = new FlexBoxLayout(viewContainer);
        column.addClassName(CLASS_NAME + "__column");
        column.setFlexDirection(FlexDirection.COLUMN);
        column.setFlexGrow(1, viewContainer);
        column.setOverflow(Overflow.HIDDEN);

        row = new FlexBoxLayout(naviDrawer, column);
        row.addClassName(CLASS_NAME + "__row");
        row.setFlexGrow(1, column);
        row.setOverflow(Overflow.HIDDEN);
        add(row);
        setFlexGrow(1, row);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        this.viewContainer.getElement().appendChild(content.getElement());
    }

    public NaviDrawer getNaviDrawer() {
        return naviDrawer;
    }

    public static MainLayout get() {
        return (MainLayout) UI.getCurrent().getChildren()
                .filter(component -> component.getClass() == MainLayout.class)
                .findFirst().get();
    }

    public AppBar getAppBar() {
        return appBar;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (navigationTabs) {
            afterNavigationWithTabs(event);
        } else {
            afterNavigationWithoutTabs(event);
        }
    }

    private void afterNavigationWithTabs(AfterNavigationEvent e) {
        NaviItem active = getActiveItem(e);
        if (active == null) {
            if (tabBar.getTabCount() == 0) {
                tabBar.addClosableTab("", Home.class);
            }
        } else {
            if (tabBar.getTabCount() > 0) {
                tabBar.updateSelectedTab(active.getText(),
                        active.getNavigationTarget());
            } else {
                tabBar.addClosableTab(active.getText(),
                        active.getNavigationTarget());
            }
        }
        appBar.getMenuIcon().setVisible(false);
    }

    private NaviItem getActiveItem(AfterNavigationEvent e) {
        for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
            if (item.isHighlighted(e)) {
                return item;
            }
        }
        return null;
    }

    private void afterNavigationWithoutTabs(AfterNavigationEvent e) {
        NaviItem active = getActiveItem(e);
        if (active != null) {
            getAppBar().setTitle(active.getText());
        }
    }

}
