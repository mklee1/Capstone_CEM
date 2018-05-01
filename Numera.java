import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Numera extends Application {
    
	/**
	 * string variable needed for proper connection
	 * to the MySQL database with room table
	 */
	private static final String DB_URL = "jdbc:mysql://localhost/user";
	
    /**
     * database with room table should be the root in MySQL environment
     */
	private static final String USER = "root";
	
	/**
	 * password used as credentials for databse with room table
	 * in the MySQL environment
	 */
	private static final String PASSWORD = "whiteMamba99";
	
	private static Pane root = new Pane();
	
	// this will let the user write their answers
	private static Canvas canvas;
	private static final int CANVAS_WIDTH = 375;
	private static final int CANVAS_HEIGHT = 375;
	
	private static Scene scene;
	
	// used to keep track of which problem we are on
	private static LinkedList<WordProblem> problems = null;
	private static WordProblem pointer = null;
	private static int idx = 0;
	
	private static int min = 0;
	private static int sec = 0;
	
	private static Stage ref = null;
	
	
	// set up progress indicator stuff
	final ProgressBar[] pbs = new ProgressBar[1];
	
	private static boolean login(TextField user, TextField pass) {
		boolean res = false;
		Connection conn = null;
		String READ_OBJECT = "select * FROM user_table WHERE " 
		        + "username = ? and password = ?";
		try {
	        Class.forName("com.mysql.jdbc.Driver");
	    	System.out.println("Connecting to database... ");
	    	conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
	    	PreparedStatement pstmt = conn.prepareStatement(READ_OBJECT);
	    	pstmt.setString(1, user.getText());
	    	pstmt.setString(2,  pass.getText());
	    	ResultSet rs = pstmt.executeQuery();
	    	
	    	Label label = new Label();
	    	label.setId("label");
	    	
	    	if (rs.next()) {
	    		label.setText("Successful login!");
	    		res = true;
	    	}
	    	else {
	    		label.setText("Invalid credentials!");
	    		res = false;
	    	}
	    	label.setLayoutX(50);
	    	label.setLayoutY(50);
	    	root.getChildren().add(label);
		}
		catch (Exception e) {
	        e.printStackTrace();
	    }
		return res;
	}
	
	// used to swap screen to the where
	// user can select from a variety of
	// topics
	private void screenSwap(Pane p) {
		scene.setRoot(p);
		// the combo box for the choices user has
		ObservableList<String> options = 
		    FXCollections.observableArrayList(
			    "Addition",
			    "Multiplication",
			    "Division",
			    "Subtraction"
			);
		
		Label minLabel = new Label("Minutes: ");
		Label secLabel = new Label("Seconds: ");
		minLabel.setLayoutX(50);
		minLabel.setLayoutY(150);
		secLabel.setLayoutX(50);
		secLabel.setLayoutY(205);
		
		// use numeric spinners for time input and control
		final Spinner<Integer> minutes = new Spinner<Integer>();
		final Spinner<Integer> seconds = new Spinner<Integer>();
		
		// now set value factories
		SpinnerValueFactory<Integer> valFactory1 = 
				new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
		SpinnerValueFactory<Integer> valFactory2 = 
				new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
		
		
		minutes.setValueFactory(valFactory1);
		seconds.setValueFactory(valFactory2);
		
		// set positioning and adjust dimensions
		minutes.setPrefSize(85, 25);
		minutes.setLayoutX(50);
		minutes.setLayoutY(175);
		
		seconds.setPrefSize(85, 25);
		seconds.setLayoutX(50);
		seconds.setLayoutY(225);
		
		// disable both initially unless client explicitly 
		// pressed time toggle button
		minutes.setDisable(true);
		seconds.setDisable(true);
		
		// use a vbox so we have the options arranged in a column fashion
		int vGap = 15;
		int frameX = 100;
		VBox frame = new VBox(vGap);
		frame.setPadding(new Insets(15, 0, 0, 0));
		frame.setId("option");
		frame.setLayoutX(frameX);
		frame.setLayoutY(110);
		Label header1 = new Label("Time Options");
		
		frame.getChildren().add(header1);
		
		header1.setId("font2");
		final ToggleGroup group = new ToggleGroup();
		
		ToggleButton tb1 = new ToggleButton("Timed");
		ToggleButton tb2 = new ToggleButton("Untimed");
		tb1.setId("btn3");
		tb2.setId("btn3");
	    tb1.setToggleGroup(group);
	    tb2.setToggleGroup(group);
	    
	    VBox frame2 = new VBox(vGap);
	    frame2.setPadding(new Insets(15, 0, 0, 0));
	    frame2.setId("option");
	    int frame2X = 375;
	    frame2.setLayoutX(frame2X);
	    frame2.setLayoutY(110);
	    Label header2 = new Label("Mode Options");
	    header2.setId("font2");
	    frame2.getChildren().add(header2);
	    
	    // make a rectangle to in which we will place
	    // the integer spinner fields for nicer UI
	    int rWidth = 70;
	    int rHeight = 35;
	    // we must explicitly write out class name
	    // since we have awt import statement
	    javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle();
	    
		@SuppressWarnings({ "unchecked", "rawtypes" })
		final ChoiceBox comboBox = new ChoiceBox(options);
		comboBox.setLayoutX(250);
		comboBox.setLayoutY(75);
	    // change this later
	    comboBox.setId("comboBox");
	    
	    frame2.getChildren().add(comboBox);
	    //p.getChildren().add(comboBox);
	  
	    Button generate = new Button();
	    generate.setId("btn");
	    generate.setPrefSize(100, 25);
	    generate.setLayoutX(300);
	    generate.setLayoutY(500);
	    
	    double toggleY = 75;
	    
	    //tb1.setLayoutX(50);
	    //tb1.setLayoutY(toggleX);
	    tb1.setPrefWidth(75);
	    //tb2.setLayoutX(125);
	    //tb2.setLayoutY(toggleX);
	    tb2.setPrefWidth(75);
	    
	    HBox btnFrame = new HBox(tb1, tb2);
	    frame.getChildren().add(btnFrame);
	    //btnFrame.setLayoutX(50);
	    //btnFrame.setLayoutY(toggleY);
	    // set up event handlers for toggle buttons
	    tb1.setOnAction(new EventHandler<ActionEvent>() {
	    	
	    	@Override
	    	public void handle(ActionEvent event) {
	    		minutes.setDisable(false);
	    		seconds.setDisable(false);
	    	}
	    }); 
	    
        tb2.setOnAction(new EventHandler<ActionEvent>() {
	    	
	    	@Override
	    	public void handle(ActionEvent event) {
	    		minutes.setDisable(true);
	    		seconds.setDisable(true);
	    	}
	    }); 
	    
	    /*int rX = 50;
	    int rY = 102;
	    rect.setWidth(150);
	    rect.setHeight(80);
	    rect.setX(rX);
	    rect.setY(rY);
	    rect.setFill(Color.LIGHTGOLDENRODYELLOW);
	    */
	    
        // must be added to frame in correct order or formatting will be off
	    frame.getChildren().addAll(minLabel, minutes, secLabel, seconds);
	    generate.setText("Start practice");
	    p.getChildren().add(generate);
	    //p.getChildren().add(tb1);
	    //p.getChildren().add(tb2);
	    p.getChildren().add(rect);
	    //p.getChildren().addAll(minLabel, minutes);
	    //p.getChildren().addAll(secLabel, seconds);
	    p.getChildren().add(frame);
	    p.getChildren().add(frame2);
	    frame.setPrefSize(250, 300);
	    // print statement for testing
	    
	    // here is where we add the pictures for the options
	    // to make it more visually appealing for students
	    final ImageView img1 = new ImageView();
        File imgFile1 = new File("pictures/clock.png");
        Image image1 = new Image(imgFile1.toURI().toString());
        img1.setImage(image1);
        
        //double imgX = 75;
        double imgY = 25;
        img1.setLayoutX(frameX+25);
        img1.setLayoutY(imgY);
        img1.setFitHeight(75);
        img1.setFitWidth(75);
        p.getChildren().add(img1);
        
        final ImageView img2 = new ImageView();
        File imgFile2 = new File("pictures/notebook.png");
        Image image2 = new Image(imgFile2.toURI().toString());
        img2.setImage(image2);
        
        img2.setLayoutX(frame2X+35);
        img2.setLayoutY(imgY);
        img2.setFitHeight(75);
        img2.setFitWidth(75);
        p.getChildren().add(img2);
        
	    generate.setOnAction(new EventHandler<ActionEvent>() {
	    	 
            @Override
            
            // we must change scenes here
            public void handle(ActionEvent event) {
            	String option = (String) comboBox.getValue();
                try {
                	min = minutes.getValue();
                	sec = seconds.getValue();
					generatePractice(option, p);
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
	}
    
	// code generates LinkedList of problems 
	private static LinkedList<WordProblem> initializeList(ResultSet rs) throws SQLException {
		LinkedList<WordProblem> list = new LinkedList<WordProblem>();
		
		// create word problem instances and then queue
		while (rs.next()) {
			String question = rs.getString("problem_text");
			String answer = rs.getString("problem_answer");
			Set<String> solutions = new HashSet<>();
			solutions.add(answer);
			WordProblem wp = new WordProblem(question, solutions);
			list.add(wp);
		}
		
		return list;
	}
	
	private static void scoreBoard(int problemLength) {
		int midX = 225;
		int y = 75;
		Pane p = new Pane();
		int len = problems.size();
		
		// place these in a horizontal box for nice spacing
		int spacing = 50;
		int hx = 150;
		int hy = 200;
		
		javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(460, 55);
		// store hexcode in variable
		String rgb = "0x4286f4";
		Color colorChoice = Color.web(rgb, 1.0);
		//rect.setStroke(Color.BLACK);
		rect.setFill(colorChoice);
		rect.setStrokeWidth(3);
		rect.setArcWidth(25);
		rect.setArcHeight(25);
		rect.setLayoutX(hx-15);
		rect.setLayoutY(hy-60);
		
		// make two separate HBoxes for the headers and labels
		HBox header = new HBox(spacing);
		//Label nameHeader = new Label("Name");
		Label timeHeader = new Label("Time elapsed");
		Label scoreHeader = new Label("Total score");
		header.getChildren().addAll(timeHeader, scoreHeader);
		header.setLayoutX(hx);
		header.setLayoutY(hy-50);
		//nameHeader.setId("font3");
		timeHeader.setId("font3");
		scoreHeader.setId("font3");
		
		HBox info = new HBox(spacing);
		Label title = new Label("Results");
		
		//Label name = new Label("John Doe");
		Label time = new Label("5:00");
		Label s = new Label("100");
		//name.setId("font3");
		time.setId("font3");
		s.setId("font3");
		info.getChildren().addAll(title, time, s);
		//info.setId("font2");
		info.setLayoutX(hx);
		info.setLayoutY(hy);
		
		title.setId("font");
		title.setLayoutX(midX+50);
		title.setLayoutY(y);
		// we need to make a vertical box that equals length
		// of the world problems
		VBox box = new VBox(15);
		
		// programatically add fields
		for (int i = 0; i < problemLength; i++) {
			TextField score = new TextField("Placeholder");
			score.setPrefSize(100, 25);
			box.getChildren().add(score);
		}
		box.setLayoutX(midX);
		box.setLayoutY(y+100);
		p.getChildren().addAll(rect, header, title, info);
		p.setId("first");
	    scene.setRoot(p);
	}
	
	// helper method which does a quick query
	// to calculate number of problems in current table
	// mode is input parameter which tells code 
	// which table to fetch from
	private int fetchProblemSize(String mode) {
		Connection conn = null;
		int count = 0;
		try {
	        Class.forName("com.mysql.jdbc.Driver");
	    	conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
	    	String selector = "SELECT COUNT(*) AS COUNT FROM " + mode;
	    	PreparedStatement pstmt = conn.prepareStatement(selector);
	    	ResultSet rs = pstmt.executeQuery();
	    	if (rs.next()) {
	    		count = rs.getInt("COUNT");
	    	}
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return count;
	}
	
	private void generatePractice(String option, Pane prev) throws AWTException, IOException {
		Pane root = new Pane();
		
		
		root.setId("first");
		
		String READ_OBJECT = null;
		String CURRENT_TABLE = null;
		Connection conn = null;
		//LinkedList<WordProblem> problems = null;
		//int idx = 0;
		
		try {
	        Class.forName("com.mysql.jdbc.Driver");
	    	//System.out.println("Connecting to database... ");
	    	conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
	    	
	    	// use this branch to initialize READ_OBJECT correctly
	    	if (option.equals("Addition")) {
	    		READ_OBJECT = "select * FROM add_table";
	    		CURRENT_TABLE = "add_table";
	    	}
	    	else if (option.equals("Subtraction")) {
	    		READ_OBJECT = "select * FROM sub_table";
	    		CURRENT_TABLE = "SUB_TABLE";
	    	}
	    	
	    	else if (option.equals("Multiplication")) {
	    		READ_OBJECT = "select * FROM mult_table";
	    		CURRENT_TABLE = "MULT_TABLE";
	    	}
	    	
	    	else if (option.equals("Division")) {
	    		READ_OBJECT = "select * FROM div_table";
	    		CURRENT_TABLE = "DIV_TABLE";
	    	}
	    	Statement stmt = conn.createStatement();
	    	ResultSet rs = stmt.executeQuery(READ_OBJECT);
	        problems = initializeList(rs);
	        // pointer should be initialized to the first
	        pointer = problems.getFirst();
	        
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// make a label for the problem text
		Label ptext = new Label(pointer.getDescription());
		ptext.setId("font");
		//ptext.setFont(new Font("Arial", 20));
		//HBox hb = new HBox();
		//hb.setPrefSize(150, 100);
		//hb.setLayoutX(150);
		//hb.setLayoutY(75);
		//hb.setId("box");
		//ptext.setLayoutX(150);
		//ptext.setLayoutY(75);
		
		// used to go back
		Button back = new Button();
		back.setText("Back");
		back.setId("traverse");
		//back.setDisable(true);
		back.setTextFill(Color.WHITE);
		back.setPrefSize(125, 25);
		back.setLayoutX(50); // figure this out...
		back.setLayoutY(520);
		
		VBox vb = new VBox(10);
		vb.setLayoutX(275);
		vb.setLayoutY(520);
		int problemLen = fetchProblemSize(CURRENT_TABLE);
		
		// you must set padding 
		final ProgressBar pb = new ProgressBar();
		pb.setPadding(new Insets(10, 0, 0, 0));
		pb.setProgress(0);
		Label pbHeader = new Label("Progress Percentage: 0 %");
		vb.getChildren().add(pb);
		vb.getChildren().add(pbHeader);
		//pb.setLayoutX(275);
		//pb.setLayoutY(520);
		
		back.setOnAction(new EventHandler<ActionEvent>() {
			 
            @Override
            
            // we must change scenes here
            public void handle(ActionEvent event) {
            	if (idx > 0) {
            	    idx--;
			        pointer = problems.get(idx);
			        ptext.setText(pointer.getDescription());
			        //double prog = idx / ((double) problemLen);
				    //pb.setProgress(prog);
            	}
            }
        });
		
		Button next = new Button();
		next.setText("Submit");
		next.setId("traverse");
		next.setTextFill(Color.WHITE);
		next.setPrefSize(125, 15);
		next.setLayoutX(475);
		next.setLayoutY(520);
		
		
		System.out.println("Problem length: " + problemLen);
		
		// we would like to place progress bar ideally
		// little before halfway X direction
		// put in a vertical box
		// with a font right below
		
		
		// for this action handler increment the index variable
		// and be sure to correctly update the progress bar
		next.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (idx < problems.size() - 1) {
					back.setDisable(false);
				    idx++;
				    pointer = problems.get(idx);
				    ptext.setText(pointer.getDescription());
				    // must cast problemLen to double first 
				    // or else information is lost
				    double prog = idx / ((double) problemLen);
				    pb.setProgress(prog);
				    pbHeader.setText(String.format("Progress percentage: %.0f%%", prog*100));
				}
				
				// make an alert box for this case
				else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Completion");
					alert.setHeaderText(null);
					alert.setContentText("Are you sure you want to submit " +
					                     "your work as is and finish?");
					Optional<ButtonType> result = alert.showAndWait();
				    if (result.get() == ButtonType.OK) {
				    	pb.setProgress(1);
				    	scoreBoard(problemLen);
				    }
				    if (result.get() == ButtonType.CANCEL) {
				    	System.out.println("CANCEL case");
				    }
				}
			}
		});
		
		int boxX = 245;
		int boxY = 30;
		
		// OVERRIDE
		// RECTANGLE CODE HERE
		javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(205, 60);
		rect.setStroke(Color.BLACK);
		rect.setFill(null);
		rect.setStrokeWidth(3);
		rect.setArcWidth(25);
		rect.setArcHeight(25);
		
	    rect.setLayoutX(boxX-20);
		rect.setLayoutY(boxY);
		
		ptext.setLayoutX(boxX);
		ptext.setLayoutY(boxY);
		
		root.getChildren().add(ptext);
		root.getChildren().add(rect);
		root.getChildren().add(back);
		root.getChildren().add(next);
		root.getChildren().add(vb);
		
		canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
		//canvas = new Canvas(350, 350);
	    double xBound1 = 0;
	    double xBound2 = 300;
	    double yBound1 = 0;
		double yBound2 = 250;
		
		
		// now lets use setOnDragDetected for the canvas to enable user input
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		// make buttons to take a screenshot and a button to erase
		Button submit = new Button();
		submit.setId("traverse");
		submit.setText("Screenshot");
		submit.setPrefSize(90, 25);
		submit.setLayoutX(300);
		submit.setLayoutY(315);
		
		submit.setOnAction(new EventHandler<ActionEvent>() {
			 
            @Override
            public void handle(ActionEvent event) {
            	BufferedImage img = null;
				try {
					img = new Robot().createScreenCapture(new Rectangle(710, 300, 160, 170));
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		try {
					ImageIO.write(img, "png", new File("screenshot.png"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
		
		Button eraser = new Button();
		eraser.setId("btn");
		eraser.setPrefSize(75,  25);
		eraser.setLayoutX(200);
		eraser.setLayoutY(315);
		eraser.setText("Erase");
		eraser.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// must fill immediately after clearing
				gc.clearRect(0, 0, 250, 250);
				gc.setFill(Color.WHITE);
				gc.fillRect(0,  0,  250, 250);
			}
		});
		
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, 
				               new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				gc.beginPath();
				double currX = event.getX();
				double currY = event.getY();
				gc.moveTo(event.getX(), event.getY());
				if ((xBound1 <= currX && currX <= xBound2)
			      && (yBound1 <= currY && currY <= yBound2)) {
				    gc.stroke();
				}
			}
		});
		
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
		        new EventHandler<MouseEvent>(){
		    @Override
		    public void handle(MouseEvent event) {
		    	double currX = event.getX();
				double currY = event.getY();
				if ((xBound1 <= currX && currX <= xBound2)
				  && (yBound1 <= currY && currY <= yBound2)) {
		            gc.lineTo(event.getX(), event.getY());
		            gc.stroke();
		            gc.closePath();
		            gc.beginPath();
		            gc.moveTo(event.getX(), event.getY());
				}
		    }
		});
		
		canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, 
		        new EventHandler<MouseEvent>(){
		    @Override
		    public void handle(MouseEvent event) {
		    	double currX = event.getX();
				double currY = event.getY();
		    	if ((xBound1 <= currX && currX <= xBound2)
					      && (yBound1 <= currY && currY <= yBound2)) {
		            gc.lineTo(event.getX(), event.getY());
		            gc.stroke();
		            gc.closePath();
		    	}
		    }
		});
		
		// set up time labels and timer down here
		// Label time = new Label("Time");
		Label elapsed = new Label();
		elapsed.setId("elapsed");
		elapsed.setLayoutX(580);
		elapsed.setLayoutY(25);
		Timer timer = new Timer();
		//TimeManager manager = new TimeManager();
		TimerTask task = new TimerTask() {
			
			@Override public void run() {
				
			Platform.runLater(new Runnable() {
				
				// keep decrementing minutes and seconds 
				@Override public void run() {
					
					// must cancel immediately
					if (min == 0 && sec == 0) {
						elapsed.setText("Time up!");
						timer.cancel();
					}
					
					else if (sec == 0) {
					    min--;
					    sec = 59;
					    sec--;
						String time = String.format("%d : %d", min, sec);
						elapsed.setText(time);
					}
					
					else {
						sec--;
						String time = String.format("%d : %d", min, sec);
						elapsed.setText(time);
					}
				}
				
			});
			}
		};
		
		
		timer.scheduleAtFixedRate(task, 0, 1000);
		
		MenuBar mb = new MenuBar();
		
		// declare and initialize all pertinent canvas labels here
		Label eraseLabel = new Label("Erase");
		//Label colorLabel = new Label("Switch colors");
		Label helpLabel = new Label("Help");
		Label strokeLabel = new Label("Change stroke");
		ColorPicker cp = new ColorPicker();
		
		Menu e = new Menu();
		Menu c = new Menu("Switch colors");
		Menu h = new Menu();
		
		// for stroke simply invoke menu items
		Menu stroke = new Menu("Stroke thickness");
		MenuItem thin = new MenuItem("Thin");
		MenuItem medium = new MenuItem("Medium");
		MenuItem thick = new MenuItem("Thick");
		stroke.getItems().addAll(thin, medium, thick);
		
		MenuItem inner = new MenuItem();
		
		c.getItems().add(inner);
		e.setGraphic(eraseLabel);
		inner.setGraphic(cp);
		h.setGraphic(helpLabel);
		mb.getMenus().addAll(e, stroke, c, h);
		
		thin.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gc.setLineWidth(1);
			}
		});
		
		medium.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gc.setLineWidth(2);
			}
		});
		
		thick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gc.setLineWidth(3);
			}
		});
		
        inner.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) {
        		gc.setStroke(cp.getValue());
        	}
        });
        
		eraseLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// must fill immediately after clearing
				//System.out.println("ugh what the fuck");
				gc.clearRect(0, 0, 350, 250);
				gc.setFill(Color.WHITE);
				gc.fillRect(0,  0,  350, 250);
				// see if this works
				//gc.setLineWidth(5);
			}
		});
		
		helpLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Instructions");
				alert.setHeaderText("Welcome, student!");
				alert.setContentText("This is the workspace. The question to be answered is above\n"
				 + "the canvas. The canvas is where you are able to do your "
		         + "scratch work in. You have options to erase and to change stroke colors.\n\n"
				 + "The timer is in the top right-hand corner.\nWhen you are ready to submit, only "
		         + " have the FINAL answer in the canvas. Submit by pressing green submit button.\n\n"
				 + "Good luck!");
				alert.setWidth(250);
				alert.setHeight(375);
				alert.showAndWait();
			}
		});
        
		double canvasX = 150;
		
		//e.setOnAction();
		//Menu s = new Menu("Submit");
		//mb.getMenus().addAll(e);
		
		mb.setLayoutX(canvasX);
		mb.setLayoutY(145);
		mb.setPrefSize(350, 30);
		
		gc.setFill(Color.WHITE);
		gc.fillRect(0,  0,  350, 250);
		canvas.setLayoutX(canvasX);
		canvas.setLayoutY(175);
		root.getChildren().add(canvas);
		//root.getChildren().add(submit);
		//root.getChildren().add(eraser);
		//root.getChildren().add(hb);
		root.getChildren().add(elapsed);
		root.getChildren().add(mb);
		//root.getChildren().add(pb);
		
		//hb.getChildren().addAll(ptext);
		scene.setRoot(root);
	}
	
    @Override
    public void start(Stage primaryStage) {
    	
    	ref = primaryStage;
    	int posX = 300;
    	
        Label label = new Label();
        label.setId("font");
        label.setText("Welcome to Numera");
        label.setLayoutX(100);
        label.setLayoutY(75);
        
        // textfields for user and pass
        TextField user = new TextField();
        PasswordField pass = new PasswordField();
        
        user.setText("username");
        pass.setPromptText("password");
        
        // temporarily using a pane new (instead of stack pane)
        // for easier positioning of GUI components
        
        //Pane root = new Pane();
        
        root.setId("first");
        Pane root2 = new Pane();
        
        root.setId("first");
        root2.setId("second");
        
        // make two buttons
    	// one for login, another for registration
        Button loginBtn = new Button();
        Button registerBtn = new Button();
        
        // set widths
        loginBtn.setPrefSize(50, 25);
        registerBtn.setPrefSize(50, 25);
        
        loginBtn.setText("Login");
        //registerBtn.setText("register");
        
        loginBtn.setId("btn");
        //registerBtn.setId("btn2");
        
        scene = new Scene(root, 700, 600);
        
        // how images are added in javafx
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        
        final ImageView img1 = new ImageView();
        final ImageView img2 = new ImageView();
        File imgFile1 = new File("pictures/user.png");
        File imgFile2 = new File("pictures/password.png");
        Image image1 = new Image(imgFile1.toURI().toString());
        Image image2 = new Image(imgFile2.toURI().toString());
        img1.setImage(image1);
        img1.setFitHeight(35);
        img1.setFitWidth(35);
        img2.setImage(image2);
        img2.setFitHeight(35);
        img2.setFitWidth(35);
        
        img1.setLayoutX(posX/2);
        img1.setLayoutY(185);
        img2.setLayoutX(posX/2);
        img2.setLayoutY(230);
        
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            // we must change scenes here
            public void handle(ActionEvent event) {
            	boolean auth = login(user, pass);
                System.out.println("Changing the scene...");
                //scene.setRoot(root2);
                if (auth) {
                	screenSwap(root2);
                }
            }
        });
        
        registerBtn.setOnAction(new EventHandler<ActionEvent>() {
        	 
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Placeholder");
            }
        });
        
        
        //registerBtn.setLayoutX(125);
        //registerBtn.setLayoutY(200);
        loginBtn.setLayoutX(posX-75);
        loginBtn.setLayoutY(275);
        
        user.setLayoutX(posX-85);
        user.setLayoutY(200);
        
        pass.setLayoutX(posX-85);
        pass.setLayoutY(230);
        
        
        // root.getChildren().add(registerBtn);
        root.getChildren().add(user);
        root.getChildren().add(pass);
        
        root.getChildren().add(loginBtn);
        root.getChildren().add(img1);
        root.getChildren().add(img2);
        root.getChildren().add(label);
        
        // include the css sheet for styling
        scene.getStylesheets().add("style.css");
       
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
