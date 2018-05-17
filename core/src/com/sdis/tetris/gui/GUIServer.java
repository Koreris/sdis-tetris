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
import com.sdis.tetris.network.ParseServersFile;
import com.sdis.tetris.network.TetrisClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.*;

public class GUIServer extends GUIScreen{
    private final Stage stage = new Stage();
    private final Table table = new Table();
    Sprite background = new Sprite(new Texture(Gdx.files.internal("img/main_menu.png"), false));
    Sprite title = new Sprite(new Texture(Gdx.files.internal("img/main_title.png"), false));
    private final TextButton joinButton = new TextButton("Join Server", Buttons.MenuButton);
    private final TextButton backButton = new TextButton("Main Menu", Buttons.MenuButton);
    private final List<String> list;
    private Skin skin;
    private Skin skinv2;
    private TetrisClient client;
    final ScrollPane scroll;
    ThreadPoolExecutor executor;
    private TextField playerTextField;
    private Label label;
    ConcurrentHashMap<String,String> other_servers;

    private class JoinServer implements Runnable
    {
        @Override
        public void run()
        {
            parent.switchTo(new GUIMultiPlayer(parent));
        }
    }

    private class Back implements Runnable
    {
        @Override
        public void run()
        {
            parent.switchTo(new GUIMainMenu(parent));
        }
    }
    
    public void listServers(){
    	 other_servers = new ConcurrentHashMap<>();
         executor.execute(new ParseServersFile(other_servers));
         executor.shutdown();
         try {
             executor.awaitTermination(5, TimeUnit.SECONDS);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
         String[] strings = new String[other_servers.size()];
         int i = 1;
         for (Map.Entry me: other_servers.entrySet()) {
             strings[other_servers.size()-i] = me.getKey()+ " " + me.getValue();
             i++;
         }
         list.setItems(strings);
    }
    
    public GUIServer(Tetris paramParent) {
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
        LinkedBlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>();
        executor = new ThreadPoolExecutor(1, 5, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        
        listServers();
        
        scroll = new ScrollPane(list, skinv2);
        label = new Label("Player name:", skinv2);
        playerTextField = new TextField("", skinv2);
        playerTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!playerTextField.getText().equals("") && playerTextField.getText()!=null)
                	joinButton.setVisible(true);
                else joinButton.setVisible(false);
            }
        });
        playerTextField.setAlignment(Align.center);
        table.add(label).expandX().center().row();
        table.add(playerTextField).size((float)Gdx.graphics.getWidth()/4, (float)Gdx.graphics.getHeight()/10).padBottom(10).row();;
        table.row();
        table.add(list).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
        table.add(scroll);
        table.row();
        table.add(joinButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
        table.add(backButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
     
        table.setFillParent(true);
        table.setVisible(true);
        stage.addActor(table);
        
        joinButton.setVisible(false);
        joinButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                audio.playSFX(SFX.HOVER);
                String selected = list.getSelected();
                String[] lineComponents = selected.split(" ");
               
                try {
                    paramParent.serverAddress = InetAddress.getByName(lineComponents[1]);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                paramParent.serverPort = Integer.parseInt(lineComponents[2]);
            	paramParent.playerName=playerTextField.getText();
            	paramParent.serverName=lineComponents[0];
                stage.addAction(Actions.sequence(Actions.moveTo(-480.0f, 0.0f, 0.5f), Actions.run(new JoinServer())));   
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
