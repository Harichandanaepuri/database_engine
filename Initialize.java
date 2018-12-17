import java.io.File;

public class Initialize {


	public static void initializeSystemDatabase() {
		// TODO Auto-generated method stub
		try 
		{
			File d = new File("data");
			if (!d.exists()) 
			{
				d.mkdir();
			}
			File dbCatalog = new File("data\\catalog");
			if (dbCatalog.mkdir()) 
			{
				System.out.println("Catalog folder is not existing....Initializing catalog....");
				Table.initializeDataStore();
			} 
			else 
			{
				boolean x = false;
				String cols = "davisbase_columns.tbl";
				String tabs = "davisbase_tables.tbl";
				String[] tableList = dbCatalog.list();

				for(String x2:tableList)
				{
					if(x2.equals(tabs))
					{
						x=true;
					}
				}
				
				if (x!=true) 
				{
					System.out.println("'davisbase_tables.tbl' does not exit, initializing davisbase_tables");
					System.out.println();
					Table.initializeDataStore();
				}
				
				x = false;
				for(String x1:tableList)
				{
					if(x1.equals(cols))
					{
						x=true;
					}
				}
				
				if (x!=true) 
				{
					System.out.println("'davisbase_columns.tbl' does not exit, initializing davisbase_columns");
					System.out.println();
					Table.initializeDataStore();
				}
				

				
			}
		} 
		catch (Exception se) 
		{
			System.out.println("Catalog files not careated " + se);
		}
	}

	
	
}
