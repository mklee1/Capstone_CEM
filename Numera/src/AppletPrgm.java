
import java.applet.Applet;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// you need to implement key listener 
// for user to be able to switch views
// resources:
// https://www.tutorialspoint.com/java/java_applet_basics.htm
// link shows that you need init 
public class AppletPrgm extends Applet implements KeyListener

{

    Graphics g;
    char c = '1'; // just initialize to 1
    // you technically need an init function
    // here you add a key listener
    public void init() {
    	addKeyListener(this);
    }
    
    // will paint based off of c
    // c initialized to 1
    public void paint(Graphics g) {
    	if (c == '1') {
    		paint1(g);
    	}
    	else if (c == '2') {
    		paint2(g);
    	}
    	else if (c == '3') {
    		paint3(g);
    	}
    	else if (c == '4') {
    		paint4(g);
    	}
    }
    
    // the key listener documentation
    // https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyListener.html
    public void keyPressed(KeyEvent evt){
    	// use this to get the int of char
        int code = evt.getKeyCode();
        c = (char) code; // turn back to char
        // you must use repaint
        // http://ecomputernotes.com/java/awt-and-applets/repaintmethod
        repaint();
    }
    
    public void paint1(Graphics g)

        {

               g.setColor(Color.yellow);

               g.fillOval(20,20,150,150);          // for drawing face

               g.setColor(Color.black);

               g.fillOval(50,60,15,25);              // Left Eye

               g.fillOval(120,60,15,25);    // Right Eye

               g.drawArc(55,95,78,50,0,-180);    // Smile

               g.drawLine(50,126,60,116);    // Smile arc1

               g.drawLine(128,115,139,126);      // Smile arc2

        }

//drawing huse and pane tree

        public void paint2(Graphics g)

         {

               int xp1[] = {110,180,30,110};

               int yp1[] = {30,100,100,30};

               g.setColor(Color.white);   //set color as white

               g.fillPolygon(xp1, yp1, 4);   //draw triangle as roof

               g.drawRect(50,100,160,250); //draw rectangle as house body

               g.drawRect(80,180,110,250);     //draw rectangle as door

               g.drawRect(130,160,150,200);   //draw rectangle as window

                g.drawLine(140,160,140,200);    //draw line for window partition

                g.drawLine(130,180,150,180);    //draw line for window partition

                g.setColor(new Color(0xA52A2A));           //set color as brown

                 g.drawRect(210,150,220,250);      //draw rectangle for pane tree

                 int xp2[] = {215,205,225,215};

             int yp2[] = {40,80,80,40};

                 int xp3[] = {215,200,230,215};

                 int yp3[] = {60,150,150,60};

                g.setColor(Color.green);

                g.fillPolygon(xp2, yp2, 4);    //draw triangle for pane tree

                g.fillPolygon(xp3, yp3, 4);    //draw triangle for pane tree

        }

//enemy function

       public void paint3(Graphics g)

        {

             g.setColor(Color.yellow); //set color as yellow

             g.fillOval(20,20,150,150);   //draw face

             g.setColor(Color.green);   //set color as green

             String msg = "TK";          //set message as TK

             g.drawString(msg,25,40); //draw message

        }

    

       public void paint4(Graphics g)

        {

           g.setColor(Color.red);

               g.fillOval(20,20,150,150);          // for drawing face

               g.setColor(Color.black);

               g.fillOval(50,60,15,25);              // Left Eye

               g.fillOval(120,60,15,25);    // Right Eye

               int x[] ={95,85,106,95};

        int y[] = {125,104,104,125};

          g.drawPolygon(x,y,4);   //nose

              g.drawLine(81,130,110,130);   //draw line

        }

    // just leave blank
    // methods must be added since we 
    // implement keylistener
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}