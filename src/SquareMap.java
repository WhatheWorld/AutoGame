
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;


public class SquareMap {

	ArrayList<String> toolTips = new ArrayList<>();
	ArrayList<Image> Colors = new ArrayList<>();
	ArrayList<String> DisplayText = new ArrayList<>();
	
	
	
	
	SquareMap()
	{
		File intFile = new File("src/Grey.png");
		File intFileVoid = new File("src/Grey.png");
		File intFileSPS = new File("src/Grey.png");
		InputStream red=null;
		InputStream isVoid=null;
		InputStream isSPS=null;
		try
		{
		 red = new FileInputStream(intFile);
		 isVoid = new FileInputStream(intFileVoid);
		 isSPS = new FileInputStream(intFileSPS);
		}
		catch(Exception e)
		{}
		
		toolTips.add("Empty Square");
		DisplayText.add("E");
		Colors.add(new Image(null, red));
		
		toolTips.add("Void Square");
		DisplayText.add("V");
		Colors.add(new Image(null, isVoid));
		
		toolTips.add("Secit Passage Square");
		DisplayText.add("S");
		Colors.add(new Image(null, isSPS));
		
	}
	
	String getNextOption(String strToolTip)
	{
		if(strToolTip == null)
		{
			return toolTips.get(0);
		}
		boolean done=false;
		for(int x = 0; x < toolTips.size(); x++)
		{
			if(done)
			{
				return toolTips.get(x);
			}
			if(strToolTip.equals(toolTips.get(x)))
			{
				done = true;
			}
		}
		return toolTips.get(0);
	}
	
	Image getNextColor(Image color)
	{
		if(color == null)
		{
			return Colors.get(0);
		}
		boolean done=false;
		for(int x = 0; x < Colors.size(); x++)
		{
			if(done)
			{
				return Colors.get(x);
			}
			if(color.equals(Colors.get(x)))
			{
				done = true;
			}
		}
		return Colors.get(0);
	}
	
	Image getImage(String toolTip)
	{
	    for(int x = 0; x < toolTips.size(); x++)
        {
            if(toolTip.equals(toolTips.get(x)))
            {
                return Colors.get(x);
            }
        }
	    return null;
	}
	
}
