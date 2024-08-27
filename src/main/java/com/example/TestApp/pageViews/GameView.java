package com.example.TestApp.pageViews;

import com.example.TestApp.security.JWTcore;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GameView extends Div {

    private static final List<String> tokensOnPage = new ArrayList<>();
    private static final List<String> teamOne=new ArrayList<>();
    private static final List<String> teamTwo=new ArrayList<>();
    private final Div usersDiv=new Div();
    private final Div team1=new Div();
    private final Div team2=new Div();

    @Autowired
    private GameRepository gameRepository;



    public GameView() {
        Div playersText=new Div();
        playersText.setText("Players:");
        add(playersText);
        add(usersDiv);
        JWTcore jwTcore = new JWTcore();
        String userToken = jwTcore.getTokenFromCookie();
        String userName= jwTcore.getAuth(userToken)[0];
        if(userToken!=null)
        {
            tokensOnPage.add(userName);
            updateUsers();
        }

        UI.getCurrent().getSession().addRequestHandler((session, request, response) -> {
            tokensOnPage.remove(userName);
            return false;
        });


        HorizontalLayout buttonLayout = getHorizontalButtonLayout(userName);
        add(buttonLayout);

        HorizontalLayout teamsDiv=new HorizontalLayout(team1,team2);
        add(teamsDiv);

        Button buttonSave = new Button("Save");
        buttonSave.addClickListener(event->{
            GameLobby gameLobby=new GameLobby("test2team1","test2team2",1);
            gameRepository.save(gameLobby);
        });
        add(buttonSave);
    }

    private HorizontalLayout getHorizontalButtonLayout(String userName) {
        Button buttonTeam1 = new Button("team1");
        buttonTeam1.addClickListener(event -> {
            if(teamCheck(userName)==0) {

                Div teamDiv1 = new Div();
                teamDiv1.setText(userName);
                teamOne.add(userName);
                team1.add(teamDiv1);
            }
        });
        Button buttonTeam2 = new Button("team2");
        buttonTeam2.addClickListener(event -> {
            if(teamCheck(userName)==0) {
                Div teamDiv2 = new Div();
                teamDiv2.setText(userName);
                teamTwo.add(userName);
                team2.add(teamDiv2);
            }
        });

        return new HorizontalLayout(buttonTeam1, buttonTeam2);
    }

    private void updateUsers() {
        usersDiv.removeAll();
        for (String user : tokensOnPage) {
            Div userDiv = new Div();
            userDiv.setText(user);
            usersDiv.add(userDiv);
        }
    }

    private int teamCheck(String name)
    {
        int found=0;
        for(String user : teamOne)
        {
            if(Objects.equals(user, name))
            {
                found=1;
                break;
            }
        }
        for(String user : teamTwo)
        {
            if(Objects.equals(user, name))
            {
                found=2;
                break;
            }
        }

        return found;
    }


}


