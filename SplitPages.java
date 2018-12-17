import java.io.IOException;
import java.io.RandomAccessFile;

public class SplitPages {

	public static void seeking(long x,RandomAccessFile f)
	{
		try {
			f.seek(x);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void splitLeafPage(RandomAccessFile f, int current, int new_p)
	{
		try
		{			
			int noOfCells = Page.getCellNumber(f, current);			
			int middle = (int) Math.ceil((double) noOfCells / 2);
			int cellA = middle - 1;
			int cellB = noOfCells - cellA;
			int cont = 512;

			int i=cellA;
			while(i < noOfCells)
			{
				long loc = Page.getCellLoc(f, current, i);
				
				seeking(loc,f);
				int cellSize = f.readShort()+6;
				cont = cont - cellSize;
				seeking(loc,f);
				byte[] cell = new byte[cellSize];
				f.read(cell);
				
				seeking((new_p-1)*Page.pageSize+cont,f);
				f.write(cell);
				
				SetPages.setCellOffset(f, new_p, i - cellA, cont);
				i++;
			}

			seeking((new_p-1)*Page.pageSize+2,f);
			f.writeShort(cont);

			
			short offset = Page.getCellOffset(f, current, cellA-1);
			seeking((current-1)*Page.pageSize+2,f);
			
			f.writeShort(offset);

			
			int rightMost = Page.getRightMost(f, current);
			SetPages.setRightMost(f, new_p, rightMost);
			SetPages.setRightMost(f, current, new_p);
			
			int parent = Page.getParent(f, current);
			SetPages.setParent(f, new_p, parent);

			
			byte num = (byte) cellA;
			SetPages.setCellNumber(f, current, num);
			num = (byte) cellB;
			SetPages.setCellNumber(f, new_p, num);
		}
		catch(Exception e)
		{
			System.out.println("Error at splitLeafPage");
			e.printStackTrace();
		}
	}
	
	public static void splitInteriorPage(RandomAccessFile f, int current, int new_p)
	{
		try
		{
			
			int noOfCells = Page.getCellNumber(f, current);
			
			int m = (int) Math.ceil((double) noOfCells / 2);

			int cellA = m - 1;
			int cellB = noOfCells - cellA - 1;
			short cont = 512;

			int i=cellA+1;
			while(noOfCells>i)
			{
				long loc = Page.getCellLoc(f, current, i);
				
				short cellSize = 8;
				cont = (short)(cont - cellSize);
				
				f.seek(loc);
				byte[] cell = new byte[cellSize];
				f.read(cell);
				
				f.seek((new_p-1)*Page.pageSize+cont);
				f.write(cell);
				
				f.seek(loc);
				int page = f.readInt();
				SetPages.setParent(f, page, new_p);
				
				SetPages.setCellOffset(f, new_p, i - (cellA + 1), cont);
				i++;
			}
			
			int temporary = Page.getRightMost(f, current);
			SetPages.setRightMost(f, new_p, temporary);
			
			long mid2 = Page.getCellLoc(f, current, m - 1);
			f.seek(mid2);
			temporary = f.readInt();
			SetPages.setRightMost(f, current, temporary);
			
			f.seek((new_p-1)*Page.pageSize+2);
			f.writeShort(cont);
			
			short off_set = Page.getCellOffset(f, current, cellA-1);
			f.seek((current-1)*Page.pageSize+2);
			f.writeShort(off_set);

			
			int par = Page.getParent(f, current);
			SetPages.setParent(f, new_p, par);
			
			byte num1 = (byte) cellA;
			SetPages.setCellNumber(f, current, num1);
			num1 = (byte) cellB;
			SetPages.setCellNumber(f, new_p, num1);
		}
		catch(Exception e)
		{
			System.out.println("Error at splitLeafPage");
		}
	}

	
	public static void splitLeaf(RandomAccessFile f, int p)
	{
		int pagenew = MakePages.makeLeaf(f);
		int mid = Page.findMidKey(f, p);
		splitLeafPage(f, p, pagenew);
		int pa = Page.getParent(f, p);
		if(pa == 0)
		{
			int root = MakePages.makeInterior(f);
			SetPages.setParent(f, p, root);
			SetPages.setParent(f, pagenew, root);
			SetPages.setRightMost(f, root, pagenew);
			InsertPages.insertInteriorCell(f, root, p, mid);
		}
		else
		{
			long plocation = Page.getPointerLoc(f, p, pa);
			SetPages.setPointerLoc(f, plocation, pa, pagenew);
			InsertPages.insertInteriorCell(f, pa, p, mid);
			Page.sortCellArray(f, pa);
			while(Page.checkInteriorSpace(f, pa))
			{
				pa = splitInterior(f, pa);
			}
		}
	}

	
	public static int splitInterior(RandomAccessFile f, int p)
	{
		int newp = MakePages.makeInterior(f);
		int mid = Page.findMidKey(f, p);
		splitInteriorPage(f, p, newp);
		int par = Page.getParent(f, p);
		if(par == 0)
		{
			int root = MakePages.makeInterior(f);
			SetPages.setParent(f, p, root);
			SetPages.setParent(f, newp, root);
			SetPages.setRightMost(f, root, newp);
			InsertPages.insertInteriorCell(f, root, p, mid);
			return root;
		}
		else
		{
			long pl = Page.getPointerLoc(f, p, par);
			SetPages.setPointerLoc(f, pl, par, newp);
			InsertPages.insertInteriorCell(f, par, p, mid);
			Page.sortCellArray(f, par);
			return par;
		}
	}

	
}
