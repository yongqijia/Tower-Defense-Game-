package MVC;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import Enemies.Enemy;
import Networking.TowerDefenseMessge;
import Tower.*;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import Enemies.*;

public class TowerDefenseView extends Application implements Observer {

	public static final double HEIGHT = 50;
	
	public TowerDefenseController controller;
	private GridPane gridPane = new GridPane();
	private TowerDestory sellTower;
	private Label moneyBalance;
	private Timeline timeline;
	private int SECS = 0;
	private long waveInterval = 5;
	private PathTransition pathTransition;
	private Group root;

	public TowerDefenseView() {
		controller = new TowerDefenseController(this);
	}

	// @Override
	// public void start(Stage stage) throws Exception {
	// stage.setTitle("Tower Defense");
	// BorderPane window = new BorderPane();
	//
	// Path path = new Path();
	// Circle circle = new Circle();
	//
	// circle.setCenterX(50);
	// circle.setCenterX(50);
	// circle.setRadius(20);
	// circle.setFill(Color.BLACK);
	//
	// path.getElements().add(new MoveTo(50, 50));
	// path.getElements().add(new LineTo(300, 50));
	// path.getElements().add(new LineTo(300, 100));
	// path.getElements().add(new LineTo(250, 100));
	// PathTransition pathTransition = new PathTransition();
	// pathTransition.setDuration(Duration.millis(5000));
	// pathTransition.setNode(circle);
	// pathTransition.setPath(path);
	// pathTransition.play();
	// //Scene scene2 = new Scene(root, 600, 300);
	// //window.getChildren().add(root);
	// Group root = new Group();
	// createBoard(window);
	// createRightContainer(window);
	// root.getChildren().add(window);
	// root.getChildren().add(circle);
	//
	// Scene scene = new Scene(root, 850, 750);
	// stage.setScene(scene);
	// //stage.setScene(scene2);
	// stage.show();
	// }

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Tower Defense");
		BorderPane window = new BorderPane();
		root = new Group(window);
		createBoard(window);
		createRightContainer(window);
		Scene scene = new Scene(root, 850, 750);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Creates 50x50 grid which represents the 2D array. Each position corresponds
	 * to the array.
	 * 
	 * @param window:
	 *            BorderPane to set grid
	 */
	private void createBoard(BorderPane window) {
		for (int i = 0; i < controller.getCol(); i++) {
			for (int j = 0; j < controller.getRow(); j++) {
//				Rectangle rec = new Rectangle();
//				if (controller.get_intBoard()[j][i] == 1) {
//					rec.setFill(Color.YELLOW);
//				} else {
//					rec.setFill(Color.GREEN);
//				}
//				rec.setWidth(45);
//				rec.setHeight(45);
				
				if(controller.get_imagePos() > 0) {
					Image image = new Image("file:image/map3/" +controller.get_imagePos() + ".jpg"); 
					ImageView imageView = new ImageView();
					imageView.setImage(image);
					imageView.setFitHeight(50);
					imageView.setFitWidth(50); 
					
					gridPane.add(imageView, j, i);
					controller.update_imagePos();
				}
				
			}
		}
		gridPane.setOnMouseClicked(e -> {
			int x = (int) e.getX();
			int y = (int) e.getY();
			// System.out.println(controller.getLCT());

			if (!controller.is_tower_here(x, y)) {
				if (controller.getLCT() != null) {
					controller.placeTower(x, y);
				} else {
					// lct is null, nothing happen
				}
			} else {
				if (controller.getLCT() == null) {
					sellTower = new TowerDestory(x, y);
					Tower temp = controller.getTowerAt(x, y);
					int credit = temp.getSoldPrice();
					sellTower.setCost(credit);
					controller.setLCT(sellTower);

				} else {
					controller.setLCT_null();
				}
			}
		});
		gridPane.setVgap(0.2);
		gridPane.setHgap(0.2);
		window.setCenter(gridPane);

	}

	/**
	 * Creates the menu, money, and tower vBoxes that's displayed on the right.
	 * 
	 * @param window
	 *            BorderPane to add vBoxes
	 */
	private void createRightContainer(BorderPane window) {
		VBox vBox = new VBox(50);
		createMenu(vBox); // menu VBox
		createMoney(vBox); // money VBox
		createTowers(vBox); // tower VBox
		window.setRight(vBox);
	}

