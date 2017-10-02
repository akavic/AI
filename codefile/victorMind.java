import java.util.*;
import java.awt.Point;
import org.w2mind.net.*;




public class victorMind implements Mind
{
	
	Point[] borders = PacSurvive.get_borders();
	int num_borders = PacSurvive.get_borderlength();


//====== Mind must respond to these methods: ==========================================================
//  newrun(), endrun()
//  getaction()
//======================================================================================================

	public void newrun()  throws RunError 
	{

	}

	public void endrun()  throws RunError
	{ 		
	}
	public boolean contains(Point [] p,Point p1,int count)
	{
		// USED TO CHECK IF AN POINT IS CONTAINED IN THE ARRAY
		boolean match=false;
		for(int i=0;i<count;i++)
		{
			if(p[i].x==p1.x && p[i].y==p1.y)
			{
				match=true;
			}
		}
		return match;
	}
	public Point north(Point p) 
	{
		//check position above pac man

		p.y=p.y-1;
		p.x=p.x;
		p.y=Math.abs(p.y);
		return p;
	}
	public Point south(Point p) 
	{
		//check position below pac man

		p.y=p.y+1;
		p.x=p.x;
		p.y=Math.abs(p.y);
		return p;
	}
	public Point west(Point p) 
	{
		//check position left pac man

		p.x=p.x-1;
		p.y=p.y;
		p.x=Math.abs(p.x);
		return p;
	}
	public Point east(Point p) 
	{
		//check position right pac man

		p.x=p.x+1;
		p.y=p.y;
		p.x=Math.abs(p.x);
		return p;
		 
	}
 	public int  checkborder(Point x1) 
 	{
 		// checks which direction the border is in

 		if(contains(borders,north(x1),num_borders)==true)
 			return 1;
 		else if(contains(borders,south(x1),num_borders)==true)
 			return 2;
 		else if(contains(borders,east(x1),num_borders)==true)
 			return 3;
 		else if (contains(borders,west(x1),num_borders)==true)
 			return 4;
 		else 
 			return 5; // it doesnt match anything
 	}

