
public class GameSquare implements Cloneable
{

    private int X;
    private int Y;
    private int maxX;
    private int maxY;
    // private Path path1 = new Path();
    // private Path path2 = new Path();
    private SquareType type;
    private Connector top;
    private Connector right;
    private Connector bottom;
    private Connector left;
    private boolean completed = false;
    
    public Object clone() throws CloneNotSupportedException
    {
        GameSquare cloneGS = (GameSquare) super.clone();
        cloneGS.top = (Connector) this.top.clone();
        cloneGS.right = (Connector) this.right.clone();
        cloneGS.bottom = (Connector) this.bottom.clone();
        cloneGS.left = (Connector) this.left.clone();
        
        return cloneGS;
    }

    public GameSquare(int x, int y, SquareType type, int maxX, int maxY, Connector top, Connector right,
            Connector bottom, Connector left)
    {
        this.X = x;
        this.Y = y;
        this.type = type;
        this.maxX = maxX;
        this.maxY = maxY;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;

    }

    void clearNonLockedConnections()
    {
        if ( top.isBlocked() && !top.isLocked() )
        {
            top.setPath(false);
            top.setBlocked(false);
        }
        if ( right.isBlocked() && !right.isLocked() )
        {
            right.setPath(false);
            right.setBlocked(false);
        }
        if ( bottom.isBlocked() && !bottom.isLocked() )
        {
            bottom.setPath(false);
            bottom.setBlocked(false);
        }
        if ( left.isBlocked() && !left.isLocked() )
        {
            left.setPath(false);
            left.setBlocked(false);
        }

    }

    void clearHiegherLocked(int lockLevel)
    {
        if ( top.isBlocked() && (top.getLockedLevel() > lockLevel || !top.isLocked()) )
        {
            top.setPath(false);
            top.setBlocked(false);
            top.setLocked(false, GameConstants.UNLOCKED);
            top.setGuess(false);
        }
        if ( right.isBlocked() && (right.getLockedLevel() > lockLevel || !right.isLocked()) )
        {
            right.setPath(false);
            right.setBlocked(false);
            right.setLocked(false, GameConstants.UNLOCKED);
            right.setGuess(false);
        }
        if ( bottom.isBlocked() && (bottom.getLockedLevel() > lockLevel || !bottom.isLocked()) )
        {
            bottom.setPath(false);
            bottom.setBlocked(false);
            bottom.setLocked(false, GameConstants.UNLOCKED);
            bottom.setGuess(false);
        }
        if ( left.isBlocked() && (left.getLockedLevel() > lockLevel || !left.isLocked()) )
        {
            left.setPath(false);
            left.setBlocked(false);
            left.setLocked(false, GameConstants.UNLOCKED);
            left.setGuess(false);
        }
    }

    boolean squareEqualTo(int numberOfOpenPaths, int numberOfOpenConnections)
    {
        if ( numberOfOpenConnections() == numberOfOpenConnections && numberOfOpenPaths() == numberOfOpenPaths )
        {
            return true;
        }
        return false;
    }

    /*
     * boolean lockPaths() { boolean updated = false; if(path1.getDirection() !=
     * 0) { path1.setLocked(true,
     * getConnectorBasedOnDirection(path1.getDirection())); updated = true; }
     * if(path2.getDirection() != 0) { path2.setLocked(true,
     * getConnectorBasedOnDirection(path2.getDirection())); updated = true; }
     * return updated; }
     */

    Connector getConnectorBasedOnDirection(int Direction)
    {
        switch ( Direction )
        {
            case 1:// top
            {
                return top;
            }
            case 2:// right
            {
                return right;
            }
            case 3:// bottom
            {
                return bottom;
            }
            case 4:// left
            {
                return left;
            }
        }
        return null;
    }

    /*
     * boolean updatePathBasedOnConnections() { if(top.isPath()) { setAPathTo(1,
     * top.isLocked(), top); } if(right.isPath()) { setAPathTo(2,
     * right.isLocked(), right); } if(bottom.isPath()) { setAPathTo(3,
     * bottom.isLocked(), bottom); } if(left.isPath()) { setAPathTo(4,
     * left.isLocked(), left); }
     * 
     * return true; }
     */

    /**
     * set the completed status based on passed in parameter.
     * 
     * @param com
     * @return returns true if a connection was changed, false other wise.
     */
    boolean setCompleted(boolean com)
    {
        if ( com == this.completed )
        {
            return false;
        }
        if ( com )
        {
            boolean returnValue = false;
            if(!top.isBlocked())
            {
                top.setBlocked(true);
                returnValue = true;
            }
            if(!right.isBlocked())
            {
                right.setBlocked(true);
                returnValue = true;
            }
            if(!bottom.isBlocked())
            {
                bottom.setBlocked(true);
                returnValue = true;
            }
            if(!left.isBlocked())
            {
                left.setBlocked(true);
                returnValue = true;
            }
            completed = com;
            return returnValue;
        }
        else
        {
            completed = com;
            return false;
        }
    }

   /* boolean updateConnectionsBaseOnWalls()
    {

        top.updateBasedOnWall();
        right.updateBasedOnWall();
        bottom.updateBasedOnWall();
        left.updateBasedOnWall();
        return true;
    }*/

    int getX()
    {
        return X;
    }

    int getY()
    {
        return Y;
    }

    Path getPath1()
    {
        if ( top.isPath() )
        {
            return new Path(1, top.isLocked());
        }
        if ( right.isPath() )
        {
            return new Path(2, right.isLocked());
        }
        if ( bottom.isPath() )
        {
            return new Path(3, bottom.isLocked());
        }
        if ( left.isPath() )
        {
            return new Path(4, left.isLocked());
        }
        return null;
    }

