import java.io.File;
import java.io.RandomAccessFile;

public class DML {



	public static void createDB(String database) {
		// TODO Auto-generated method stub
		try 
		{
			File database1 = new File("data\\"+database);
			
			if(database1.exists())
			{
				System.out.println("Database already exists");
				return;
			}
			database1.mkdir();
			DavisBase.currentDatabase=database;
			
			System.out.println("Database "+database+" created successfully.");
		}
		catch (Exception se) 
		{
			System.out.println("Unable to create catalog directory :"+se);			
		}		
	}
	
	
	
	public static void createTable(String table, String[] col)
	{
		try
		{	
			File cat = new File("data\\"+DavisBase.currentDatabase+"\\"+table);
			
			cat.mkdir();
			RandomAccessFile RAfile = new RandomAccessFile("data\\"+DavisBase.currentDatabase+"\\"+table+"\\"+table+".tbl", "rw");
			RAfile.setLength(Table.pageSize);
			RAfile.seek(0);
			RAfile.writeByte(0x0D);
			RAfile.close();
			
			RAfile = new RandomAccessFile("data\\catalog\\davisbase_tables.tbl", "rw");
			int numberOfPages = Table.pag(RAfile);
			int page = 1;
			for(int p = 1; p <= numberOfPages; p++)
			{
				int rm = Page.getRightMost(RAfile, p);
				if(rm == 0)
					page = p;
			}
			int[] keyArray = Page.getKeyArray(RAfile, page);
			int l = keyArray[0];
			for(int i = 0; i < keyArray.length; i++)
				if(l < keyArray[i])
					l = keyArray[i];
			RAfile.close();
			String[] values = {Integer.toString(l+1), DavisBase.currentDatabase+"."+table};
		insert("davisbase_tables", values);

			RandomAccessFile cfile = new RandomAccessFile("data\\catalog\\davisbase_columns.tbl", "rw");
			Buffer buffer = new Buffer();
			String[] columnName = {"rowid", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable"};
			String[] cmp = {};
			Table.filter(cfile, cmp, columnName, buffer);
			l = buffer.content.size();

			for(int i = 0; i < col.length; i++)
			{
				l = l + 1;
				String[] token = col[i].split(" ");
				String n = "YES";
				if(token.length > 2)
					n = "NO";
				String col_name = token[0];
				String dt = token[1].toUpperCase();
				String pos = Integer.toString(i+1);
				String[] v = {Integer.toString(l), DavisBase.currentDatabase+"."+table, col_name, dt, pos, n};
				insert("davisbase_columns", v);
			}
			cfile.close();
			RAfile.close();
		}
		catch(Exception e)
		{
			System.out.println("Error at createTable");
			e.printStackTrace();
		}
	}
	
	public static void insert(String table, String[] values)
	{
		try
		{
			RandomAccessFile file = new RandomAccessFile("data\\catalog\\"+table+".tbl", "rw");
			insert(file, table, values);
			file.close();

		}
		catch(Exception e)
		{
			System.out.println("Error in inserting the data");
			e.printStackTrace();
		}
	}
	
	public static void insert(RandomAccessFile file, String table, String[] values)
	{
		String[] dtype = Table.getDataType(table);
		String[] nullable = Table.getNullable(table);

		for(int i = 0; i < nullable.length; i++)
		{
			if(values[i].equals("null") && nullable[i].equals("NO"))
			{
				System.out.println("NULL value constraint violation");
				System.out.println();
				return;
			}
		}

		int key = new Integer(values[0]);
		int page = Table.searchKey(file, key);
		if(page != 0)
			if(Page.hasKey(file, page, key))
			{
				System.out.println("Uniqueness constraint violation");
				System.out.println();
				return;
			}
		if(page == 0)
			page = 1;


		byte[] stc = new byte[dtype.length-1];
		short plSize = (short) Table.calPayloadSize(table, values, stc);
		int cellSize = plSize + 6;
		int offset = Page.checkLeafSpace(file, page, cellSize);

		if(offset != -1)
		{
			InsertPages.insertLeafCell(file, page, offset, plSize, key, stc, values,table);
		}
		else
		{
			SplitPages.splitLeaf(file, page);
			insert(file, table, values);
		}
	}

}