	/**
	 * Creates the menu drop down for new game, pause, and speed
	 * 
	 * @param vBox
	 *            Vertical box to add children.
	 */
	private void createMenu(VBox vBox) {
		// Menu VBox
		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Menu");
		MenuItem newGame = new MenuItem("New Game");
		MenuItem startGame = new MenuItem("Start Game");
		MenuItem pause = new MenuItem("Pause");
		MenuItem speedUp = new MenuItem("Speed++");
		pause.setDisable(true); // disable until new game is clicked
		speedUp.setDisable(true); // disable until new game is clicked

		// event handler
		startGame.setOnAction(event -> {
			startNewGame(startGame, pause, speedUp); // parameters used when new game
		});
		menu.getItems().addAll(newGame, startGame, pause, speedUp);
		menuBar.getMenus().add(menu);
		VBox menuBox = new VBox(menuBar);
		vBox.getChildren().add(menuBox);
	}

	/**
	 * Creates the total balance for the game.
	 * 
	 * @param vBox
	 *            Vertical box to add children
	 */
	private void createMoney(VBox vBox) {
		VBox moneyBox = new VBox(5);
//		TextFlow moneyDisplay = new TextFlow();
		Text moneyTitle = new Text("BALANCE");
		moneyBalance = new Label(Integer.toString(controller.getBalance()));
		moneyBox.getChildren().addAll(moneyTitle, moneyBalance);
		vBox.getChildren().add(moneyBox);
	}

	/**
	 * Creates tower boxes with event handlers.
	 * 
	 * @param vBox
	 *            Vertical box to add children
	 */
	private void createTowers(VBox vBox) {
		// main tower Vbox
		VBox mainTowerBox = new VBox(30);

		List<Tower> towerList = new ArrayList<Tower>();
		Tower t = new Tower1();
		towerList.add(t);
		t = new Tower2();
		towerList.add(t);
		t = new Tower3();
		towerList.add(t);
		t = new Tower4();
		towerList.add(t);
		t = new Tower5();
		towerList.add(t);
		t = new Tower6();
		towerList.add(t);

		for (int i = 0; i < towerList.size(); i++) {
			final String index = i + 1 + "";
			Tower tower = towerList.get(i);
			VBox towerBox = new VBox(5);
			Button towerButton = new Button(tower.getName());
			Label towerCost = new Label("Cost: " + tower.getCost());
			towerBox.getChildren().addAll(towerButton, towerCost);
			mainTowerBox.getChildren().addAll(towerBox);
			towerButton.setOnAction(e -> {
//				System.out.println("tower");
				controller.new_tower_to_LCT(index);
			});
		}

		VBox towerBox = new VBox(5);
		Button sellButton = new Button("sell");
		Label sellReturn = new Label("get 75%");
		towerBox.getChildren().addAll(sellButton, sellReturn);
		mainTowerBox.getChildren().addAll(towerBox);
		sellButton.setOnMouseClicked(e -> {
			int x = sellTower.getSellX();
			int y = sellTower.getSellY();
			controller.sellTower(x, y);

		});
		
		VBox testBox = new VBox(5);
		Button testButton = new Button("test");
		towerBox.getChildren().addAll(testButton);
		mainTowerBox.getChildren().addAll(testBox);
		testButton.setOnMouseClicked(e -> {
			this.fps();
		});
		
		vBox.getChildren().add(mainTowerBox);
	}

	// Event handlers methods
	private void startNewGame(MenuItem startGame, MenuItem pause, MenuItem speedUp) {
		pause.setDisable(false);
		speedUp.setDisable(false);
		startTimer();
		pause.setOnAction(event -> {
			pauseGame();
		});
		speedUp.setOnAction(event -> {
			speedUpGame();
		});

	}

	private void pauseGame() {

	}

	private void speedUpGame() {

	}

	// new Timeline(new KeyFrame(
	// Duration.millis(2500),ae -> doSomething())).play();
	//

	private void startTimer() { 
		InputStream music;
		try {
			music = new FileInputStream(new File("sound/first_turrets.wav"));
			AudioStream audios = new AudioStream(music);
			AudioPlayer.player.start(audios);
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Error");
		}
		timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> fps()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(false);
		timeline.play();
	}

