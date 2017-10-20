import org.eclipse.swt.widgets.Button;

public class Wall {
	
	private String type;

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
