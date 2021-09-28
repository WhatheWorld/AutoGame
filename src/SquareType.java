
public class SquareType {

    int type = 0;
    
	public int getType()
    {
        return type;
    }

    public SquareType(String toolTipType) {
	    switch(toolTipType) {
	        case GameConstants.TOOLTIP_SQ_NORMAL:
            {
                type = 1;
                break;
            }
	        case GameConstants.TOOLTIP_SQ_ODD:
            {
                type = 2;
                break;
            }
	        case GameConstants.TOOLTIP_SQ_SECRET_PASSAGE:
            {
                type = 3;
                break;
            }
	    }
	    
	}

}
