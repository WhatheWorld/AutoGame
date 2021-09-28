import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

public class VerticalWallMap {
	ArrayList<String> toolTips = new ArrayList<String>();
	ArrayList<Image> Images = new ArrayList<Image>();
	ArrayList<String> DisplayText = new ArrayList<String>();
	
	VerticalWallMap()
	{
		InputStream tan=null;
		InputStream green=null;
		InputStream black=null;
		
		try
        {
            green = VerticalWallMap.class.getClassLoader().getResourceAsStream("Green.png");
            black = VerticalWallMap.class.getClassLoader().getResourceAsStream("Black.png");
            tan = VerticalWallMap.class.getClassLoader().getResourceAsStream("Tan.png");
        }
        catch(Exception e)
        {}
		
		
		toolTips.add("Empty");
		DisplayText.add("E");
		Images.add(new Image(null, tan));
		
		toolTips.add(GameBoard.WALL);
		DisplayText.add("w");
		Images.add(new Image(null, black));
		
		toolTips.add("Exit");
		DisplayText.add("X");
		Images.add(new Image(null, green));
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
	
	Image getNextImage(Image image)
	{
		if(image == null)
		{
			return Images.get(0);
		}
		boolean done=false;
		for(int x = 0; x < Images.size(); x++)
		{
			if(done)
			{
				return Images.get(x);
			}
			if(image.equals(Images.get(x)))
			{
				done = true;
			}
		}
		return Images.get(0);
	}

}
