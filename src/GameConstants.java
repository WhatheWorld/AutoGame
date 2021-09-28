

public class GameConstants {
	
	public static final int UNLOCKED = 0;
	public static final int MAX_LOCKED = 1;
	public static final int TOP = 1;
	public static final int RIGHT = 2;
	public static final int BOTTOM = 3;
	public static final int LEFT = 4;
	public static final String WALL = "Wall";
	public static final String EXIT = "Exit";
    public static final String EMPTY = "Empty";
    public static final String DELIMITER = "$";
    
    //BUTTON square types/tool tips
    // normal squares that require you to enter and leave only once
    public static final String TOOLTIP_SQ_NORMAL = "Normal Square";
    public static final int SQ_TYPE_NORMAL = 1;
    //squares that can either be used like normal or not used at all
    public static final String TOOLTIP_SQ_ODD = "Odd Square";
    public static final int SQ_TYPE_ODD = 2;
    // either none or exactly 2 in a game, can be used like a normal square or enter one and leave through the other.
    public static final String TOOLTIP_SQ_SECRET_PASSAGE =  "Secret Passage Square";
    public static final int SQ_TYPE_SECRET_PASSAGE = 3;
    
    
    //drawing board elements
    public static final int DRAW_SQUARE_SIZE = 50;
    
    //Locked level for connectors
    public static final int HEIGHEST_LOCK_LEVEL = 1;
    

	public GameConstants() {
		// TODO Auto-generated constructor stub
	}

}
