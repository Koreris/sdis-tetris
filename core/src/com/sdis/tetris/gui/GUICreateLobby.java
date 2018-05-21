package com.sdis.tetris.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.sdis.tetris.Buttons;
import com.sdis.tetris.Tetris;
import com.sdis.tetris.audio.SFX;
import com.sdis.tetris.audio.Song;
import com.sdis.tetris.network.TetrisClient;

import java.io.IOException;

public class GUICreateLobby extends GUIScreen{

    private final Stage stage = new Stage();
    private final Table table = new Table();
    Sprite background = new Sprite(new Texture(Gdx.files.internal("img/main_menu.png"), false));
    Sprite title = new Sprite(new Texture(Gdx.files.internal("img/main_title.png"), false));
    private final TextButton createButton = new TextButton("CREATE LOBBY", Buttons.MenuButton);
    private final TextButton backButton = new TextButton("< BACK", Buttons.MenuButton);
    private Label label;
    private Skin skin;
    private TextField lobbyTextField;
    private TetrisClient client;
    
    private class Back implements Runnable
    {
        @Override
        public void run()
        {
            parent.switchTo(new GUIMultiPlayer(parent));
        }
    }

    private class CreateLobby implements Runnable
    {
        @Override
        public void run()
        {
            parent.switchTo(new GUIWaitLobby(parent));
        }
    }

    public GUICreateLobby(Tetris paramParent) {
        super(paramParent, Song.THEME_A);
        client = paramParent.networkClient;
        background.setPosition(0,0);
        background.setSize((float)Gdx.graphics.getWidth(),(float)Gdx.graphics.getHeight());
        skin  = new Skin(Gdx.files.internal("menu/menu.json"), new TextureAtlas(Gdx.files.internal("menu/menu.atlas")));
        label = new Label("Lobby name:", skin);
        lobbyTextField = new TextField("", skin);
        lobbyTextField.setAlignment(Align.center);
        table.add(label).expandX().center().row();
        table.add(lobbyTextField).size((float)Gdx.graphics.getWidth()/4, (float)Gdx.graphics.getHeight()/10).padBottom(10).row();;
        table.row();
        title.setPosition((float)Gdx.graphics.getWidth()/3.7f,(float)Gdx.graphics.getHeight()-title.getHeight()*2);
        table.add(createButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
        table.add(backButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
        title.scale(0.7f);
        table.bottom();
        table.setFillParent(true);
        table.setVisible(true);
        stage.addActor(table);
        backButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                audio.playSFX(SFX.HOVER);
                stage.addAction(Actions.sequence(Actions.moveTo(-480.0f, 0.0f, 0.5f), Actions.run(new Back())));

            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                if (! backButton.isPressed())
                {
                    audio.playSFX(SFX.SELECT);
                }
            }
        });
        createButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                audio.playSFX(SFX.HOVER);
             
                try {
                    if(client.create_lobby(lobbyTextField.getText(), paramParent.playerName)==0)
                    	stage.addAction(Actions.sequence(Actions.moveTo(-480.0f, 0.0f, 0.5f), Actions.run(new CreateLobby())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                if (! createButton.isPressed())
                {
                    audio.playSFX(SFX.SELECT);
                }
            }
        });
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor( 1, 1, 1, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        stage.act();

        batch.begin();

        background.draw(batch);
        title.draw(batch);

        batch.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height);
        //camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void dispose()
    {
        stage.dispose();
        skin.dispose();
    }
}
