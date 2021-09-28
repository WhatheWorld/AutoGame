import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Button;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SavedMaps
{

    private final static String FileName = "src/savedMaps.txt";
    public final static String JSON_X = "X";
    public final static String JSON_Y = "Y";
    public final static String JSON_NAME = "Name";
    public final static String JSON_WALL = "Wall";
    public final static String JSON_SQUARE = "Square";
    public final static String JSON_SQUARE_TYPE = "Square Type";
    public final static String JSON_WALL_TYPE = "Wall Type";

    public SavedMaps()
    {

    }

    public void add(int X, int Y, String Name, ArrayList<ArrayList<Button>> rawSquares, ArrayList<Button> rawWalls)
    {

        JsonArray currentSaves = getSaved();

        JsonObject newMap = new JsonObject();
        newMap.addProperty(JSON_X, X);
        newMap.addProperty(JSON_Y, Y);
        newMap.addProperty(JSON_NAME, Name);

        JsonArray squares = new JsonArray();
        for ( int x = 0 ; x < rawSquares.size() ; x++ )
        {
            for ( int y = 0 ; y < rawSquares.get(x).size() ; y++ )
            {
                JsonObject square = new JsonObject();
                square.addProperty(JSON_SQUARE_TYPE, rawSquares.get(x).get(y).getToolTipText());
                squares.add(square);

            }
        }
        newMap.add(JSON_SQUARE, squares);

        JsonArray walls = new JsonArray();
        for ( int index = 0 ; index < rawWalls.size() ; index++ )
        {
            JsonObject wall = new JsonObject();
            wall.addProperty(JSON_WALL_TYPE, rawWalls.get(index).getToolTipText());
            walls.add(wall);
        }
        newMap.add(JSON_WALL, walls);

        currentSaves.add(newMap);
        
        save(currentSaves);
    }

    public JsonObject get(int x)
    {
        JsonArray currentSaves = getSaved();
        
        return currentSaves.get(x).getAsJsonObject();
    }

    private JsonObject get(String Name)
    {
        return null;
    }

    public ArrayList<String> getSavedMapNames()
    {
        JsonArray currentSaves = getSaved();
        
        ArrayList<String> mapNames = new ArrayList<String>();
        for(int index=0; index < currentSaves.size() ;index++)
        {
            mapNames.add(currentSaves.get(index).getAsJsonObject().get(JSON_NAME).getAsString());
        }
        return mapNames;
    }
    
    public boolean delete(int index)
    {
        JsonArray currentSaves = getSaved();
        
        currentSaves.remove(index);
        
        return save(currentSaves);
    }
    
    private JsonArray getSaved()
    {
        JsonParser parser = new JsonParser();
        JsonArray currentSaves = null;

        try
        {
            /*File saves = new File(FileName);
            FileReader reader = new FileReader(saves);*/
            
            InputStream savedFile=null;
            savedFile = SavedMaps.class.getClassLoader().getResourceAsStream("savedMaps.txt");
            String test = new BufferedReader(new InputStreamReader(savedFile))
                    .lines().collect(Collectors.joining("\n"));
            currentSaves = parser.parse(test).getAsJsonArray();
            
           // currentSaves = parser.parse(reader).getAsJsonArray();
        }
        catch (Exception e)
        {
        }
        if(currentSaves == null)
        {
            currentSaves = new JsonArray();
        }
        return currentSaves;
    }
    
    private boolean save(JsonElement toBeSaved)
    {
        Gson gson = new Gson();
        String json = gson.toJson(toBeSaved);
        try{
            FileWriter writer = new FileWriter(FileName);
            writer.write(json);
            writer.close();
        }catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
