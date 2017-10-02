import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.awt.Point;
import org.w2mind.net.*;

 
//The Aim of the game is to survive as long as you can.
 
public class PacSurvive extends AbstractWorld {

	public static final int GRID_SIZE = 16;		
   //character positions 	
    private Point ghostapos, ghostbpos, ghostcpos, pacpos; 
   //used for score	
	int steps;			 
	private List <String> scorecols;	 
   //used to store all the points that pacman or ghosts cannot move onto
    private static Point[] border;
   //number of elements in border array.
	private static int num_border = 0;
   //if true the game ends
	private boolean caught;

	public  static final int ACTION_LEFT  	= 0;
	public  static final int ACTION_RIGHT 	= 1;
	public  static final int ACTION_UP   	= 2;
	public 	static final int ACTION_DOWN 	= 3;
	public  static final int NO_ACTIONS 	= 4;

	String SUPPORT_DIR 		= "images";	
	String IMG_GHOSTA 		= SUPPORT_DIR + "/ghostred.png";
	String IMG_GHOSTB 		= SUPPORT_DIR + "/ghostred.png";
	String IMG_GHOSTC 		= SUPPORT_DIR + "/ghostred.png";
	String IMG_PAC 			= SUPPORT_DIR + "/pacman.png";
	String IMG_BORDER 	    = SUPPORT_DIR + "/border.png";	
	String IMG_GAMEOVER     = SUPPORT_DIR + "/gameover.jpg";

	private transient ArrayList <BufferedImage> buf;
	private transient InputStream ghostaStream, ghostbStream, ghostcStream, pacStream, caughtStream;		
	private transient BufferedImage ghostaImg, ghostbImg, ghostcImg, pacImg, game_overImg; 
	int imgwidth = 250; 
	int imgheight = 250;

   //returns a valid random position
	Point randomPos(){
	  Random r = new Random();
	  int x,y;
      do {
		x = r.nextInt(GRID_SIZE);
	    y = r.nextInt(GRID_SIZE);
	  } while(illegal(x,y));
	  
      return new Point(x,y);
	}
   
   //used by mind, returns all illegal positions
	public static Point[] get_borders(){
		return border;
	}
   //returns amount of illegal positions
	public static int get_borderlength(){
		return num_border;
	}
	
   //initialise characterpositions
	private void initPos(){ 								  
	  do { 
	     pacpos = randomPos();
	     ghostapos = randomPos(); 
	     ghostbpos = randomPos(); 
		 ghostcpos = randomPos(); 
	  
	  } while (ghostapos.equals(pacpos) || ghostbpos.equals(pacpos) 
			|| ghostcpos.equals(pacpos) || ghostapos.equals(ghostbpos)
			|| ghostapos.equals(ghostcpos) 
			|| ghostbpos.equals(ghostcpos));		 
	}
   
   //returns true if position is illegal   
	public static boolean illegal(int x, int y){
	  for(int i = 0 ; i < num_border; i++){
		if(x == border[i].x && y == border[i].y) {
			return true;
		}
	  }
	  return false;
	}
   
   //used by ghosts and possibly pac depending on the writer of pac.
   //move is passed in and it leads to an illegal position, choose either
   //of the best next moves, if those two are illegal, take the worst 
   //possible direction. E.g. Can't move up, choose either left or right,
   //if both fail then you must move down.
   
