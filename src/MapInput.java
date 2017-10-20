import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Spinner;

public class MapInput
{

    protected Shell shell;
    int wallLength = 30;
    int WallWidth = 15;
    int square = 30;
    SquareMap SquareMap = new SquareMap();
    HorizontalWallMap wallMap = new HorizontalWallMap();
    ArrayList<ArrayList<Button>> rawSquares = new ArrayList<ArrayList<Button>>();
    ArrayList<Button> rawWalls = new ArrayList<Button>();
    GameBoard gameBoard;
    Canvas canvas;
    Button btnXUpButton;
    Button btnXDownButton;
    Button btnYUpButton;
    Button btnYDownButton;
    Button btnNewButton;
    Button btnSolve;
    Button btnSave;
    Button btnLoad;
    Button btnDelete;
    Spinner drawTimeSpinner;
    
    Text saveText;
    Combo loadSelect;
    
    SavedMaps savedMaps = new SavedMaps();

    int boardX = 5;
    int boardY = 5;

    int paint = 0;
    private Text text;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            MapInput window = new MapInput();
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open()
    {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while ( !shell.isDisposed() )
        {
            if ( !display.readAndDispatch() )
            {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents()
    {
        shell = new Shell();
        shell.setSize(950, 900);
        shell.setText("SWT Application");

        canvas = new Canvas(shell, SWT.NONE);
        canvas.setBounds(boardX * (square + WallWidth) + WallWidth + 10 + 100, 0, 600, 600);
        GC gc = new GC(canvas);
        // canvas.drawBackground(gc, 0, 0, 10, 10);
        canvas.addPaintListener(new PaintListener()
        {

            @Override
            public void paintControl(PaintEvent e)
            {
                // e.gc.drawLine(0, 0, 10, 10);
                if ( gameBoard != null )
                {
                    gameBoard.Draw(e.gc, e.display);

                }
                else
                {
                    e.gc.drawLine(0, 0, 10, 10);
                }
            }

        });
        
        
        drawTimeSpinner = new Spinner(shell, SWT.BORDER);
        drawTimeSpinner.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 300, 60, 22);
        drawTimeSpinner.setMinimum(0);
        drawTimeSpinner.setMaximum(5000);
        drawTimeSpinner.setSelection(500);
        
        saveText = new Text(shell, SWT.BORDER);
        saveText.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 135, 75, 25);
        saveText.setTextLimit(25);
        
        
         loadSelect = new Combo(shell, SWT.READ_ONLY);
         loadSelect.setVisibleItemCount(10);
         loadSelect.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 210, 75, 25);
         ArrayList<String> names = savedMaps.getSavedMapNames();
         for(int x =0; x<names.size() ;x++)
         {
             loadSelect.add(names.get(x));
         }
        
        
        btnLoad = new Button(shell, SWT.NONE);
        btnLoad.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 240, 75, 25);
        btnLoad.setText("Load");
        btnLoad.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                JsonObject map = savedMaps.get(loadSelect.getSelectionIndex());
                boardX = map.get(savedMaps.JSON_X).getAsInt();
                boardY = map.get(savedMaps.JSON_Y).getAsInt();
                updateBoadSize();
                updateBoadFromSavedMap(map);
                
