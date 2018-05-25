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
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sdis.tetris.Buttons;
import com.sdis.tetris.Tetris;
import com.sdis.tetris.audio.SFX;
import com.sdis.tetris.audio.Song;
import com.sdis.tetris.network.TetrisClient;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GUIMultiPlayer extends GUIScreen{
    private final Stage stage = new Stage();
    private final Table table = new Table();
    Sprite background = new Sprite(new Texture(Gdx.files.internal("img/main_menu.png"), false));
    Sprite title = new Sprite(new Texture(Gdx.files.internal("img/main_title.png"), false));
    private final TextButton joinButton = new TextButton("Join lobby", Buttons.MenuButton);
    private final TextButton createButton = new TextButton("Create lobby", Buttons.MenuButton);
    private final TextButton backButton = new TextButton("Server menu", Buttons.MenuButton);
    private final List<String> list;
    private ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(0);
    private Skin skin;
    private Skin skinv2;
    final ScrollPane scroll;
    private TetrisClient client;

    private class JoinLobby implements Runnable
    {
        @Override
        public void run()
        {
            parent.switchTo(new GUIWaitLobby(parent));
        }
    };

    private class CreateLobby implements Runnable
    {
        @Override
        public void run()
        {
            parent.switchTo(new GUICreateLobby(parent));
        }
    }

    private class Back implements Runnable
    {
        @Override
        public void run()
        {
            parent.switchTo(new GUIServer(parent));
        }
    }

    public void listLobbies(Tetris paramParent) {
    	  try {
    			client.list_lobbies(paramParent.serverName, paramParent.serverAddress, paramParent.serverPort);
    			String[] strings = new String[client.list_lobbies.size()];
    	        if(strings.length==0) {
    	        	joinButton.setVisible(false);
    	        	list.setItems(strings);
    	        }
    	        else{
	    	        joinButton.setVisible(true);
	    	        for (int i = 0; i<strings.length;i++) {
	    	            strings[i] = client.list_lobbies.get(i);
	    	        }
	    	        list.setItems(strings);
    	  		}
            } catch (IOException e1) {
    			e1.printStackTrace();
    			 scheduler.shutdown();
    			 stage.addAction(Actions.sequence(Actions.moveTo(-480.0f, 0.0f, 0.5f), Actions.run(new Back())));
    		}  
    }
    
    public GUIMultiPlayer(Tetris paramParent) {
        super(paramParent, Song.THEME_A);
        client = paramParent.networkClient;
        background.setPosition(0,0);
        background.setSize((float)Gdx.graphics.getWidth(),(float)Gdx.graphics.getHeight());
        title.setPosition((float)Gdx.graphics.getWidth()/3.7f,(float)Gdx.graphics.getHeight()-title.getHeight()*2);
        title.setColor(175f,130f,80f,255f);
        skin  = new Skin(Gdx.files.internal("menu/myskin.json"), new TextureAtlas(Gdx.files.internal("menu/atlas.atlas")));
        skinv2  = new Skin(Gdx.files.internal("menu/menu.json"), new TextureAtlas(Gdx.files.internal("menu/menu.atlas")));
        list=new List<>(skin);
        list.setAlignment(1);
        joinButton.setVisible(false);
        scheduler.scheduleAtFixedRate(new Runnable() 
        {
        	public void run() 
        	{
        		listLobbies(paramParent);
        	}
        }, 0, 1,TimeUnit.SECONDS);
        
        scroll = new ScrollPane(list, skinv2);
        table.add(list).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
        table.add(scroll);
        table.row();
        table.add(joinButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
        table.add(createButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
        table.add(backButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
        table.setFillParent(true);
        table.setVisible(true);
        stage.addActor(table);
        joinButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                audio.playSFX(SFX.HOVER);
                String selected = list.getSelected();
                try {
                    client.join_lobby(selected.split(" ")[0], paramParent.playerName);
                    scheduler.shutdown();
                    stage.addAction(Actions.sequence(Actions.moveTo(-480.0f, 0.0f, 0.5f), Actions.run(new JoinLobby())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                if ( joinButton.isPressed())
                {
                    audio.playSFX(SFX.HOVER);
                }
            }
        });

        createButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	scheduler.shutdown();
                audio.playSFX(SFX.HOVER);
                stage.addAction(Actions.sequence(Actions.moveTo(-480.0f, 0.0f, 0.5f), Actions.run(new CreateLobby())));
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                if (! createButton.isPressed())
                {
                    audio.playSFX(SFX.HOVER);
                }
            }
        });

        backButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	scheduler.shutdown();
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
