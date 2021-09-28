import org.eclipse.swt.widgets.Button;

public class Wall implements Cloneable{
	
	private String type;
	
	public Object clone() throws CloneNotSupportedException
    {
	    Wall cloneC = (Wall) super.clone();
        
        return cloneC;
    }

	public Wall(Button btnWall) {
		type = btnWall.getToolTipText();
		
	}
	

	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

}
