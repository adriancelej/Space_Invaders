package keyboard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener; 

public class KeyManager implements KeyListener {
	private boolean[] keys;
	public boolean left, right, space;
	
	public KeyManager(){
		keys = new boolean[256];
	}
	
	public void check(){					//sprawdza, ktore klawisze sa wcisniete
		left = keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_RIGHT];
		space = keys[KeyEvent.VK_SPACE];
	}
	//to sa domyslne metody do zaimplementowania kiedy uzywamy KeyListenera
	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;//definiujemy, ze kiedy wciskamy klawisz to element tablicy o indeksie=id tego klawisza ma wartosc true
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
