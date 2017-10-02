class Points
{
	int x,y;

	Points(int x,int y)
	{
		this.x=x;
		this.y=y;
	}
	void print()
	{
		System.out.print(x+" "+y);
	}
	boolean equal(Points p)
	{
		boolean equal=false;
		if(p.x==x&&p.y==y)
			equal=true;
		else
			equal=false;
		return equal;
	}
	static Points increasePoint(Points p)
	{
		p.x=p.x+1;
		p.y=p.y+1;
		return p;
	}
	static Points increase(Points p)
	{
		p.x=p.x+1;
		p.y=p.y+1;
		return p;
	}
	static boolean validPoint(Points p1)
	{
		boolean yo=false;
		if(increasePoint(p1)==increase(p1))
		{
			yo =true;
		}
		return yo;
	}


	public static void main(String [] args)
	{
		Points [] borders={new Points(1,3),new Points(1,4),new Points(1,5),new Points(1,6),new Points(1,7)};

		Points pac = new Points(1,5);
		Points vic=new Points(1,5);

		/*boolean itexsit=false;
		int bob;
		for(int i=0;i<borders.length;i++)
		{
			if(pac.x==borders[i].x&& pac.y==borders[i].y)
				itexsit=true;
			else
				 bob =2;
		}

		if(pac.equal(vic)==true)
		{
			System.out.print("yeah it is");
		}
		else
			System.out.print("nope");

		increasePoint(pac);
		pac.print();*/
		if(validPoint(pac)==true)
		{
			System.out.print("yeah it is");
		}

		
	}	
}