import java.io.RandomAccessFile;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Page
{
	public static int pageSize = 512;
	public static final String datePattern = "yyyy-MM-dd_HH:mm:ss";
	

	public static int findMidKey(RandomAccessFile file, int page)
	{
		int value = 0;
		try
		{
			file.seek((page-1)*pageSize);
			byte pType = file.readByte();
			
			int noOfCells = getCellNumber(file, page);
			
			int mid = (int) Math.ceil((double) noOfCells / 2);
			long loc = getCellLoc(file, page, mid-1);
			file.seek(loc);
			if(pType==0X05)
			{
				value = file.readInt(); 
				value = file.readInt();
				
			}
			else if(pType==0X0D)
			{
				value = file.readShort();
				value = file.readInt();
			
			}

		}
		catch(Exception e)
		{
			System.out.println("Error at findMidKey");
		}

		return value;
	}


	public static void sortCellArray(RandomAccessFile f, int p)
	{
		byte nu = getCellNumber(f, p);
		int[] keys = getKeyArray(f, p);
		short[] cells = getCellArray(f, p);
		int ltemp;
		short rtemp;
		

		int i1=1;
		while (i1 < nu) 
		{
			int j1=i1;
			while(j1 > 0)
			{
			    if(keys[j1-1]>keys[j1])
			    {

			       swap(keys[j1],keys[j1-1]);
			       swap(cells[j1],cells[j1-1]);
			    }
			    j1--;
			}
			i1++;
		}

		try
		{
			f.seek((p-1)*pageSize+12);
			
			int i=0;
			while(i<nu)
			{
				f.writeShort(cells[i]);
				i++;
			}
			
		}
		catch(Exception e)
		{
			System.out.println("Error at sortCellArray");
		}
	}

	private static void swap(int i, int j) {
		// TODO Auto-generated method stub
		int temp=i;
		i=j;
		j=temp;
		
	}


	public static int[] getKeyArray(RandomAccessFile f, int p)
	{
		int number = new Integer(getCellNumber(f, p));
		int[] arr = new int[number];

		try
		{
			f.seek((p-1)*pageSize);
			byte Type = f.readByte();
			byte off = 0;
			
			if(Type==0x0d)
			{
				off = 2;
			}
			else if(Type==0x05)
			{
				off = 4;
			}
			else
			{
				off=2;
			}
			
			
			int i=0;
			while(number>i)
			{
				long location = getCellLoc(f, p, i);
				f.seek(location+off);
				arr[i] = f.readInt();
				i++;
			}

		}
		catch(Exception e)
		{
			System.out.println("Error at getKeyArray");
		}

		return arr;
	}

	public static short[] getCellArray(RandomAccessFile f, int p)
	{
		int number = new Integer(getCellNumber(f, p));
		short[] array = new short[number];

		try
		{
			f.seek((p-1)*pageSize+12);
		
			int i=0;
			while(i<number)
			{
				array[i] = f.readShort();
				i++;
			
			}
		}
		catch(Exception e)
		{
			System.out.println("Error at getCellArray");
		}

		return array;
	}

	
	public static int getParent(RandomAccessFile f, int p)
	{
		int value = 0;

		try
		{
			f.seek((p-1)*pageSize+8);
			value = f.readInt();
		}
		catch(Exception e)
		{
			System.out.println("Error at getParent");
		}

		return value;
	}

	

	
	public static long getPointerLoc(RandomAccessFile f, int p, int par)
	{
		long value = 0;
		try
		{
			int numCells = new Integer(getCellNumber(f, par));
			
			
			int i=0;
			while(i<numCells)
			{
				long loc = getCellLoc(f, par, i);
				f.seek(loc);
				int childPage = f.readInt();
				if(childPage == p)
				{
					value = loc;
				}
				i++;
			}
			
		}
		catch(Exception e)
		{
			System.out.println("Error at getPointerLoc");
		}

		return value;
	}

	
	
	

	public static void updateLeafCell(RandomAccessFile f, int p, int off, int plsize, int keys, byte[] st, String[] values, String table)
	{
		try
		{
			String s;
			f.seek((p-1)*pageSize+off);
			f.writeShort(plsize);
			f.writeInt(keys);
			int coll = values.length - 1;
			f.writeByte(coll);
			f.write(st);
			int i=1;
			while(i < values.length)
			{
			
				if(st[i-1]==0x00)
				{
					f.writeByte(0);
					
				}
				else if(st[i-1]==0x01)
				{
					f.writeShort(0);
					
				}
				else if(st[i-1]==0x02)
				{
					f.writeInt(0);
					
				}
				else if(st[i-1]==0x03)
				{
					f.writeLong(0);
					
				}
				else if(st[i-1]==0x04)
				{
					f.writeByte(new Byte(values[i]));
					
				}
				else if(st[i-1]==0x05)
				{
					f.writeShort(new Short(values[i]));
					
				}
				else if(st[i-1]==0x06)
				{
					f.writeInt(new Integer(values[i]));
					
				}
				else if(st[i-1]==0x07)
				{
					f.writeLong(new Long(values[i]));
					
				}
				else if(st[i-1]==0x08)
				{
					f.writeFloat(new Float(values[i]));
					
				}
				else if(st[i-1]==0x09)
				{
					f.writeDouble(new Double(values[i]));
					
				}
				else if(st[i-1]==0x0A)
				{
					s = values[i];
					Date tempa = new SimpleDateFormat(datePattern).parse(s.substring(1, s.length()-1));
					long timea = tempa.getTime();
					f.writeLong(timea);
					
				}
				else if(st[i-1]==0x0B)
				{
					s = values[i];
					s = s.substring(1, s.length()-1);
					s = s+"_00:00:00";
					Date temp2a = new SimpleDateFormat(datePattern).parse(s);
					long time2a = temp2a.getTime();
					f.writeLong(time2a);
					
				}
				else
				{
					f.writeBytes(values[i]);
				}
				i++;
			}
		}
		catch(Exception e)
		{
			System.out.println("Error at Page.update");
			System.out.println(e);
		}
	}

	public static int getRightMost(RandomAccessFile f, int p)
	{
		int value = 0;

		try
		{
			f.seek((p-1)*pageSize+4);
			value = f.readInt();
		}
		catch(Exception e)
		{
			System.out.println("Error in rightmost");
		}

		return value;
	}

	
	
	public static byte getCellNumber(RandomAccessFile f, int p)
	{
		byte value = 0;
		try
		{
			f.seek((p-1)*pageSize+1);
			value = f.readByte();
		}
		catch(Exception e)
		{
			System.out.println(e);
			System.out.println("Error at getCellNumber");
		}
		return value;
	}

	

	public static boolean checkInteriorSpace(RandomAccessFile f, int p)
	{
		byte Cells = getCellNumber(f, p);
		if(Cells > 30)
			return true;
		else
			return false;
	}

	
	public static int checkLeafSpace(RandomAccessFile f, int p, int si)
	{
		int value = -1;

		try
		{
			f.seek((p-1)*pageSize+2);
			int content = f.readShort();
			if(content == 0)
				return pageSize - si;
			int numCells = getCellNumber(f, p);
			int space = content - 20 - 2*numCells;
			if(si < space)
				return content - si;
			
		}
		catch(Exception e)
		{
			System.out.println("Error at checkLeafSpace");
		}

		return value;
	}

	
	public static boolean hasKey(RandomAccessFile f, int p, int k)
	{
		int[] array = getKeyArray(f, p);
		for(int i : array)
			if(k == i)
				return true;
		return false;
	}

	
	public static long getCellLoc(RandomAccessFile f, int p, int id1)
	{
		long location = 0;
		try
		{
			f.seek((p-1)*pageSize+12+id1*2);
			short offset = f.readShort();
			long orig = (p-1)*pageSize;
			location = orig + offset;
		}
		catch(Exception e)
		{
			System.out.println("Error at getCellLoc");
		}
		return location;
	}

	public static short getCellOffset(RandomAccessFile f, int p, int id1)
	{
		short off = 0;
		try
		{
			f.seek((p-1)*pageSize+12+id1*2);
			off = f.readShort();
		}
		catch(Exception e)
		{
			System.out.println("Error at getCellOffset");
		}
		return off;
	}

	
}
