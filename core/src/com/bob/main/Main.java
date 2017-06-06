package com.bob.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.bob.game.GameController;
import com.bob.game.levels.Level;
import com.bob.game.levels.LevelFactory;
import com.bob.game.world.Textures;

public class Main extends ApplicationAdapter {

	// Game State
	protected GameState gameState;
	private float gameStateTime;

	private Skin skin;
	private Stage stage;

	protected Menu menu;
	private GameController gameController;

	@Override
	public void create() {

        skin = new Skin(Gdx.files.internal("default-skin/uiskin.json"));
		stage = new Stage(new FitViewport(1920, 1080));

		skin.add("font", new BitmapFont());

		addAllStyles();

		OrthographicCamera camera = new OrthographicCamera();
		Viewport viewport = new FitViewport(1920,1080,camera);
		camera.position.set(960,540,0);
		viewport.apply();

		gameController = new GameController(skin, camera);
		menu = new Menu(skin);

		gameController.linkStage(stage);
		menu.setStage(stage);

		gameStateTime = 0;
		gameState = GameState.MENU;

		LevelFactory.initialiseLevels();

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void dispose() {
		//batch.dispose();
		stage.dispose();

		for (Textures t: Textures.values()) {
			t.dispose();
		}

		TextureFactory.dispose();
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render() {

		float deltaTime = Gdx.graphics.getDeltaTime();
        gameStateTime += deltaTime;

		Gdx.gl.glClearColor(39, 156, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (gameState == GameState.MENU) {
			if (!menu.isVisible()) {
				startLevel(menu.getLevelSelected());
			}
		}

		if (gameState == GameState.PLAYING) {
			gameController.render(deltaTime);

			if (!gameController.isVisible()) {
				menu.show();
				gameState = GameState.MENU;
			}
		}

		// Stage
		stage.act(deltaTime);
		stage.draw();
		//stage.setDebugAll(true);
	}

	protected void startLevel(Level lvl) {
		gameController.setLevel(lvl);
		gameController.show();
		gameController.startNewLevel(skin);
		gameState = GameState.PLAYING;
	}

	private void addAllStyles() {

		BitmapFont whiteFont = new BitmapFont(Gdx.files.internal("font/white.fnt"));
		whiteFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		whiteFont.getData().scale(-0.2f);
		skin.add("white", whiteFont);

		BitmapFont smallWhiteFont = new BitmapFont(Gdx.files.internal("font/white.fnt"));
		smallWhiteFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		smallWhiteFont.getData().scale(-0.4f);
		skin.add("small_white", smallWhiteFont);

		BitmapFont impactFont = new BitmapFont(Gdx.files.internal("font/impact.fnt"));
		impactFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		skin.add("impact", impactFont);

		BitmapFont smallFont = new BitmapFont(Gdx.files.internal("font/impact.fnt"));
		smallFont.getData().scale(-0.3f);
		smallFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		skin.add("impact_small", smallFont);

		BitmapFont defaultFont = new BitmapFont(Gdx.files.internal("default-skin/default.fnt"));
		defaultFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		skin.add("default", defaultFont);

		String[] buttonColor = {"grey", "grey_square", "big_grey", "orange", "red", "green", "blue", "yellow"};
		String[] buttonFont = {"impact_small", "impact", "impact", "impact_small", "impact_small", "impact_small", "impact_small", "impact_small"};
		skin.add("disabled_button", TextureFactory.createTexture("buttons/disabled.png"));
		
		for (int i = 0; i < buttonColor.length; i++) {
			String color = buttonColor[i];
			skin.add(color + "_button", TextureFactory.createTexture("buttons/" + color + ".png"));
			skin.add(color + "_clicked", TextureFactory.createTexture("buttons/" + color + "_clicked.png"));
			TextButton.TextButtonStyle colorButtonStyle = new TextButton.TextButtonStyle();
			colorButtonStyle.up = skin.getDrawable(color + "_button");
			colorButtonStyle.down = skin.getDrawable(color + "_clicked");
			colorButtonStyle.disabled = skin.getDrawable("disabled_button");
			colorButtonStyle.font = skin.getFont(buttonFont[i]);
			skin.add(color + "_button", colorButtonStyle);
		}

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = skin.getFont("white");
		skin.add("label_style", labelStyle);

		Label.LabelStyle redLabelStyle = new Label.LabelStyle();
		redLabelStyle.fontColor = Color.RED;
		redLabelStyle.font = skin.getFont("default");
		skin.add("red_label", redLabelStyle);

		Label.LabelStyle lightGreyLabelStyle = new Label.LabelStyle();
		lightGreyLabelStyle.fontColor = Color.LIGHT_GRAY;
		lightGreyLabelStyle.font = skin.getFont("default");
		skin.add("light_grey_label", lightGreyLabelStyle);

		Label.LabelStyle overLabelStyle = new Label.LabelStyle();
		overLabelStyle.fontColor = Color.WHITE;
		overLabelStyle.font = skin.getFont("default");
		skin.add("over_label", overLabelStyle);

		Label.LabelStyle infoStyle = new Label.LabelStyle();
		infoStyle.font = skin.getFont("small_white");
		skin.add("info_label", infoStyle);

		ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
		scrollPaneStyle.background
				= new TextureRegionDrawable(
						new TextureRegion(TextureFactory.createTexture("screens/transparent.png")));
		skin.add("scroll", scrollPaneStyle);

		Label.LabelStyle toolLabelStyle = new Label.LabelStyle();
		toolLabelStyle.font = new BitmapFont();
		toolLabelStyle.font.getData().scale(0.3f);

		// DRAG N DROP
		TextTooltip.TextTooltipStyle tooltipStyle = new TextTooltip.TextTooltipStyle();
		skin.add("tooltip_bkg", TextureFactory.createTexture("blocks/tooltip.png"));
		tooltipStyle.label = toolLabelStyle;

		tooltipStyle.background = skin.getDrawable("tooltip_bkg");

		skin.add("tooltipStyle", tooltipStyle);
	}
}
