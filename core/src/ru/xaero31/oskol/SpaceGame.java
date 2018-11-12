package ru.xaero31.oskol;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import ru.xaero31.oskol.screen.titles.OpenScreen;

public class SpaceGame extends Game {
	@Override
	public void create () {
		Gdx.input.setCatchBackKey(true);
		setScreen(new OpenScreen(this));
	}
}