    Path getPath2()
    {
        boolean second = false;

        if ( top.isPath() )
        {
            second = true;
        }
        if ( right.isPath() )
        {
            if ( second )
            {
                return new Path(2, right.isLocked());
            }
            second = true;
        }
        if ( bottom.isPath() )
        {
            if ( second )
            {
                return new Path(3, bottom.isLocked());
            }
            second = true;
        }
        if ( left.isPath() )
        {
            if ( second )
            {
                return new Path(4, left.isLocked());
            }
        }
        return null;
    }

    boolean updateCompleted()
    {
        if ( getNumberOfPaths() == 2 )
        {
            return setCompleted(true);
        }
        else
        {
            setCompleted(false);
            return false;
        }
    }

    int getNumberOfPaths()
    {
        int NumberOfPaths = 0;

        if ( top.isPath() )
        {
            NumberOfPaths++;
        }
        if ( right.isPath() )
        {
            NumberOfPaths++;
        }
        if ( bottom.isPath() )
        {
            NumberOfPaths++;
        }
        if ( left.isPath() )
        {
            NumberOfPaths++;
        }

        return NumberOfPaths;
    }

    /*
     * boolean updateConnectionsBasedOnPaths() { if(path1.getDirection() != 0) {
     * switch(path1.getDirection()) { case 1://top { top.setBlocked(true);
     * top.setPath(true); return true; } case 2://right {
     * right.setBlocked(true); right.setPath(true); return true; } case
     * 3://bottom { bottom.setBlocked(true); bottom.setPath(true); return true;
     * } case 4://left { left.setBlocked(true); left.setPath(true); return true;
     * } } }
     * 
     * if(path2.getDirection() != 0) { switch(path2.getDirection()) { case
     * 1://top { top.setBlocked(true); top.setPath(true); return true; } case
     * 2://right { right.setBlocked(true); right.setPath(true); return true; }
     * case 3://bottom { bottom.setBlocked(true); bottom.setPath(true); return
     * true; } case 4://left { left.setBlocked(true); left.setPath(true); return
     * true; } } }
     * 
     * return false; }
     */

    /**
     * completes any path that is deterministic.
     * 
     * @return returns true if something was updated else false
     */
    boolean setPathsIfDeterminiedAndLock()
    {
        switch(getType().getType()) {
            case GameConstants.SQ_TYPE_NORMAL:
            {
                if ( !completed && numberOfOpenConnections() == numberOfOpenPaths() && numberOfOpenConnections() != 0 )
                {
                    if ( !top.isBlocked() )
                    {
                        setAPathTo(1);

                    }
                    if ( !right.isBlocked() )
                    {
                        setAPathTo(2);

                    }
                    if ( !bottom.isBlocked() )
                    {
                        setAPathTo(3);

                    }
                    if ( !left.isBlocked() )
                    {
                        setAPathTo(4);

                    }
                    return true;
                }
                return false;
            }
            case GameConstants.SQ_TYPE_ODD:
            {
                if(numberOfOpenConnections() == 1 && numberOfOpenPaths() == 2)
                {
                    if ( !top.isBlocked() )
                    {
                        top.setBlocked(true);

                    }
                    if ( !right.isBlocked() )
                    {
                        right.setBlocked(true);

                    }
                    if ( !bottom.isBlocked() )
                    {
                        bottom.setBlocked(true);

                    }
                    if ( !left.isBlocked() )
                    {
                        left.setBlocked(true);

                    }
                    return true;
                }
                return false;
            }
            case GameConstants.SQ_TYPE_SECRET_PASSAGE:
            {
                return false;
            }
        }
        return false;
    }

    int numberOfOpenPaths()
    {
        return 2 - getNumberOfPaths();
    }

    /*
     * boolean setAPathTo(int direction, Connector connector) {
     * if(path1.getDirection() == 0) { path1.setDirection(direction,connector);
     * return true; } else if(path2.getDirection() == 0) {
     * path2.setDirection(direction,connector); return true; } return false; }
     */

    boolean setAPathTo(int direction)
    {

        switch ( direction )
        {
            case 1:// top
            {
                top.setBlocked(true);
                top.setPath(true);
                return true;
            }
            case 2:// right
            {
                right.setBlocked(true);
                right.setPath(true);
                return true;
            }
            case 3:// bottom
            {
                bottom.setBlocked(true);
                bottom.setPath(true);
                return true;
            }
            case 4:// left
            {
                left.setBlocked(true);
                left.setPath(true);
                return true;
            }
        }
        return false;
    }

    int numberOfOpenConnections()
    {
        int numberOfConnections = 0;
        if ( !top.isBlocked() )
        {
            numberOfConnections++;
        }
        if ( !right.isBlocked() )
        {
            numberOfConnections++;
        }
        if ( !bottom.isBlocked() )
        {
            numberOfConnections++;
        }
        if ( !left.isBlocked() )
        {
            numberOfConnections++;
        }

        return numberOfConnections;
    }

    int LocationOfLeftWall()
    {
        return (X * 2 + Y * (2 * this.maxX + 1));
    }

    int LocationOfTopWall()
    {
        return (LocationOfLeftWall() + 1);
    }

    int LocationOfRightWall()
    {
        return (LocationOfLeftWall() + 2);
    }

    int LocationOfBottomWall()
    {
        if ( Y != (this.maxY - 1) )
        {
            return (LocationOfLeftWall() + (this.maxX + 1) * 2);
        }
        else
        {
            return (LocationOfLeftWall() + ((this.maxX + 1) * 2 - 1 - X));
        }
    }
    
    public SquareType getType()
    {
        return type;
    }
}
