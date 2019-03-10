package display;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Display_1 extends JFrame {
	private String title; //tytu³
	private JMenu menu_game;
	//private JMenu menu_help; 
	private JPanel gameGraphics; // obszar grafiki gry
	private JPanel jpanel; //dolny pasek na wynik i ¿yciê
	private String scorePoints;
	private JLabel scorePoint;
	private int healthPoints;
	private JMenuItem newgame;
	private JMenuItem network;
	//private JMenuItem save;
	//private JMenuItem open;
	private JMenuItem bestResults;
	private JMenuItem pause;
	private JMenuItem rePause;
	private JPanel lifeSq1; //pasek ¿ycia 1
	private JPanel lifeSq2; //pasek ¿ycia 2
	private JPanel lifeSq3; //pasek ¿ycai 3
	private int width, height; //rozmiary okna
	private JDialog resultsDialog;
	private JOptionPane playerName;
	
	public Display_1(String title, int width, int height){
		super(title);
		this.title=title;
		this.width=width;
		this.height=height;
		createGui(); //tworzy GUI
	}
	
	private void createGui() { //tworzy GUI
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.createGameGraphics();
		this.createPanel();
		this.createMenu();
		
		this.pack();
		this.setVisible(true);
		this.setResizable(true);
		this.setMinimumSize(new Dimension(400,400));
	}
	
	private void createMenu() { //tworzy Menu g³ówne gry
		menu_game= new JMenu("Gra");
		//menu_help= new JMenu("Pomoc");
		menu_game.setIgnoreRepaint(true);
		menu_game.setDoubleBuffered(true);
		
		//opcje w menu "Gra"
		newgame = new JMenuItem("Nowa gra"); 
		network= new JMenuItem("Gra sieciowa");
		bestResults = new JMenuItem("Najlepsze wyniki"); 
		//save= new JMenuItem("Zapisz grê");
		//open= new JMenuItem("Wczytaj grê");
		pause = new JMenuItem("Pauza");
		pause.setMnemonic(KeyEvent.VK_P);
		pause.setAccelerator(KeyStroke.getKeyStroke("alt P"));
		
		rePause = new JMenuItem("Wznów grê");
		rePause.setVisible(false);
		rePause.setMnemonic(KeyEvent.VK_S);
		rePause.setAccelerator(KeyStroke.getKeyStroke("alt S"));
		
		
		menu_game.add(newgame);
		menu_game.add(pause);
		menu_game.add(rePause);
		menu_game.add(network);
		menu_game.add(bestResults);
		//menu_game.add(save);
		//menu_game.add(open);
		
		JMenuBar menuBar= new JMenuBar();
		menuBar.add(menu_game);
		//menuBar.add(menu_help);
		
		
		this.setJMenuBar(menuBar);
	}
	
	private void createGameGraphics() { //tworzy obszar grafiki gry
		gameGraphics=new JPanel();
		gameGraphics.setPreferredSize(new Dimension(width,height));
		gameGraphics.setFocusable(false);
		this.add(gameGraphics,"Center");
		gameGraphics.setVisible(true);
		gameGraphics.setDoubleBuffered(true);
		gameGraphics.setIgnoreRepaint(true);
	}
	
	private void createPanel() { // tworzy panel - dolny pasek na wynik i ¿ycie
		jpanel=new JPanel();
		jpanel.setPreferredSize(new Dimension(width,height/20));
		jpanel.setVisible(true);
		this.add(jpanel,"South");
		jpanel.setLayout(new BoxLayout(jpanel,BoxLayout.X_AXIS));
		jpanel.setDoubleBuffered(false);
		jpanel.setIgnoreRepaint(true);
		
		JLabel lifeLabel=new JLabel("¯ycie:");
		lifeLabel.setVisible(true);
		
		JLabel scoreLabel= new JLabel("Punkty:");
		scoreLabel.setVisible(true);
		
		scorePoint = new JLabel();
		scorePoint.setVisible(true);
		
		lifeSq1=new JPanel();
		lifeSq1.setMinimumSize(new Dimension(50,25));
		lifeSq1.setMaximumSize(new Dimension(100,25));
		lifeSq1.setBackground(new Color(255,0,0));
		lifeSq1.setVisible(true);
		
		lifeSq2=new JPanel();
		lifeSq2.setMinimumSize(new Dimension(50,25));
		lifeSq2.setMaximumSize(new Dimension(100,25));
		lifeSq2.setBackground(new Color(255,0,0));
		lifeSq2.setVisible(true);
		
		lifeSq3=new JPanel();
		lifeSq3.setMinimumSize(new Dimension(50,25));
		lifeSq3.setMaximumSize(new Dimension(100,25));
		lifeSq3.setBackground(new Color(255,0,0));
		lifeSq3.setVisible(true);
		
		jpanel.add(Box.createGlue());
		jpanel.add(scoreLabel);
		jpanel.add(scorePoint);
		jpanel.add(Box.createRigidArea(new Dimension(100,25))); //puste miejsce
		jpanel.add(lifeLabel);
		jpanel.add(Box.createRigidArea(new Dimension(15,25))); //puste miejsce
		jpanel.add(lifeSq1);
		jpanel.add(Box.createRigidArea(new Dimension(10,jpanel.getHeight()))); //puste miejsce
		jpanel.add(lifeSq2);
		jpanel.add(Box.createRigidArea(new Dimension(10,jpanel.getHeight()))); //puste miejsce
		jpanel.add(lifeSq3);
		jpanel.add(Box.createRigidArea(new Dimension(10,jpanel.getHeight()))); //puste miejsce
		
	}
	
	public JPanel getGameGraphics() {
		return gameGraphics;
	}
	
	public void setScore(int score) { //ustaw wynik - aktualizuj
		scorePoints= Integer.toString(score);
		scorePoint.setText(scorePoints);
	}
	
	public void setHealth(int health) { //ustaw pkt ¿ycia - aktualizuj
		healthPoints=health;
		if(healthPoints>=3) { //wyœwietlaj 3 paski ¿ycia
			lifeSq1.setVisible(true);
			lifeSq2.setVisible(true);
			lifeSq3.setVisible(true);
		}
		else {
			if(healthPoints==2) { //wyœwietlaj 2 paski ¿ycia
				lifeSq1.setVisible(true);
				lifeSq2.setVisible(true);
				lifeSq3.setVisible(false);
			}
			if(healthPoints==1) { //wyœwietlaj 1 pasek ¿ycia
				lifeSq1.setVisible(true);
				lifeSq2.setVisible(false);
				lifeSq3.setVisible(false);
			}
			if(healthPoints==0) { //wyœwietlaj 0 pasków ¿ycia
				lifeSq1.setVisible(false);
				lifeSq2.setVisible(false);
				lifeSq3.setVisible(false);
			}
		}
	}
	
	public void setPauseListener(ActionListener action) { //ustawia listenera opcji Pause w menu Gra 
		pause.addActionListener(action);
	}
	
	public void setRePauseListener(ActionListener action) { //ustawia listenera opcji Wznów w menu Gra
		rePause.addActionListener(action);
	}
	
	public void setPauseVisibility(boolean visibility) { //ustawia widocznoœæ opcji Wznów w menu Gra
		pause.setVisible(visibility);
	}
	
	public void setRePauseVisibility(boolean visibility) { //ustawia widocznoœæ opcji Pauza w menu Gra
		rePause.setVisible(visibility);
	}
	
	public void setBestResultsListener(ActionListener action) {
		bestResults.addActionListener(action);
	}
	
	public void setNetworkListener(ActionListener action) {
		network.addActionListener(action);
	}
	
	public int showNewGameDialog() {
		String options[]= {"Nie","Tak"};
		int option = JOptionPane.showOptionDialog(this, "Czy na pewno chcesz zacz¹æ now¹ grê?", "Nowa gra", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		return option;
	}
	
	public int showNetworkGameDialog() {
		String options[]= {"Nie", "Tak"};
		int option = JOptionPane.showOptionDialog(this, "Czy chcesz zacz¹æ grê sieciow¹?", "Gra sieciowa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		return option;
	}
	
	public void setNewGameListener(ActionListener action) { //ustawia listenera opcji Nowa gra w menu Gra
		newgame.addActionListener(action);
	}
	
	public void menuUpdate() {
		menu_game.updateUI();
		//menu_help.updateUI();
		jpanel.updateUI();
	}
	
	public void createResultsFrame(JPanel results) {
		resultsDialog=new JDialog(this,"Najlepsze wyniki");
		resultsDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		resultsDialog.add(results);
		resultsDialog.pack();
		resultsDialog.setModal(false);
		resultsDialog.setVisible(true);
	}
	
	public String getPlayerName() {
		String message="Podaj nazwê gracza:";
		String nick="default";
		nick=JOptionPane.showInputDialog(this, message, "Podaj nick", JOptionPane.QUESTION_MESSAGE);
		return nick;
	}
	
	public String getIPAdress() {
		String message="Podaj adres IP serwera:";
		String adress="localhost";
		adress=JOptionPane.showInputDialog(this, message, "Podaj IP", JOptionPane.QUESTION_MESSAGE);
		return adress;
	}
	
	public String getPortNumber() {
		String message="Podaj numer portu serwera:";
		String port="50000";
		port=JOptionPane.showInputDialog(this, message, "Podaj numer portu", JOptionPane.QUESTION_MESSAGE);
		return port;
	}
	
}
