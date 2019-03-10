package loader;

import java.awt.image.BufferedImage;

public class LoadedImage {

	private static BufferedImage player, alien, alien2, backgroundLVL1, backgroundLVL2, backgroundLVL3, others,shot,alienShot, alienShot2, gameOver;//obrazy gracza,potworka,t�a

	public static void load(){
		player = ImageLoader.loadImage("/textures/player1.png");//gracz
		alien = ImageLoader.loadImage("/textures/ufo.png");//potworek
		alien2 = ImageLoader.loadImage("/textures/alien2.png");
		backgroundLVL1 = ImageLoader.loadImage("/textures/ground.jpg");//t�o
		backgroundLVL2 = ImageLoader.loadImage("/textures/lvl2.png");//t�o
		backgroundLVL3 = ImageLoader.loadImage("/textures/lvl3.png");//t�o
		others = ImageLoader.loadImage("/textures/high.png");//t�o
		shot = ImageLoader.loadImage("/textures/playerShot.png");//pocisk
		alienShot = ImageLoader.loadImage("/textures/alienShot.png");//pocisk potworka
		alienShot2 = ImageLoader.loadImage("/textures/alienShot2.png");//pocisk potworka
		gameOver= ImageLoader.loadImage("/textures/gameOver.png");//koniec gry
		
	}
	
	public static BufferedImage getPlayer() {//zwr�� obraz gracza
		return player;
	}
	
	public static BufferedImage getAlien() {//zwr�� obraz potworka
		return alien;
	}
	
	public static BufferedImage getAlien2() {//zwr�� obraz potworka
		return alien2;
	}
	
	public static BufferedImage getBackground(int lvl) {//zwr�� obraz t�a
		if(lvl==1)
			return backgroundLVL1;
		if(lvl==2)
			return backgroundLVL2;
		if(lvl==3)
			return backgroundLVL3;
		else
			return others;
	}
	public static BufferedImage getShot(){
		return shot;
	}
	public static BufferedImage getAlienShot() {
		return alienShot;
	}
	
	public static BufferedImage getAlienShot2() {
		return alienShot2;
	}
	
	public static BufferedImage getGameOver() {
		return gameOver;
	}
}
	