 	public int bestmove(Point pac,Point ghosta,Point ghostb,Point ghostc)
 	{
 		// method to find the closet ghost

 		int action=0;
 		int distanceX, distanceY;

 		int distanceax=ghosta.x-pac.x;
		int distanceay=ghosta.y-pac.y;
		int distancebx=ghostb.x-pac.x;
		int distanceby=ghostb.y-pac.y;
		int distancecx=ghostc.x-pac.x;
		int distancecy=ghostc.y-pac.y;

		int distanceghosta = (int) Math.sqrt(distanceax*distanceax + distanceay*distanceay);
		int distanceghostb = (int) Math.sqrt(distancebx*distancebx + distanceby*distanceby);
		int distanceghostc = (int) Math.sqrt(distancecx*distancecx + distancecy*distancecy);

		//ghost a is closer
	if(distanceghosta <= distanceghostb && distanceghosta <= distanceghostc){
		distanceX = distanceax;
		distanceY = distanceay;
	}

   //ghostb is the closest
   
	else if(distanceghostb <= distanceghostc && distanceghostb <= distanceghosta){
		distanceX = distancebx;
		distanceY = distanceby;
	}
	
	else {
		distanceX = distancecx;
		distanceY = distancecy;
	}
	



	//Do we want to move in the x or y direction
	//we move in which ever distance is greatest
	if(Math.abs(distanceX)>Math.abs(distanceY)){

		if(distanceX<0 && checkborder(pac)!=3){   
			action=PacSurvive.ACTION_RIGHT;
			// move right in the x direction (east) if there is no border
		}
		else if(distanceX<0 && checkborder(pac)!=4) {
			action=PacSurvive.ACTION_LEFT;
			// move right in the x direction (west) if there is no border
		}
		else if(distanceX<0 && checkborder(pac)==3)
		{
			action=PacSurvive.ACTION_LEFT;
			// if  the x direction (east) is blocked move in the opposite direction
		}
		else if(distanceX>0 && checkborder(pac)!=2 && distanceY>0)
		{
			// if  the x direction (south) is not blocked nd your close to a ghost move down
			action=PacSurvive.ACTION_DOWN;

		}
		else if(distanceX<0 && westandeast(pac)==true) //if your right and left and ghost is on you x location
		{
			if(distanceY<0 )
			{
				action=PacSurvive.ACTION_DOWN; //move down if your close to a ghost above you
			}
			else if(distanceY>0)
				action=PacSurvive.ACTION_UP; //move up if your close to a ghost below you
		}
	}
	else{	//checking ghosts above you
		if(distanceY<0 && checkborder(pac)!=2){
			action=PacSurvive.ACTION_DOWN;
			//
		}
		else if(distanceY<0 &&checkborder(pac)!=1){
			action=PacSurvive.ACTION_UP;
		}
		else if(distanceY<0 && northandSouth(pac)==true) // if blocked up and down 
		{
			if(distanceX<0 && checkborder(pac)!=4) // if west is not blocked 
				action=PacSurvive.ACTION_LEFT;
			else
				action=PacSurvive.ACTION_RIGHT;
		}
		else if(clear(pac) && Math.abs(distanceX)>Math.abs(distanceY)) //if no ghost is around north south east and west	
		{
			if(distanceX<0 && checkborder(pac)!=3)
			{
				action=PacSurvive.ACTION_RIGHT;
				// move right get away from the closet ghost on x axis
			}
			else if(distanceX<0 && checkborder(pac)==3)
			{
				action=PacSurvive.ACTION_LEFT;
				// move left get away from the closet ghost on x axis and border is clear
			}

		}
		if(distanceY<0 && checkborder(pac)!=2){
			action=PacSurvive.ACTION_DOWN;
		}
		else if(distanceY<0 &&checkborder(pac)!=1){
			action=PacSurvive.ACTION_UP;
		}

	}
		return action;
	 
 	}
 	boolean northandSouth(Point p)
 	{
 		//check if position north && south blocked
 		return  contains(borders,north(p),num_borders)&&contains(borders,south(p),num_borders);
 	}	
 	boolean westandeast(Point p)
 	{
 		//check if position WEST AND EAST blocked
 		return  contains(borders,west(p),num_borders)&&contains(borders,east(p),num_borders);
 	}
 	boolean clear(Point p)
 	{
 		//check if north south east and west clear
 		return !contains(borders,north(p),num_borders)&&contains(borders,south(p),num_borders)&&contains(borders,east(p),num_borders)&&contains(borders,west(p),num_borders);
 	}
 	boolean  westnorth(Point p)
 	{
 			/// pac should move right if this occurs
 		return contains(borders,north(p),num_borders)&&contains(borders,west(p),num_borders);
 	}
 	boolean eastnorth(Point p)
 	{
 			/// pac should move left if this occurs
 		return contains(borders,north(p),num_borders)&&contains(borders,east(p),num_borders);
 	}

	public Action getaction ( State state ) 
	{ 
		
    	
   		   

		String s = state.toString();		 
		String[] x = s.split(",");			   
	 	Point pac = new Point();
		Point ghosta = new Point();
		Point ghostb = new Point();
		Point ghostc = new Point();
		pac.x = Integer.parseInt(x[6]);
		pac.y = Integer.parseInt(x[7]);
		ghosta.x = Integer.parseInt (x[0]);
		ghosta.y = Integer.parseInt (x[1]);
		ghostb.x = Integer.parseInt(x[2]);
		ghostb.y = Integer.parseInt(x[3]);
		ghostc.x = Integer.parseInt(x[4]);
		ghostc.y = Integer.parseInt(x[5]);
		
	

	   //Get an array of all the illegal Positions.
		//Point [] borders = PacSurvive.get_borders();
	   //get the number of illegal borders in the array

		int move;
		System.out.print("("+pac.x+","+pac.y+")");
		move=bestmove(pac,ghosta,ghostb,ghostc);
		System.out.println();
		System.out.println(" action is: "+move);
		System.out.println();
		String a = String.format("%d", move);
		return new Action(a);	

		
			 
	}

		 
}