	private void move ( Point startPos, int direction ){
	  int decide = 100;
	  Random r = new Random();
	  
	  if(direction == ACTION_LEFT){
		if(!illegal(((startPos.x - 1 + GRID_SIZE) % GRID_SIZE),startPos.y))
		  startPos.x=((startPos.x - 1 + GRID_SIZE) % GRID_SIZE);
		else {
		  //50% chance left, 50% chance right if one direction is illegal just take the other.
          decide = r.nextInt(decide);
		 //if up isnt illegal go up. 
		  if (decide < 50 && !illegal(startPos.x,(startPos.y - 1 + GRID_SIZE) % GRID_SIZE)) startPos.y=((startPos.y - 1 + GRID_SIZE) % GRID_SIZE);
		 //otherwise try down
		  else if (!illegal(startPos.x,(startPos.y + 1 + GRID_SIZE) % GRID_SIZE)) startPos.y=((startPos.y + 1 + GRID_SIZE) % GRID_SIZE);
		  
		  else if (!illegal(startPos.x,(startPos.y - 1 + GRID_SIZE) % GRID_SIZE)) startPos.y=((startPos.y - 1 + GRID_SIZE) % GRID_SIZE);
		 //otherwise you must go right
		  else startPos.x=((startPos.x + 1 + GRID_SIZE) % GRID_SIZE);
		}
	  }
	  
	  if(direction == ACTION_RIGHT){
	     if(!illegal(((startPos.x + 1 + GRID_SIZE) % GRID_SIZE),startPos.y)) 
		    startPos.x=((startPos.x + 1 + GRID_SIZE) % GRID_SIZE);
		 else{
		   //50% chance go down, 50% chance go up
			decide = r.nextInt(decide);
		   //remember to check if illegal or not
			if (decide < 50 && !illegal(startPos.x,(startPos.y + 1 + GRID_SIZE) % GRID_SIZE)) startPos.y=((startPos.y + 1 + GRID_SIZE) % GRID_SIZE);
		   //try up
			else if (!illegal(startPos.x,((startPos.y - 1 + GRID_SIZE) % GRID_SIZE))) startPos.y=((startPos.y - 1 + GRID_SIZE) % GRID_SIZE);
			
			else if ( !illegal(startPos.x,(startPos.y + 1 + GRID_SIZE) % GRID_SIZE)) startPos.y=((startPos.y + 1 + GRID_SIZE) % GRID_SIZE);
		   //you must try left
		   else startPos.x=((startPos.x - 1 + GRID_SIZE) % GRID_SIZE);
		 }
	  }
	  
	  if(direction == ACTION_UP){
		if (!illegal(startPos.x,((startPos.y - 1 + GRID_SIZE) % GRID_SIZE)))
		   startPos.y=((startPos.y - 1 + GRID_SIZE) % GRID_SIZE);
		else{
		   decide = r.nextInt(decide);
		  //50% chance go left, 50% go left, otherwise you must go down
		   if(decide < 50 && !illegal((startPos.x - 1 + GRID_SIZE) % GRID_SIZE,startPos.y)) 
		      startPos.x=((startPos.x - 1 + GRID_SIZE) % GRID_SIZE);
			   
		   else if(!illegal((startPos.x + 1 + GRID_SIZE) % GRID_SIZE, startPos.y))
		      startPos.x=((startPos.x + 1 + GRID_SIZE) % GRID_SIZE);
			  
		   else if (!illegal((startPos.x - 1 + GRID_SIZE) % GRID_SIZE,startPos.y)) 
		      startPos.x=((startPos.x - 1 + GRID_SIZE) % GRID_SIZE);
			   
		   else startPos.y = ((startPos.y + 1 + GRID_SIZE) % GRID_SIZE);
		}
	  }
	  
	  if(direction == ACTION_DOWN){
	     if (!illegal(startPos.x,((startPos.y + 1 + GRID_SIZE) % GRID_SIZE))) 
		    startPos.y=((startPos.y + 1 + GRID_SIZE) % GRID_SIZE);
		 else{
		    decide = r.nextInt(decide);
			if (decide < 50 && !illegal((startPos.x + 1 + GRID_SIZE) % GRID_SIZE,startPos.y)) 
			   startPos.x=((startPos.x + 1 + GRID_SIZE) % GRID_SIZE);
			else if (!illegal((startPos.x - 1 + GRID_SIZE) % GRID_SIZE,startPos.y)) 
			   startPos.x=((startPos.x - 1 + GRID_SIZE) % GRID_SIZE);
			   
			else if ( !illegal((startPos.x + 1 + GRID_SIZE) % GRID_SIZE,startPos.y)) 
			   startPos.x=((startPos.x + 1 + GRID_SIZE) % GRID_SIZE);
		   //otherwise try up
			else startPos.y=((startPos.y - 1 + GRID_SIZE) % GRID_SIZE);
		 }
	  }
	}

   //is the game over?
	private boolean runFinished() { 
	  return ( caught ); 
	}		 
	
