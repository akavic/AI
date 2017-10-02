import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;

public class Testing {

  static boolean search(int a,int [] b)
  {
      boolean exists=false;

      for(int i=0; i<b.length;i++)
      {
        if(b[i]==a)
        exists=true;
      
      }
      return exists;
  }

  static int check(int x,int y)
  {

      if(x>y)
       return 1;

      if(y>x)
        return 2;

     else 
        return 3;
  }
  static int number(int  x)
  {
    x+=1;
   // x= Math.abs(x);
    return x;
  }

  public static void main(String[] args)  throws IOException {

    int p;
    /*File file = new File ("C:\\3rd year\\AI\\skeleton\\file1.txt");
    try{
    	 PrintWriter printWriter = new PrintWriter ("file1.txt");
    	printWriter.println ("hello");
    	printWriter.close ();   

    }
   // catch (IOException e)
  /{
      	//System.out.print("file not found");
    //}

           
      int x=-4;

      System.out.print(Math.abs(x));
      System.out.println(" the real number "+x);

          */
      /*int [] ab =  {1,2,3,4,5,6};
      if(search(3,ab)==true)
      {
        System.out.print("yes");
      }
      else
         System.out.print("no");*/

       /* if(check(4,3)==1 && check(4,2)==1)
        {
          System.out.print("yolo");
        }*/

       System.out.print( number(-9));

       
  }
}