package display;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Display extends JFrame { //grafika aplikacji serwerowej
	private String title; 
	JButton startButton=new JButton("Start"); //przycisk start
	JButton stopButton=new JButton("Stop"); //przycisk stop
	JLabel label=new JLabel("Podaj port, na którym będzie działał serwer:"); //etykieta
	JLabel adressLabel=new JLabel("Adres IP localhost serwera:"); //etykieta
	JLabel externalAdressLabel=new JLabel("Zewnętrzny adres IP serwera:"); //etykieta
	JTextField text=new JTextField(5); //pole tekstowe na port
	JTextField adress=new JTextField(10); //pole tekstowe, do wyświetlenia adresu IP (localhost)
	JTextField externalAdress=new JTextField(10); //pole tekstowe, do wyświetleneia adrsu IP (karty siecioewej)
	
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

	public void setStartButtonListener(ActionListener listener) { //ustawia słuchacza do startButton
		startButton.addActionListener(listener);
	}
	
	public void setStopButtonListener(ActionListener listener) { //ustawia słuchacza do stopButton
		stopButton.addActionListener(listener);
	}
	
	public void setStartButtonVisible(boolean aFlag) { //widzialność przycisku start
		startButton.setVisible(aFlag);
	}
	
	public void setStopButtonVisible(boolean aFlag) { //widzialność przycisku stop
		stopButton.setVisible(aFlag);
	}
	
	public void setAdress(String adress) { //wstawia adress do pola adress
		this.adress.setText(adress);
	}
	
	public void setExternalAdress(String adress) { //wstawia adres do pola externalAdress
		this.externalAdress.setText(adress);
	}
	
	public void setAdressVisible(boolean aFlag) { //widzialność adresu
		adress.setVisible(aFlag);
	}
	
	public void setAdressLabelVisible(boolean aFlag) { //widzialność etykiety adresu
		adressLabel.setVisible(aFlag);
	}
	
	public void setExternalAdressLabelVisible(boolean aFlag) { //widzialoność etykiet externalAdress
		externalAdressLabel.setVisible(aFlag);
	}
	
	public void setExternalAdressVisible(boolean aFlag) { //widzialność externalAdress
		externalAdress.setVisible(aFlag);
	}
}
