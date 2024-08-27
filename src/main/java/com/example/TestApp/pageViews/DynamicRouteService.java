package com.example.TestApp.pageViews;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteConfiguration;
import org.springframework.stereotype.Component;

@Component
public class DynamicRouteService {

    public void createAndNavigateToDynamicRoute(String routeName) {
        RouteConfiguration routeConfig = RouteConfiguration.forApplicationScope();
        routeName="myapp/"+routeName;
        if (routeConfig.isPathAvailable(routeName)) {
            UI.getCurrent().navigate(routeName);
        } else {
            routeConfig.setRoute(routeName, GameView.class);
            UI.getCurrent().navigate(routeName);
        }
    }

}



