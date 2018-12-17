import java.io.IOException;
import java.io.RandomAccessFile;

public class MakePages {

	public static int makeInterior(RandomAccessFile f)
	{
		int noOfPages = 0;
		try
		{
			noOfPages = calNoofPages(f);
			noOfPages = noOfPages + 1;
			int psize=Page.pageSize;
			f.setLength(psize * noOfPages);
			seeking(f,(noOfPages-1) * noOfPages);
			f.writeByte(0x05);  
		}
		catch(Exception e)
		{
			System.out.println("Error in creating the Interior Page");
		}

		return noOfPages;
	}

	
	
	public static int makeLeaf(RandomAccessFile f)
	{
		int noOfPages = 0;
		try
		{
			noOfPages = calNoofPages(f);
			noOfPages = noOfPages + 1;
			int psize=calNoofPages(f);
			seeking(f,(noOfPages-1)*Page.pageSize);
			f.writeByte(0x0D); 
		}
		catch(Exception e)
		{
			System.out.println("Error in creating the Leaf Page");
		}

		return noOfPages;

	}
	
	public static int calNoofPages(RandomAccessFile f) throws IOException
	{
		long len=f.length();
		long h=(new Long(Page.pageSize));
		return (int)(len/h);
	}
	public static void seeking(RandomAccessFile f,int x)
	{
		try {
			f.seek(x);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
