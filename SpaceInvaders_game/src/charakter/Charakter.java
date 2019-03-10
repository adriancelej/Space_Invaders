package charakter;

public abstract class Charakter implements Runnable{ //postacie, uruchamiane w oddzielnych w¹tkach 

	protected int x_position;
	protected int y_position;
	protected int move_speed;
	
	public Charakter(int x, int y, int speed) {
		x_position=x;
		y_position=y;
		move_speed=speed;
	}
	
	protected abstract void move(); //odpowiedzialna za ruch
	
	public void set_Speed(int speed) {
		move_speed=speed;
	}
	
	public void reverse_Speed(int y) { //odwraca kierunek ruchu poprzez mianê prêdkoœci na przeciwn¹
		move_speed=-move_speed;
		y_position+= y/20; //ruch stworka w dó³
	}
	
	public int getX_position() {
		return x_position;
	}
	
	public int getY_position() {
		return y_position;
	}
}