	private void fps() {
		controller.fps();
	}

	
	Map<String, ImageView> circleMap = new HashMap<>();
	@Override
	public void update(Observable o, Object arg) {
		TowerDefenseModel model = (TowerDefenseModel)o;
		if (arg != null) {
			TowerDefenseMessge message = (TowerDefenseMessge) arg;

			if (message.getColor() == 0) { // Grass
				
				Image image = new Image("file:image/map1/" + 
						((15 - message.getRow() - 1) * 15 + (15 - message.getCol())) + ".jpg"); 
				ImageView imageView = new ImageView();
				imageView.setImage(image);
				imageView.setFitHeight(50);
				imageView.setFitWidth(50); 
				
				gridPane.add(imageView, message.getCol(), message.getRow());
			} else if (message.getColor() > 0 && message.getColor() < 7) { // Tower
				ImageView imageView = new ImageView();
				if(message.getColor() == 1) {
					Image image = new Image("file:image/tower/t1.gif"); 
					imageView.setImage(image);
				}else if(message.getColor() == 2) {
					Image image = new Image("file:image/tower/t2.gif"); 
					imageView.setImage(image);
				}else if(message.getColor() == 3) {
					Image image = new Image("file:image/tower/t3.gif"); 
					imageView.setImage(image);
				}else if(message.getColor() == 4) {
					Image image = new Image("file:image/tower/t4.gif"); 
					imageView.setImage(image);
				}else if(message.getColor() == 5) {
					Image image = new Image("file:image/tower/t5.gif"); 
					imageView.setImage(image);
				}else if(message.getColor() == 6) {
					Image image = new Image("file:image/tower/t1.gif"); 
					imageView.setImage(image);
				}
				imageView.setFitHeight(50);
				imageView.setFitWidth(50); 	
				gridPane.add(imageView, message.getCol(), message.getRow());
			}else if(message.getColor() == -1 ){ // < 0 update balance
					
			}else { // attack action
					
			}
			moneyBalance.setText(Integer.toString(controller.getBalance()));
			System.out.println("col: " + message.getCol());
			System.out.println("row: " + message.getRow());
		}else { 
			
			
			/*
			 * move enemies on the view based on the data from model
			 */
			Map<Enemy, Point> map = model.getMap();
			ArrayList<Enemy> removelist = new ArrayList<>();
			
			for(Entry<Enemy, Point> element : map.entrySet()) {
				Enemy key = element.getKey();
				System.out.print("enemy hp: " + key.getHp() + " ");
			}
			
			// enemy in view
			for (Entry<Enemy, Point> element : map.entrySet()) {
				Enemy key = element.getKey();
				Point value = element.getValue();
				if (value==null) {
					ImageView imageView = new ImageView();

					imageView.setX(-50);
					imageView.setY(-50);
//					circle.setRadius(20);
					if(key instanceof Enemy1) {
						Image image = new Image("file:image/enemy/e1.gif"); 
						imageView.setImage(image);	
//						gridPane.add(imageView, j, i);
//						controller.update_imagePos();
//						circle.setFill(Color.RED);
					}else if(key instanceof Enemy2) {
						Image image = new Image("file:image/enemy/e2.gif"); 
						imageView.setImage(image);
//						circle.setFill(Color.PINK);
					}else if(key instanceof Enemy3) {
						Image image = new Image("file:image/enemy/e3.gif"); 
						imageView.setImage(image);
//						circle.setFill(Color.BLUE);
					}else if(key instanceof Enemy4) {
						Image image = new Image("file:image/enemy/e4.gif"); 
						imageView.setImage(image);
//						circle.setFill(Color.GREY);
					}else {
//						circle.setFill(Color.BLACK);
					}
					
					imageView.setFitHeight(50);
					imageView.setFitWidth(50);
					/*Path path = new Path();
					path.getElements().add(new MoveTo(key.getX(), key.getY()));*/
					circleMap.put(key.getId(), imageView);
					root.getChildren().add(imageView);
					// create line
				}else {
					// find circle
					ImageView circle = circleMap.get(key.getId());
					// Trigger die?
//					System.out.println(key.isAlive());
					
					
					if (!key.isAlive()) {
						circle.setVisible(false);
						removelist.add(key);
						circleMap.remove(key.getId());
						
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
						model.addBalance(key.getCredit());
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
						
					}else {
						Path path = new Path();
						path.getElements().add(new MoveTo(value.getX(), value.getY()));
						path.getElements().add(new LineTo(key.getX(), key.getY()));
						
						// move in line
						pathTransition = new PathTransition();
						pathTransition.setDuration(Duration.millis(1000));
						pathTransition.setNode(circle);
						pathTransition.setPath(path);
						//
						pathTransition.play();
						pathTransition.setOnFinished((event)->{
//							System.out.println("end!!");
							path.getElements().clear();
						});

						// enemy exits map without dying 
						if(key.getX() == 15 * HEIGHT + 25) {
							circle.setVisible(false);	
						}
					}
				}
			}
			for(Enemy enemy : removelist) {
				controller.removeEnemy(enemy);
			}
			System.out.println();
		}

	}

}
