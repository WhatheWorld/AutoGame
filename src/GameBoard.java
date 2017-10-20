import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class GameBoard
{

    // string Constants
    public static final String WALL = "Wall";
    public static final String EXIT = "Exit";

    private ArrayList<ArrayList<GameSquare>> BoardSquares = new ArrayList<ArrayList<GameSquare>>();
    private ArrayList<Connector> BoardConnections = new ArrayList<Connector>();
    private int maxX;
    private int maxY;
    ArrayList<ArrayList<Button>> rawSquares;
    ArrayList<Button> rawWalls;

    // draw variables
    int drawBoxSize = GameConstants.DRAW_SQUARE_SIZE;

    // board conditions
    private ArrayList<Integer> exitLocations = new ArrayList<>();

    // wait times
    private long mainWait = 0;

    public GameBoard(int x, int y, ArrayList<ArrayList<Button>> rawSquares, ArrayList<Button> rawWalls)
    {
        maxX = x;
        maxY = y;
        this.rawSquares = rawSquares;
        this.rawWalls = rawWalls;
        CreateBoardMapFromRawInputs();

    }

    void CreateBoardMapFromRawInputs()
    {
        // create connections
        for ( int x = 0 ; x < rawWalls.size() ; x++ )
        {
            BoardConnections.add(new Connector(new Wall(rawWalls.get(x))));
            if ( rawWalls.get(x).getToolTipText().equals(EXIT) )
            {
                exitLocations.add(x);
            }

        }

        // creates squares
        for ( int y = 0 ; y < this.maxY ; y++ )
        {
            BoardSquares.add(new ArrayList<GameSquare>());
            for ( int x = 0 ; x < this.maxX ; x++ )
            {
                Button button = rawSquares.get(y).get(x);
                SquareType type = new SquareType(button.getToolTipText());
                GameSquare square = new GameSquare(x, y, type, this.maxX, this.maxY,
                        BoardConnections.get(LocationOfTopWall(x, y)), BoardConnections.get(LocationOfRightWall(x, y)),
                        BoardConnections.get(LocationOfBottomWall(x, y)),
                        BoardConnections.get(LocationOfLeftWall(x, y)));
                BoardSquares.get(y).add(square);
            }
        }
        // process walls/connectors

    }

    int LocationOfLeftWall(int x, int y)
    {
        return (x * 2 + y * (2 * this.maxX + 1));
    }

    int LocationOfTopWall(int x, int y)
    {
        return (LocationOfLeftWall(x, y) + 1);
    }

    int LocationOfRightWall(int x, int y)
    {
        return (LocationOfLeftWall(x, y) + 2);
    }

    int LocationOfBottomWall(int x, int y)
    {
        if ( y != (this.maxY - 1) )
        {
            return (LocationOfLeftWall(x, y) + (this.maxX + 1) * 2);
        }
        else
        {
            return (LocationOfLeftWall(x, y) + ((this.maxX + 1) * 2 - 1 - x));
        }
    }

    public boolean Solve(Canvas canvas, Display display, long mainWaitTime,  int thislockLevel)
    {
        mainWait = mainWaitTime;
        int lockLevel = thislockLevel + 1;

        fillInDeterminedConnections(canvas, display);
        LockPaths(canvas, display, lockLevel);

        int passCount = 0;
        boolean guessingWalls = false;

        while ( !isSolved() )
        {
            Connector con = getConnectionToGuess(passCount);
            if ( con == null )
            {
                if ( !guessingWalls )
                {
                    guessingWalls = true;
                    passCount = 0;
                    con = getConnectionToGuess(passCount);
                }
                if(guessingWalls && con == null)
                {
                    // have ran out of guesses and not solved.
                    // recursive solve begins
                    // reset counters
                    passCount = 0;
                    guessingWalls = false;

                    // pass off to recursive solve to finish solving.

                    return recursiveSolve(canvas, display, mainWaitTime, lockLevel+1, 5, 1);
                }
            }

            // set up the temp wall/path
            if ( !guessingWalls )
            {
                con.setPath(true);
            }
            con.setBlocked(true);

            // assume as much as possible
            updateConnections(canvas, display);
            fillInDeterminedConnections(canvas, display);

            // check to see what state your in
            if ( isSolved() )
            {
                // solved Done
                LockPaths(canvas, display, lockLevel);
                break;
            }

            if ( isInErrorState() )
            {// useful clean up, set what we assumed, restart
                undoNonLockedPaths(canvas, display);

                con.setBlocked(true);
                con.setLocked(true, lockLevel);
                if ( !guessingWalls )
                {
                    con.setPath(false);
                }
                else
                {
                    con.setPath(true);
                }

                fillInDeterminedConnections(canvas, display);
                LockPaths(canvas, display, lockLevel);

                passCount = 0;
                guessingWalls = false;
            }
            else
            {// not useful clean up, go to next assumption.
                undoNonLockedPaths(canvas, display);
                passCount++;
            }

        }
        return true;

    }

    private boolean recursiveSolve(Canvas canvas, Display display, float mainWaitTime, int lockLevel, int howDeepToGo,
            int howDeepIAm)
    {
        int passCount = 0;
        boolean guessingWalls = false;

        while ( true )
        {
            Connector con = getConnectionToGuess(passCount);
            if ( con == null )
            {
                if ( !guessingWalls )
                {
                    guessingWalls = true;
                    passCount = 0;
                    con = getConnectionToGuess(passCount);
                }
                else
                {
                    break;
                }
                if ( con == null )
                {
                    break;
                }
            }

            // set up the temp wall/path
            if ( !guessingWalls )
            {
                con.setPath(true);
            }
            con.setBlocked(true);
            con.setLocked(true, lockLevel);
            boolean solved = oneGuessTryToSolve(canvas, display, lockLevel +1);

            if ( solved )
            {
                return true;
            }
            else
            {
                unLockAllPathsHiegher(canvas, display, lockLevel-1);
                passCount++;
            }

        }

        if ( howDeepIAm < howDeepToGo )
        {
            // go deeper
            return recursiveSolve(canvas, display, mainWaitTime, lockLevel+1, howDeepToGo, howDeepIAm+1);
        }
        else
        {
            // gone as deep as I am suppose to go return
            return false;
        }
    }

    private boolean oneGuessTryToSolve(Canvas canvas, Display display, int lockLevel)
    {
        int passCount = 0;
        boolean guessingWalls = false;

        while ( !isSolved() )
        {
            Connector con = getConnectionToGuess(passCount);
            if ( con == null )
            {
                if ( !guessingWalls )
                {
                    guessingWalls = true;
                    passCount = 0;
                    con = getConnectionToGuess(passCount);
                }
                else
                {
                    return false;
                }
                if ( con == null )
                {
                    return false;
                }
            }

            // set up the temp wall/path
            if ( !guessingWalls )
            {
                con.setPath(true);
            }
            con.setBlocked(true);

            // assume as much as possible
            updateConnections(canvas, display);
            fillInDeterminedConnections(canvas, display);

            // check to see what state your in
            if ( isSolved() )
            {
                // solved Done
                LockPaths(canvas, display, lockLevel);
                return true;
            }

            if ( isInErrorState() )
            {// useful clean up, set what we assumed, restart
                undoNonLockedPaths(canvas, display);

                con.setBlocked(true);
                con.setLocked(true, lockLevel);
                if ( !guessingWalls )
                {
                    con.setPath(false);
                }
                else
                {
                    con.setPath(true);
                }

                fillInDeterminedConnections(canvas, display);
                LockPaths(canvas, display, lockLevel);

                passCount = 0;
                guessingWalls = false;
            }
            else
            {// not useful clean up, go to next assumption.
                undoNonLockedPaths(canvas, display);
                passCount++;
            }

        }
        return true;
    }

    void unLockAllPathsHiegher(Canvas canvas, Display display, int lockLevel)
    {
        for ( int y = 0 ; y < this.maxY ; y++ )
        {
            for ( int x = 0 ; x < this.maxX ; x++ )
            {
                GameSquare square = BoardSquares.get(y).get(x);
                square.clearHiegherLocked(lockLevel);
            }
        }
        updateConnections(canvas, display);
    }

    void undoNonLockedPaths(Canvas canvas, Display display)
    {
        for ( int y = 0 ; y < this.maxY ; y++ )
        {
            for ( int x = 0 ; x < this.maxX ; x++ )
            {
                GameSquare square = BoardSquares.get(y).get(x);
                square.clearNonLockedConnections();
            }
        }
        updateConnections(canvas, display);
    }

    boolean isInErrorState()
    {
        for ( int y = 0 ; y < this.maxY ; y++ )
        {
            for ( int x = 0 ; x < this.maxX ; x++ )
            {
                GameSquare square = BoardSquares.get(y).get(x);
                if ( square.numberOfOpenPaths() > square.numberOfOpenConnections() || square.getNumberOfPaths() > 2 )
                {
                    return true;
                }
            }
        }
        if ( isThereALoop() )
        {
            return true;
        }
        if ( validNumberOfExits().size() < 2 )
        {
            return true;
        }

        return false;
    }

    boolean fillInDeterminedConnections(Canvas canvas, Display display)
    {
        boolean changed = false;
        boolean changeInLoop = false;
        do
        {
            changeInLoop = false;
            updateConnections(canvas, display);
            changeInLoop = ((setAndLockDeterminedPaths(canvas, display)) ? true : changeInLoop);
            changeInLoop = ((checkExits(canvas, display)) ? true : changeInLoop);
            changed = ((changeInLoop) ? true : changed);
        }
        while ( changeInLoop );

        return changed;
    }

    Connector getConnectionToGuess(int passes)
    {
        int connectorNumber = 0;
        for ( int i = 2 ; i <= 4 ; i++ )
        {
            for ( int y = 0 ; y < this.maxY ; y++ )
            {
                for ( int x = 0 ; x < this.maxX ; x++ )
                {
                    GameSquare square = BoardSquares.get(y).get(x);
                    if ( square.numberOfOpenConnections() == i )
                    {
                        for ( int direction = 1 ; direction <= 4 ; direction++ )
                        {
                            Connector con = square.getConnectorBasedOnDirection(direction);
                            if ( !con.isBlocked() && connectorNumber == passes )
                            {
                                return con;
                            }
                            else if ( !con.isBlocked() )
                            {
                                connectorNumber++;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    boolean isSolved()
    {
        if ( isInErrorState() )
        {
            return false;
        }
        ArrayList<Integer> validExits = validNumberOfExits();
        if ( validExits.size() != 2 )
        {
            return false;
        }

        for ( int y = 0 ; y < this.maxY ; y++ )
        {
            for ( int x = 0 ; x < this.maxX ; x++ )
            {
                GameSquare square = BoardSquares.get(y).get(x);
                if ( square.numberOfOpenPaths() > 0 )
                {
                    return false;
                }
            }
        }

        return true;
    }

    boolean isThereALoop()
    {
        for ( int y = 0 ; y < this.maxY ; y++ )
        {
            for ( int x = 0 ; x < this.maxX ; x++ )
            {
                GameSquare square = BoardSquares.get(y).get(x);
                if ( square.numberOfOpenPaths() == 0 )
                {
                    if ( startLoopChecking(square) )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean startLoopChecking(GameSquare startingPoint)
    {
        int dir = startingPoint.getPath1().getDirection();
        return loopCheckingRecercive(startingPoint, getSquare(startingPoint, dir), reversDirection(dir));

        // return false;
    }

    boolean loopCheckingRecercive(GameSquare startingPoint, GameSquare currentPoint, int cameFromDirection)
    {
        if ( currentPoint == null || currentPoint.numberOfOpenPaths() > 0 )
        {
            return false;
        }

        if ( startingPoint.getX() == currentPoint.getX() && startingPoint.getY() == currentPoint.getY() )
        {
            return true;
        }

        GameSquare nextSquare;
        int direction;

        if ( currentPoint.getPath1().getDirection() != cameFromDirection )
        {
            nextSquare = getSquare(currentPoint, currentPoint.getPath1().getDirection());
            direction = currentPoint.getPath1().getDirection();
        }
        else
        {
            nextSquare = getSquare(currentPoint, currentPoint.getPath2().getDirection());
            direction = currentPoint.getPath2().getDirection();
        }

        return loopCheckingRecercive(startingPoint, nextSquare, reversDirection(direction));

    }

    int reversDirection(int direction)
    {
        switch ( direction )
        {
            case 1:// top
            {
                return 3;
            }
            case 2:// right
            {
                return 4;
            }
            case 3:// bottom
            {
                return 1;
            }
            case 4:// left
            {
                return 2;
            }
        }
        return 0;
    }

    GameSquare getSquare(GameSquare square, int direction)
    {
        int x = square.getX();
        int y = square.getY();

        switch ( direction )
        {
            case 1:// top
            {
                y--;
                break;
            }
            case 2:// right
            {
                x++;
                break;
            }
            case 3:// bottom
            {
                y++;
                break;
            }
            case 4:// left
            {
                x--;
                break;
            }
        }
        if ( x < this.maxX && y < this.maxY && x >= 0 && y >= 0 )
        {
            return BoardSquares.get(y).get(x);
        }
        return null;
    }

    boolean LockPaths(Canvas canvas, Display display, int lockLevel)
    {
        boolean updated = false;
        for ( int y = 0 ; y < BoardConnections.size() ; y++ )
        {
            if ( !BoardConnections.get(y).isLocked() && BoardConnections.get(y).isBlocked() )
            {
                updated = true;
                BoardConnections.get(y).setLocked(true, lockLevel);
                // too do, if not path then set to wall.
            }
        }
        if ( updated )
        {
            invokeRedrawAndWait(canvas, mainWait, display);
        }
        return updated;
    }

    boolean checkExits(Canvas canvas, Display display)
    {
        ArrayList<Integer> validExits = validNumberOfExits();
        if ( validExits.size() == 2 )
        {
            boolean updated = false;
            for ( int x = 0 ; x < validExits.size() ; x++ )
            {
                if ( !BoardConnections.get(validExits.get(x)).isBlocked() )
                {
                    BoardConnections.get(validExits.get(x)).setBlocked(true);
                    BoardConnections.get(validExits.get(x)).setPath(true);
                    updated = true;
                }
            }
            if ( updated )
            {
                updateConnections(canvas, display);
                return true;
            }
        }
        if ( getNumberOfExitsWithPaths(validExits) >= 2 )
        {
            boolean updated = false;
            for ( int x = 0 ; x < validExits.size() ; x++ )
            {
                if ( !BoardConnections.get(validExits.get(x)).isBlocked() )
                {
                    BoardConnections.get(validExits.get(x)).setBlocked(true);
                    updated = true;
                }
            }
            if ( updated )
            {
                updateConnections(canvas, display);
                return true;
            }
        }

        return false;
    }

    int getNumberOfExitsWithPaths(ArrayList<Integer> validExits)
    {
        int numberOfExitsWithPaths = 0;
        for ( int x = 0 ; x < validExits.size() ; x++ )
        {
            if ( BoardConnections.get(validExits.get(x)).isPath() )
            {
                numberOfExitsWithPaths++;
            }
        }

        return numberOfExitsWithPaths;
    }

    ArrayList<Integer> validNumberOfExits()
    {
        ArrayList<Integer> validExits = new ArrayList<>();
        for ( int x = 0 ; x < exitLocations.size() ; x++ )
        {
            if ( !BoardConnections.get(exitLocations.get(x)).isBlocked()
                    || BoardConnections.get(exitLocations.get(x)).isPath() )
            {
                validExits.add(exitLocations.get(x));
            }
        }
        return validExits;
    }

    boolean setAndLockDeterminedPaths(Canvas canvas, Display display)
    {
        boolean updated = false;
        for ( int y = 0 ; y < this.maxY ; y++ )
        {
            for ( int x = 0 ; x < this.maxX ; x++ )
            {
                GameSquare square = BoardSquares.get(y).get(x);
                if ( square.setPathsIfDeterminiedAndLock() )
                {
                    updated = true;
                }
            }
        }
        if ( updated )
        {
            updateConnections(canvas, display);
        }
        return updated;
    }

    void updateConnections(Canvas canvas, Display display)
    {
        boolean badUpdate = false;

        do
        {
            badUpdate = false;
            for ( int y = 0 ; y < this.maxY ; y++ )
            {
                for ( int x = 0 ; x < this.maxX ; x++ )
                {
                    GameSquare square = BoardSquares.get(y).get(x);
                    // square.updateConnectionsBasedOnPaths();
                    square.updateConnectionsBaseOnWalls();
                    // square.updatePathBasedOnConnections();
                    if ( square.updateCompleted() )
                    {
                        badUpdate = true;
                    }
                }
            }
        }
        while ( badUpdate );
        invokeRedrawAndWait(canvas, mainWait, display);
    }

    public void invokeRedrawAndWait(Canvas canvas, long waitTime, Display display)
    {

        if ( waitTime > 0 )
        {
            display.asyncExec(new Runnable()
            {

                @Override
                public void run()
                {
                    canvas.redraw();
                    canvas.update();
                }

            });
            try
            {
                TimeUnit.MILLISECONDS.sleep(waitTime);
            }
            catch (InterruptedException e1)
            {
            }
        }
    }

    public void Draw(GC gc, Display display)
    {
        int boxSize = drawBoxSize;
        // gc.drawLine(0, 5, 10, 10);
        // draw base grid
        for ( int y = 0 ; y <= this.maxY ; y++ )
        {
            gc.drawLine(0, y * boxSize, this.maxX * boxSize, y * boxSize);
        }
        for ( int x = 0 ; x <= this.maxX ; x++ )
        {
            gc.drawLine(x * boxSize, 0, x * boxSize, this.maxY * boxSize);
        }

        // draw walls
        int sideCount = 0;
        int xCount = 0;
        int LastRowCount = 0;

        int drawX = 0;
        int drawY = 0;

        for ( int w = 0 ; w < this.BoardConnections.size() ; w++ )
        {
            if ( w < (this.maxX * this.maxY) * 2 + this.maxY )
            {
                if ( sideCount == 2 )
                {
                    sideCount = 1;
                    drawX = drawX + drawBoxSize;
                }
                else
                {
                    sideCount++;
                }
                if ( xCount == (2 * this.maxX + 1) )
                {
                    sideCount = 1;
                    xCount = 1;
                    drawY = drawY + drawBoxSize;
                    drawX = 0;
                }
                else
                {
                    xCount++;
                }
            }
            else
            {
                if ( LastRowCount == 0 )
                {
                    LastRowCount++;
                    drawX = 0;
                    drawY = drawY + drawBoxSize;

                }
                else
                {
                    drawX = drawX + drawBoxSize;
                }
            }

            DrawWall(drawX, drawY, this.rawWalls.get(w), this.BoardConnections.get(w), gc, display);
        }

        // draw square connections
        for ( int y = 0 ; y < this.maxY ; y++ )
        {
            for ( int x = 0 ; x < this.maxX ; x++ )
            {
                GameSquare square = BoardSquares.get(y).get(x);
                DrawSquare(square, gc, display);
            }
        }
    }

    private void DrawSquare(GameSquare square, GC gc, Display display)
    {
        // center of square
        int x = square.getX() * this.drawBoxSize + this.drawBoxSize / 2;
        int y = square.getY() * this.drawBoxSize + this.drawBoxSize / 2;
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));

        if ( square.getPath1() != null )
        {
            if ( square.getPath1().isLocked() )
                gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
            DrawSquareHelper(x, y, square.getPath1().getDirection(), gc);
            gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
        }
        if ( square.getPath2() != null )
        {
            if ( square.getPath2().isLocked() )
                gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
            DrawSquareHelper(x, y, square.getPath2().getDirection(), gc);
        }

        gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    }

    private void DrawSquareHelper(int x, int y, int direction, GC gc)
    {
        switch ( direction )
        {
            case 1:// top
            {
                gc.fillRectangle(x - 2, y - 2 - this.drawBoxSize, 5, this.drawBoxSize + 4);
                break;
            }
            case 2:// right
            {
                gc.fillRectangle(x - 2, y - 2, this.drawBoxSize + 4, 5);
                break;
            }
            case 3:// bottom
            {
                gc.fillRectangle(x - 2, y + 2, 5, this.drawBoxSize + 4);
                break;
            }
            case 4:// left
            {
                gc.fillRectangle(x - 2 - this.drawBoxSize, y - 2, this.drawBoxSize + 4, 5);
                break;
            }
        }
    }

    public void DrawWall(int x, int y, Button button, Connector connector, GC gc, Display display)
    {
        int btnX = button.getSize().x;
        int btnY = button.getSize().y;
        String btnType = button.getToolTipText();

        // Device device = Display.getCurrent();

        Color Black = display.getSystemColor(SWT.COLOR_BLACK);// new
                                                              // Color(device,255,255,255);
        Color Green = display.getSystemColor(SWT.COLOR_GREEN);
        Color Grey = display.getSystemColor(SWT.COLOR_DARK_GRAY);
        Color Blue = display.getSystemColor(SWT.COLOR_BLUE);

        if ( btnType.equals(GameConstants.WALL) )
        {
            gc.setBackground(Black);
        }
        else if ( btnType.equals(GameConstants.EMPTY) && connector.isPath() )
        {
            return;
        }
        else if ( btnType.equals(GameConstants.EXIT) )
        {
            gc.setBackground(Green);
        }
        else if ( !connector.isBlocked() )
        {
            return;
        }
        else if ( connector.isLocked() )
        {
            gc.setBackground(Grey);
        }
        else if ( !connector.isLocked() )
        {
            gc.setBackground(Blue);
        }

        if ( btnX > btnY )
        {// --
            gc.fillRectangle(x - 2, y - 2, this.drawBoxSize + 4, 5);
        }
        else
        {// |
            gc.fillRectangle(x - 2, y - 2, 5, this.drawBoxSize + 4);
        }

        gc.setBackground(Black);
    }
}
