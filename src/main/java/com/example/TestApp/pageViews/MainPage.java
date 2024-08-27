package com.example.TestApp.pageViews;


import com.example.TestApp.security.JWTcore;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("myapp/home")
public class MainPage extends VerticalLayout {

    @Autowired
    public MainPage(DynamicRouteService dynamicRouteService) {
        JWTcore jwTcore = new JWTcore();
        String userToken = jwTcore.getTokenFromCookie();

        Button createAndNavigateButton = new Button("Create Dynamic Route and Navigate", event -> {
            String routeName = jwTcore.getAuth(userToken)[0];
            dynamicRouteService.createAndNavigateToDynamicRoute(routeName);
        });

        add(createAndNavigateButton);
    }
}