	private void initImages(){
	   if ( imagesDesired ){
	      buf = new ArrayList <BufferedImage> ();
          if ( ghostaStream  == null ){
	        try{
		      ImageIO.setUseCache(false);

		      ghostaStream  = getClass().getResourceAsStream ( IMG_GHOSTA );
			  ghostbStream  = getClass().getResourceAsStream ( IMG_GHOSTB );
			  ghostcStream  = getClass().getResourceAsStream ( IMG_GHOSTC );
		      pacStream    = getClass().getResourceAsStream ( IMG_PAC );
		      caughtStream = getClass().getResourceAsStream ( IMG_BORDER );
		
		      ghostaImg    = javax.imageio.ImageIO.read( ghostaStream );
			  ghostbImg    = javax.imageio.ImageIO.read( ghostbStream );
			  ghostcImg    = javax.imageio.ImageIO.read( ghostcStream );
		      pacImg       = javax.imageio.ImageIO.read( pacStream );
		      game_overImg = javax.imageio.ImageIO.read( caughtStream );

	     	  imgwidth  = ghostaImg.getWidth();
		      imgheight = ghostaImg.getHeight();
	        } catch ( IOException e ) {}
	      }
	   }
	}
	
   //draw a border at position p using img.
	private void addborder(BufferedImage img,Point p){
	   img.createGraphics().drawImage ( game_overImg, (imgwidth*p.x), (imgwidth*p.y), null );
	}
	
	private void addPoint( int x, int y){
		border[num_border] = new Point(x,y);
	    num_border++;
	}
	
	private void addImage(){
	   BufferedImage img = new BufferedImage ( ( imgwidth * GRID_SIZE ), (imgheight * GRID_SIZE), BufferedImage.TYPE_INT_RGB );
	   if ( imagesDesired ){
		 for(int i = 0; i < num_border; i++){
			addborder(img, border[i]);
		 }
		 if (ghostapos == pacpos || ghostbpos == pacpos || ghostcpos == pacpos )
		   img.createGraphics().drawImage ( game_overImg, 0, 0, null );
		 else{
		   img.createGraphics().drawImage ( ghostaImg, (imgwidth * ghostapos.x), (imgwidth * ghostapos.y), null );
		   img.createGraphics().drawImage ( ghostbImg, (imgwidth * ghostbpos.x), (imgwidth * ghostbpos.y), null );
		   img.createGraphics().drawImage ( ghostcImg, (imgwidth * ghostcpos.x), (imgwidth * ghostcpos.y), null );
		   img.createGraphics().drawImage ( pacImg, (imgwidth * pacpos.x), (imgwidth * pacpos.y), null );
	   	 }
		 buf.add(img);	
	   }
	}

	public void newrun() throws RunError{
		ghostapos = new Point();
		ghostbpos = new Point();
		ghostcpos = new Point();
		pacpos = new Point();
		border = new Point[GRID_SIZE*GRID_SIZE];
		steps = 0;
		caught = false;
		
	   //uppermost border
		  int j =0;
		  for(int i = 0 ; i < 16 ; i++){
		    addPoint(i,j);
		  }
	     //lowermost border
		  j = 15;
		  for(int i = 0 ; i < 16 ; i++){
		    addPoint(i,j);
		  }
	     //leftmost border
		  int i = 0;
		  for(j=0; j<16 ; j++){
		    addPoint(i,j);
		  }
	     //rightmost border
		  i = 15;
		  for(j=0; j<16 ; j++){
		    addPoint(i,j);
		  }
		
         //left upper square
		  addPoint(3, 4);
		  addPoint(3, 5);
		  addPoint(4, 4);
		  addPoint(4, 5);
		 
		  addPoint(6, 4);
		  addPoint(7, 5);
		
		  //left upper line
		  addPoint(2, 2);
		  addPoint(3, 2);
		  addPoint(4, 2);
		  addPoint(5, 2);
		
		  //middle upper border line
		  addPoint(7, 2);
		  addPoint(7, 1);
		  addPoint(8, 1);
		  addPoint(8, 2);
		
	     //right upper square
		  addPoint(12, 4);
		  addPoint(12, 5);
		  addPoint(11, 4);
		  addPoint(11, 5);
		
	     //right upper short line
		  addPoint(9, 5);
		  addPoint(9, 4);
		
	     //right upper line
	      addPoint(11, 2);
          addPoint(10, 2);
          addPoint(12, 2);
          addPoint(13, 2);		
		
	     //left middle line
	      addPoint(2, 8);
	      addPoint(3, 7);
		  addPoint(3, 8);
		  addPoint(4, 7);
		
	     //right middle line
	      addPoint(11, 8);
	      addPoint(12, 7);
		  addPoint(12, 8);
		  addPoint(13, 7);
		  
	     //left lower square
		  addPoint(3, 10);
		  addPoint(3, 11);
		  addPoint(4, 10);
		  addPoint(4, 11);
		
	     //left lower short line
	      addPoint(9, 10);
		  addPoint(9, 11);
		
		 //left lower diag
		  addPoint(6, 11);
		  addPoint(7, 10);
		
	     //lower left line
		  addPoint(3, 13);
		  addPoint(4, 13);
		  addPoint(2, 13);
		  addPoint(5, 13);
		
		 //middle lower border
		  addPoint(7, 14);
		  addPoint(7, 13);
		  addPoint(8, 14);
          addPoint(8, 13);
		
	     //right lower square
		  addPoint(12, 10);
		  addPoint(12, 11);
		  addPoint(11, 10);
		  addPoint(11, 11);
		
	     //right lower line
	      addPoint(10, 13);
	      addPoint(11, 13);
	      addPoint(12, 13);
	      addPoint(13, 13);
		 
		initPos();

		scorecols = new LinkedList <String> ();		
		scorecols.add("Steps");
	}

