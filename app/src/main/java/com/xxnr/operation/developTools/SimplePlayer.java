package com.xxnr.operation.developTools;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;

import java.io.IOException;

public class SimplePlayer {
	private MediaPlayer player;

	public boolean isPlaying() {
		return player != null && player.isPlaying();
	}

	public void setOnCompletionListener(OnCompletionListener listener) {
		if (player != null) {
			player.setOnCompletionListener(listener);
		}
	}

	public void start(String path) {
		if (isPlaying()) {
			stop();
		}
		if (TextUtils.isEmpty(path)) {
			XLog.e("audio path is null.");
			return;
		}
		player = new MediaPlayer();
		try {
			player.setDataSource(path);
			player.prepare();
			player.start();
		} catch (IllegalArgumentException | IOException | IllegalStateException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		if (player != null && player.isPlaying()) {
			player.stop();
			player.reset();
			player.release();
		}
		player = null;
	}
}
