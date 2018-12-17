import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertPages {

	public static void seeking(RandomAccessFile f,int x)
	{
		try {
			f.seek(x);
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
	
	
	
	public static void insertInteriorCell(RandomAccessFile f, int p, int ch, int keys)
	{
		try
		{
	
			int x=getsize(p,2);
			seeking(f,x);
			
			short cont = f.readShort();
			if(cont != 0)
				cont = (short)(cont - 8);
			else
				cont = 512;
			
			int y=getsize(p,cont);
			seeking(f,y);
			f.writeInt(ch);
			f.writeInt(keys);
			
			int u=getsize(p,2);
			seeking(f,u);
			
			f.writeShort(cont);
			
			byte num = Page.getCellNumber(f, p);
			SetPages.setCellOffset(f, p ,num, cont);
			
			num = (byte) (num + 1);
			SetPages.setCellNumber(f, p, num);

		}
		catch(Exception e)
		{
			System.out.println("Error at insertInteriorCell");
		}
	}

	
	public static void insertLeafCell(RandomAccessFile f, int p, int off, short pl, int k, byte[] st, String[] values, String tableName)
	{
		try
		{
			String s1;
			int x=getsize(p,off);
			seeking(f,x);
			
			String[] colName = Table.getColName(tableName);
			if(!tableName.equals("davisbase_columns") && !tableName.equals("davisbase_tables"))
			{
				
				RandomAccessFile IndexFile = new RandomAccessFile("data\\"+DavisBase.currentDatabase+"\\"+tableName+"\\"+colName[0]+".ndx", "rw");
				IndexFile.seek(IndexFile.length());
				IndexFile.writeInt(k);
				IndexFile.writeLong(f.getFilePointer());
				IndexFile.close();
				
			
				for(int i = 1; i < values.length; i++)					
				{
					IndexFile = new RandomAccessFile("data\\"+DavisBase.currentDatabase+"\\"+tableName+"\\"+colName[i]+".ndx", "rw");
					IndexFile.seek(IndexFile.length());
					
					if(st[i-1]==0x03)
					{
						IndexFile.writeLong(0);
					}
					else if(st[i-1]==0x04)
					{

						IndexFile.writeByte(new Byte(values[i]));
						
					}
					else if(st[i-1]==0x05)
					{
						IndexFile.writeShort(new Short(values[i]));
					}
					else if(st[i-1]==0x06)
					{
						IndexFile.writeInt(new Integer(values[i]));
					}
					else if(st[i-1]==0x00)
					{
						IndexFile.writeByte(0);
					}
					else if(st[i-1]==0x01)
					{
						IndexFile.writeShort(0);
					}
					else if(st[i-1]==0x02)
					{
						IndexFile.writeInt(0);
					}
					else if(st[i-1]==0x07)
					{
						IndexFile.writeLong(new Long(values[i]));
					}
					else if(st[i-1]==0x08)
					{
						IndexFile.writeFloat(new Float(values[i]));
					}
					else if(st[i-1]==0x09)
					{
						IndexFile.writeDouble(new Double(values[i]));
					}
					else if(st[i-1]==0x0A)
					{
						s1 = values[i];
						
						Date temp1 = new SimpleDateFormat(Page.datePattern).parse(s1);
						long time1 = temp1.getTime();
						IndexFile.writeLong(time1);
					}
					else if(st[i-1]==0x0B)
					{
						s1 = values[i];
						
						s1 = s1+"_00:00:00";
						Date temp9 = new SimpleDateFormat(Page.datePattern).parse(s1);
						long time9 = temp9.getTime();
						IndexFile.writeLong(time9);
					}
					else
					{
						f.writeBytes(values[i]);
					}
						
					IndexFile.writeLong(f.getFilePointer());
					IndexFile.close();
				}
				
			}
			
			int j=getsize(p,off);
			seeking(f,j);
			
			f.writeShort(pl);
			f.writeInt(k);
			int col = values.length - 1;
			
			
			f.writeByte(col);
			f.write(st);
			
			int i=1;
			while(i < values.length)				
			{	
				
				if(st[i-1]==0X04)
				{
					f.writeByte(new Byte(values[i]));
				}
				else if(st[i-1]==0X05)
				{
					f.writeShort(new Short(values[i]));
				}
				else if(st[i-1]==0X06)
				{
					f.writeInt(new Integer(values[i]));
				}
				else if(st[i-1]==0X07)
				{
					f.writeLong(new Long(values[i]));
				}
				else if(st[i-1]==0X00)
				{
					f.writeByte(0);
				}
				
				
				
				else if(st[i-1]==0X09)
				{
					f.writeDouble(new Double(values[i]));
					
				}
				else if(st[i-1]==0X01)
				{
					f.writeShort(0);
				}
				else if(st[i-1]==0X02)
				{
					f.writeInt(0);
				}
				else if(st[i-1]==0X03)
				{
					f.writeLong(0);
				}
				else if(st[i-1]==0X08)
				{
					f.writeFloat(new Float(values[i]));
				}
				
				else if(st[i-1]==0X0A)
				{
					s1 = values[i];
					
					Date temp = new SimpleDateFormat(Page.datePattern).parse(s1);
					long time = temp.getTime();
					f.writeLong(time);
				}
				else if(st[i-1]==0X0B)
				{
					s1 = values[i];
					
					s1 = s1+"_00:00:00";
					Date temp4 = new SimpleDateFormat(Page.datePattern).parse(s1);
					long time4 = temp4.getTime();
					f.writeLong(time4);
				}

				else 
			
						f.writeBytes(values[i]);
				//		break;
				i++;
			}
			
			int n = Page.getCellNumber(f, p);
			byte tmp = (byte) (n+1);
			SetPages.setCellNumber(f, p, tmp);
			int h2=getsize(p,12+n*2);
			seeking(f,h2);
			
			f.writeShort(off);
			int h1=getsize(p,2);
			seeking(f,h1);
			
			int con = f.readShort();
			if(con >= off || con == 0)
			{
				int h=getsize(p,2);
				seeking(f,h);
				
				f.writeShort(off);
			}
		}
		catch(Exception e)
		{
			System.out.println("Error at insertLeafCell");
			e.printStackTrace();
		}
	}
	
	
}