	public void endrun() throws RunError{}

	public State getstate() throws RunError{
	   String x = String.format ( "%d,%d,%d,%d,%d,%d,%d,%d", ghostapos.x, ghostapos.y, ghostbpos.x, ghostbpos.y, 
			ghostcpos.x, ghostcpos.y, pacpos.x, pacpos.y);
			
	   return new State ( x );
	}

	public State takeaction ( Action action ) throws RunError {   
		initImages();
		addImage();	
		String 	s = action.toString();		 
		String[] a = s.split(",");		

		int i = Integer.parseInt ( a[0] );		 
		move (pacpos, i);

		//addImage(); 
		
        //check if pacman was caught, could have ran into a ghost
		if (ghostapos.equals(pacpos) || ghostbpos.equals(pacpos) || ghostcpos.equals(pacpos) ){
		 addImage();
		 caught = true;
		}

		else{
			int move;
			int distanceX=pacpos.x-ghostapos.x;
			int distanceY=pacpos.y-ghostapos.y;

			if(Math.abs(distanceX)>Math.abs(distanceY)){		
			   if(distanceX<0){
			      move=ACTION_LEFT;
			   }
			   else{
			      move=ACTION_RIGHT;
			   }
			}
			else{		
			   if(distanceY<0){
			      move=ACTION_UP;
			   }
			   else{
			      move=ACTION_DOWN;
			   }
			}

       		move(ghostapos, move);
			
			distanceX=pacpos.x-ghostbpos.x;
			distanceY=pacpos.y-ghostbpos.y;

			if(Math.abs(distanceX)>Math.abs(distanceY)){		
			   if(distanceX<0){
			      move=ACTION_LEFT;
			   }
			   else{
			      move=ACTION_RIGHT;
			   }
			}
			else{		
			   if(distanceY<0){
			      move=ACTION_UP;
			   }
			   else{
			      move=ACTION_DOWN;
			   }
			}

       		move(ghostbpos, move);
			
			distanceX=pacpos.x-ghostcpos.x;
			distanceY=pacpos.y-ghostcpos.y;

			if(Math.abs(distanceX)>Math.abs(distanceY)){		
			   if(distanceX<0){
			      move=ACTION_LEFT;
			   }
			   else{
			      move=ACTION_RIGHT;
			   }
			}
			else{		
			   if(distanceY<0){
			      move=ACTION_UP;
			   }
			   else{
			      move=ACTION_DOWN;
			   }
			}

       		move(ghostcpos, move);
			
		 //check again if pacman is caught, ghost could have ran into pacman
		  if (ghostapos.equals(pacpos) || ghostbpos.equals(pacpos) || ghostcpos.equals(pacpos) ){
		    addImage(); 
			caught = true;
		  }
		}
		
		steps++;

		if (runFinished())
		 addImage();
		 
		return getstate();
	}
   
   //Score in this world being the number of steps survived
	public Score getscore() throws RunError{
	   String s = String.format ( "%d",steps);
	 
	   List <Comparable> values = new LinkedList <Comparable> ();
	   values.add(steps);

	   return new Score ( s, runFinished(), scorecols, values );
	}

	// Return image(s) of World.
	// Image may show more information than State (what the Mind sees).
	// This method returns a list of images for this step - we allow multiple images per step.
	// e.g. You move, get one image, the opponent moves, next image, your turn again (this is 2 images per step).
	// This list of images should normally be built in takeaction method.
	// The first image on the list for this step should be the image before we take the action on this step.

	public ArrayList <BufferedImage> getimage() throws RunError{
	   return buf;
	}
}