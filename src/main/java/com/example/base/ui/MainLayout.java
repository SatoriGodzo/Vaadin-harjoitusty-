package com.example.base.ui;

import com.example.views.EmployeeView;
import com.example.views.DepartmentView;
import com.example.views.AccessCardView;
import com.example.views.ProjectView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

@Layout
public final class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        // Добавляем заголовок, навигацию и футер в Drawer (боковую панель)
        addToDrawer(createApplicationHeader(), createApplicationDrawer(), createApplicationFooter());
    }

    private Component createApplicationHeader() {
        var appLogo = new Avatar("My Application");
        appLogo.addClassName("app-logo");
        appLogo.addThemeVariants(AvatarVariant.AURA_FILLED, AvatarVariant.XSMALL);
        var appName = new Span("Company Manager");
        appName.addClassName("app-name");
        var header = new HorizontalLayout(appLogo, appName);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);
        return header;
    }

    private Component createApplicationDrawer() {
        var scroller = new Scroller(createSideNav());
        scroller.addThemeVariants(ScrollerVariant.OVERFLOW_INDICATORS);
        return scroller;
    }

    private Component createApplicationFooter() {
        var footer = new VerticalLayout(new Span("Made with ❤️ with Vaadin"));
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.addClassName("app-footer");
        return footer;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.setMinWidth(200, Unit.PIXELS);

        // КНОПКИ ПЕРЕХОДА ДЛЯ ВСЕХ ТВОИХ СТРАНИЦ:
        nav.addItem(new SideNavItem("Employees", EmployeeView.class, VaadinIcon.USERS.create()));
        nav.addItem(new SideNavItem("Departments", DepartmentView.class, VaadinIcon.BUILDING.create()));
        nav.addItem(new SideNavItem("Access Cards", AccessCardView.class, VaadinIcon.KEY.create()));
        nav.addItem(new SideNavItem("Projects", ProjectView.class, VaadinIcon.BRIEFCASE.create()));

        // Автоматические пункты меню, если они настроены через аннотации
        MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            Component icon = menuEntry.icon().contains(".svg") ? new SvgIcon(menuEntry.icon()) : new Icon(menuEntry.icon());
            return new SideNavItem(menuEntry.title(), menuEntry.path(), icon);
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.path());
        }
    }
}