                gameBoard = new GameBoard(boardX, boardY, rawSquares, rawWalls);
                canvas.redraw();
                
            }

        });
        
        btnDelete = new Button(shell, SWT.NONE);
        btnDelete.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 270, 75, 25);
        btnDelete.setText("Delete");
        btnDelete.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                int toBeDeleted = loadSelect.getSelectionIndex();
                savedMaps.delete(toBeDeleted);
                loadSelect.remove(toBeDeleted);
            }

        });
        
        btnSave = new Button(shell, SWT.NONE);
        btnSave.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 165, 75, 25);
        btnSave.setText("Save");
        btnSave.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                savedMaps.add(boardX, boardY,saveText.getText(), rawSquares, rawWalls);
                loadSelect.add(saveText.getText());
            }

        });

        btnXUpButton = new Button(shell, SWT.NONE);
        btnXUpButton.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 10, 30, 15);
        btnXUpButton.setText("X UP");
        btnXUpButton.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                boardX++;
                updateBoadSize();

            }

        });

        btnXDownButton = new Button(shell, SWT.NONE);
        btnXDownButton.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 30, 30, 15);
        btnXDownButton.setText("X Down");
        btnXDownButton.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                boardX--;
                updateBoadSize();

            }

        });

        btnYUpButton = new Button(shell, SWT.NONE);
        btnYUpButton.setBounds(boardX * (square + WallWidth) + WallWidth + 10 + 35, 10, 30, 15);
        btnYUpButton.setText("Y UP");
        btnYUpButton.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                boardY++;
                updateBoadSize();

            }

        });

        btnYDownButton = new Button(shell, SWT.NONE);
        btnYDownButton.setBounds(boardX * (square + WallWidth) + WallWidth + 10 + 35, 30, 30, 15);
        btnYDownButton.setText("Y Down");
        btnYDownButton.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                boardY--;
                updateBoadSize();
            }

        });

        btnNewButton = new Button(shell, SWT.NONE);
        btnNewButton.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 50, 75, 25);
        btnNewButton.setText("Preview");
        btnNewButton.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                gameBoard = new GameBoard(boardX, boardY, rawSquares, rawWalls);
                canvas.redraw();

            }

        });

        btnSolve = new Button(shell, SWT.NONE);
        btnSolve.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 50 + 50, 75, 25);
        btnSolve.setText("solve");
        btnSolve.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {

                if ( gameBoard != null )
                {
                    int drawTime = drawTimeSpinner.getSelection();
                    Thread thread = new Thread(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            gameBoard.Solve(canvas, arg0.display, drawTime ,0);
                            gameBoard.invokeRedrawAndWait(canvas, 1, arg0.display);
                        }

                    });

                    thread.start();
                }

            }

        });

        updateBoadSize();
    }
    
    public void updateBoadFromSavedMap(JsonObject map)
    {
        JsonArray squares = map.get(savedMaps.JSON_SQUARE).getAsJsonArray();
        int indexS =0;
        for ( int x = 0 ; x < rawSquares.size() ; x++ )
        {
            for ( int y = 0 ; y < rawSquares.get(x).size() ; y++ )
            {
                Button btnSquare = rawSquares.get(x).get(y);
                JsonObject savedSquare= squares.get(indexS).getAsJsonObject();
                String type = savedSquare.get(savedMaps.JSON_SQUARE_TYPE).getAsString();
                btnSquare.setToolTipText(type);
                btnSquare.setImage(SquareMap.getImage(type));
                        
                indexS++;
            }
        }
        
        JsonArray walls = map.get(savedMaps.JSON_WALL).getAsJsonArray();
        int indexWall = 0;
        for ( int x = 0 ; x < rawWalls.size() ; x++ )
        {
            Button btnWall = rawWalls.get(x);
            JsonObject savedWall = walls.get(indexWall).getAsJsonObject();
            String type = savedWall.get(savedMaps.JSON_WALL_TYPE).getAsString();
            btnWall.setToolTipText(type);
            btnWall.setImage(wallMap.getImage(type));
            indexWall++;
        }
        
    }

    public void updateBoadSize()
    {
        this.gameBoard = null;
        canvas.setBounds(boardX * (square + WallWidth) + WallWidth + 10 + 100, 0, (boardX + 1)*(GameConstants.DRAW_SQUARE_SIZE), (boardY + 1)*(GameConstants.DRAW_SQUARE_SIZE));

        btnSave.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 165, 75, 25);
        btnXUpButton.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 10, 30, 15);
        btnXDownButton.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 30, 30, 15);
        btnYUpButton.setBounds(boardX * (square + WallWidth) + WallWidth + 10 + 35, 10, 30, 15);
        btnYDownButton.setBounds(boardX * (square + WallWidth) + WallWidth + 10 + 35, 30, 30, 15);
        btnNewButton.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 50, 75, 25);
        btnSolve.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 50 + 50, 75, 25);
        btnLoad.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 240, 75, 25);
        saveText.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 135, 75, 25);
        loadSelect.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 210, 75, 25);
        btnDelete.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 270, 75, 25);
        drawTimeSpinner.setBounds(boardX * (square + WallWidth) + WallWidth + 10, 300, 60, 22);

        for ( int x = 0 ; x < rawSquares.size() ; x++ )
        {
            for ( int y = 0 ; y < rawSquares.get(x).size() ; y++ )
            {
                rawSquares.get(x).get(y).dispose();
            }
        }

        rawSquares = new ArrayList<ArrayList<Button>>();

        for ( int x = 0 ; x < rawWalls.size() ; x++ )
        {
            rawWalls.get(x).dispose();
        }
        rawWalls = new ArrayList<Button>();

        createButtonBoard();
    }

    public void createButtonBoard()
    {
        for ( int y = 0 ; y < boardY ; y++ )
        {
            rawSquares.add(new ArrayList<Button>());
            for ( int x = 0 ; x < boardX ; x++ )
            {
                // left wall
                Button tmpButton1 = createVertical(x * (square + WallWidth), WallWidth * (y + 1) + y * (square), shell);
                if ( x == 0 )
                {
                    tmpButton1.setToolTipText(wallMap.getNextOption(tmpButton1.getToolTipText()));
                    tmpButton1.setImage(wallMap.getNextImage(tmpButton1.getImage()));
                }
                // top wall
                Button tmpButton = createHorizontal(WallWidth * (x + 1) + x * (square), y * (WallWidth + square),
                        shell);
                if ( y == 0 )
                {
                    tmpButton.setToolTipText(wallMap.getNextOption(tmpButton.getToolTipText()));
                    tmpButton.setImage(wallMap.getNextImage(tmpButton.getImage()));
                }

                // square
                rawSquares.get(y).add(
                        createSquare(WallWidth * (x + 1) + x * (square), WallWidth * (y + 1) + y * (square), shell));

            }
            // right most wall
            Button tmpButton1 = createVertical(boardX * (square + WallWidth), WallWidth * (y + 1) + y * (square),
                    shell);
            tmpButton1.setToolTipText(wallMap.getNextOption(tmpButton1.getToolTipText()));
            tmpButton1.setImage(wallMap.getNextImage(tmpButton1.getImage()));
            if ( y == boardY - 1 )
            {
                for ( int x = 0 ; x < boardX ; x++ )
                {
                    // bottom bottom walls
                    Button tmpButton = createHorizontal(WallWidth * (x + 1) + x * (square),
                            (y + 1) * (WallWidth + square), shell);
                    tmpButton.setToolTipText(wallMap.getNextOption(tmpButton.getToolTipText()));
                    tmpButton.setImage(wallMap.getNextImage(tmpButton.getImage()));

                }
            }
        }
    }

    public Button createSquare(int x, int y, Shell shell)
    {
        Button btnSquare = new Button(shell, SWT.NONE);
        btnSquare.setBounds(x, y, square, square);
        btnSquare.setToolTipText(SquareMap.getNextOption(btnSquare.getToolTipText()));
        btnSquare.setImage((SquareMap.getNextColor(btnSquare.getImage())));

        btnSquare.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                btnSquare.setToolTipText(SquareMap.getNextOption(btnSquare.getToolTipText()));
                btnSquare.setImage((SquareMap.getNextColor(btnSquare.getImage())));

            }

        });
        return btnSquare;
    }

    public Button createHorizontal(int x, int y, Shell shell)
    {
        Button btnSquare = new Button(shell, SWT.NONE);
        btnSquare.setBounds(x, y, wallLength, WallWidth);
        btnSquare.setToolTipText(wallMap.getNextOption(btnSquare.getToolTipText()));
        btnSquare.setImage(wallMap.getNextImage(btnSquare.getImage()));

        btnSquare.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                btnSquare.setToolTipText(wallMap.getNextOption(btnSquare.getToolTipText()));
                btnSquare.setImage(wallMap.getNextImage(btnSquare.getImage()));

            }

        });
        rawWalls.add(btnSquare);
        return btnSquare;
    }

    public Button createVertical(int x, int y, Shell shell)
    {
        Button btnSquare = new Button(shell, SWT.NONE);
        btnSquare.setBounds(x, y, WallWidth, wallLength);
        btnSquare.setToolTipText(wallMap.getNextOption(btnSquare.getToolTipText()));
        btnSquare.setImage(wallMap.getNextImage(btnSquare.getImage()));

        btnSquare.addListener(SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event arg0)
            {
                btnSquare.setToolTipText(wallMap.getNextOption(btnSquare.getToolTipText()));
                btnSquare.setImage(wallMap.getNextImage(btnSquare.getImage()));

            }

        });
        rawWalls.add(btnSquare);
        return btnSquare;
    }
}
