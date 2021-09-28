
public class Connector implements Cloneable {

	/* 1 - wall
	 * 2 - open
	 * 3 - exit
	 */
	private boolean blocked = false; 
	private Wall Wall;
	private boolean isPath = false;
	private boolean isLocked = false;
	private int LockedLevel = 0;
	private boolean isGuess = false;
	/*
	 *  0  is not locked 
	 *  1 is highest locked level, should never be unlocked, is for sure.
	 *  2-999...  Different level of locks that can not be assumed as permanent.
	 */
	
	public Object clone() throws CloneNotSupportedException
    {
	    Connector cloneC = (Connector) super.clone();
	    cloneC.Wall = (Wall) this.Wall.clone();
        
        return cloneC;
    }
	
	public boolean isGuess()
    {
        return isGuess;
    }

    public void setGuess(boolean isGuess)
    {
        this.isGuess = isGuess;
    }

    public Connector(Wall wall) {
		this.Wall = wall;
		if(Wall.getType().equals(GameBoard.WALL))
		{
			blocked = true;
			setLocked(true , GameConstants.HEIGHEST_LOCK_LEVEL);
		}
	}
	
	/*boolean updateBasedOnWall()
	{
		if(Wall.getType().equals(GameBoard.WALL))
		{
			blocked = true;
			setLocked(true,GameConstants.HEIGHEST_LOCK_LEVEL);
			return true;
		}
		return false;
	}*/

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean isPath() {
		return isPath;
	}

	public void setPath(boolean isPath) {
		this.isPath = isPath;
	}

	public boolean isLocked() {
		return isLocked;
	}
	
	public int getLockedLevel()
	{
	    return LockedLevel;
	}

	public void setLocked(boolean isLocked , int lockedLevel) {
		this.isLocked = isLocked;
		if(!isLocked)
		{
		    this.LockedLevel = 0;
		}
		else
		{
		    this.LockedLevel = lockedLevel;
		}
	}

}
