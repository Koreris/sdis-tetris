package com.sdis.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class Buttons
{
	private static final TextureAtlas StyleAtlas = new TextureAtlas(Gdx.files.internal("menu/menu.atlas"));
	private static final Skin StyleSkin = new Skin(Gdx.files.internal("menu/menu.json"), StyleAtlas);
	public static final TextButtonStyle MenuButton = new TextButtonStyle(StyleSkin.get("menuLabel", TextButtonStyle.class));
	public static final TextButtonStyle ToggleButton = new TextButtonStyle(StyleSkin.get("toggle", TextButtonStyle.class));
	public static final LabelStyle GradientLabel = new LabelStyle(StyleSkin.get("gradientLabel", LabelStyle.class));
	public static final LabelStyle SmallLabel = new LabelStyle(StyleSkin.get("smallLabel", LabelStyle.class));
	public static final LabelStyle TitleLabel = new LabelStyle(StyleSkin.get("default", LabelStyle.class));
	public static final SliderStyle DefaultSlider = new SliderStyle(StyleSkin.get("default-horizontal", SliderStyle.class));
	public static final ImageButtonStyle VolumeButton = new ImageButtonStyle(StyleSkin.get("default", ImageButtonStyle.class));

}