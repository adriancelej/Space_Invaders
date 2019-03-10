package display;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Display extends JFrame { //grafika aplikacji serwerowej
	private String title; 
	JButton startButton=new JButton("Start"); //przycisk start
	JButton stopButton=new JButton("Stop"); //przycisk stop
	JLabel label=new JLabel("Podaj port, na kt�rym b�dzie dzia�a� serwer:"); //etykieta
	JLabel adressLabel=new JLabel("Adres IP localhost serwera:"); //etykieta
	JLabel externalAdressLabel=new JLabel("Zewn�trzny adres IP serwera:"); //etykieta
	JTextField text=new JTextField(5); //pole tekstowe na port
	JTextField adress=new JTextField(10); //pole tekstowe, do wy�wietlenia adresu IP (localhost)
	JTextField externalAdress=new JTextField(10); //pole tekstowe, do wy�wietleneia adrsu IP (karty siecioewej)
	
	public Display(String name){
		title=name;
	}
	
	public void createGui() { //tworzy interfejs urzytkownika
		setTitle(title);
		
		setLayout(new GridLayout(4,2));
		
		startButton.setVisible(true);
		stopButton.setVisible(false);
		label.setVisible(true);
		adress.setVisible(false);
		adressLabel.setVisible(false);
		externalAdressLabel.setVisible(false);
		externalAdress.setVisible(false);
		add(label);
		add(text);
		add(startButton);
		add(stopButton); 
		add(adressLabel);
		add(adress);
		add(externalAdressLabel);
		add(externalAdress);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		 
	    pack();

	    setLocationRelativeTo(null);

	    setVisible(true);
		
	}

	public String getPortNumber() { //pobiera numer portu
		return text.getText();
	}

	public void setStartButtonListener(ActionListener listener) { //ustawia s�uchacza do startButton
		startButton.addActionListener(listener);
	}
	
	public void setStopButtonListener(ActionListener listener) { //ustawia s�uchacza do stopButton
		stopButton.addActionListener(listener);
	}
	
	public void setStartButtonVisible(boolean aFlag) { //widzialno�� przycisku start
		startButton.setVisible(aFlag);
	}
	
	public void setStopButtonVisible(boolean aFlag) { //widzialno�� przycisku stop
		stopButton.setVisible(aFlag);
	}
	
	public void setAdress(String adress) { //wstawia adress do pola adress
		this.adress.setText(adress);
	}
	
	public void setExternalAdress(String adress) { //wstawia adres do pola externalAdress
		this.externalAdress.setText(adress);
	}
	
	public void setAdressVisible(boolean aFlag) { //widzialno�� adresu
		adress.setVisible(aFlag);
	}
	
	public void setAdressLabelVisible(boolean aFlag) { //widzialno�� etykiety adresu
		adressLabel.setVisible(aFlag);
	}
	
	public void setExternalAdressLabelVisible(boolean aFlag) { //widzialono�� etykiet externalAdress
		externalAdressLabel.setVisible(aFlag);
	}
	
	public void setExternalAdressVisible(boolean aFlag) { //widzialno�� externalAdress
		externalAdress.setVisible(aFlag);
	}
}
