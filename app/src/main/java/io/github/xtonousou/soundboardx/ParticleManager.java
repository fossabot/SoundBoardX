package io.github.xtonousou.soundboardx;

import java.util.ArrayList;

class ParticleManager {

	private Particle particle;
	private String itemName;
	private ArrayList<Integer> drawableList;

	ParticleManager(Particle particle, String itemName) {
		this.particle = particle;
		this.itemName = itemName;
		this.drawableList = new ArrayList<>();
	}

	void emit() {
		switch (itemName) {
			/*
				Add cases of items' title.
				You can add particles (drawables) into list in order to be animated later.
				Here you map {Title, SoundFile} with {particle list}.
			 */
			case "WLDD":
				drawableList.add(R.drawable.rick);
				drawableList.add(R.drawable.morty);
				drawableList.add(R.drawable.meeseeks);
				particle.burstRandomly(drawableList, 25);
				break;
			case "Belair":
				drawableList.add(R.drawable.belair);
				particle.burstScaleRandomly(drawableList, 25);
				break;
		}
		drawableList.clear();
	}
}
