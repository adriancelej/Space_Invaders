package game;

public class Launch {

	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 450;
	
	public static void main(String[] args){
		System.setProperty("sun.java2d.opengl","True");
		Game game = new Game("Space Invaders", WINDOW_WIDTH, WINDOW_HEIGHT);
		game.start();
	}

}
