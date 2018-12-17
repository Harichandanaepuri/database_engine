import java.io.IOException;
import java.io.RandomAccessFile;

public class SetPages {
	
	public static void seeking(RandomAccessFile f,int loc)
	{
		try {
			f.seek(loc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static int getsize(int p,int num)
	{
		int p1=Page.pageSize;
		int num1=num;
		int p2=p;
		return (p2-1)*p1+num1;
	}
	public static void setParent(RandomAccessFile f, int p, int par)
	{
		try
		{
			int l=getsize(p,8);
			seeking(f,l);
			f.writeInt(par);
		}
		catch(Exception e)
		{
			System.out.println("Error at setParent");
		}
	}
	
	public static void setPointerLoc(RandomAccessFile f, long location, int par, int p)
	{
		try
		{
			
			if(location == 0)
			{
				int l=getsize(p,4);
				seeking(f,l);
				
			}
			else
			{
				f.seek(location);
			}
			f.writeInt(p);
		}
		catch(Exception e)
		{
			System.out.println("Error at setPointerLoc");
		}
	} 

	
	public static void setRightMost(RandomAccessFile f, int p, int right)
	{
		try
		{
			int l=getsize(p,4);
			seeking(f,l);
			f.writeInt(right);
		}
		catch(Exception e)
		{
			System.out.println("Error in setting rightmost");
		}

	}

	
	public static void setCellNumber(RandomAccessFile f, int p, byte number)
	{
		try
		{
			int l=getsize(p,1);
			seeking(f,l);
			f.writeByte(number);
		}
		catch(Exception e)
		{
			System.out.println("Error at setCellNumber");
		}
	}
	
	public static void setCellOffset(RandomAccessFile f, int p, int id1, int off)
	{
		try
		{
			seeking(f,(p-1)*Page.pageSize+12+id1*2);
			
			f.writeShort(off);
		}
		catch(Exception e)
		{
			System.out.println("Error at setCellOffset");
		}
	}
	
}
