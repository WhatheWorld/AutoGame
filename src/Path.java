
public class Path
{

    /*
     * null = 0 top = 1 right = 2 bottom = 3 left = 4
     */
    private int direction = 0;
    private boolean locked = false;

    public Path(int direction, boolean locked)
    {
        this.direction = direction;
        this.locked = locked;
    }

    public int getDirection()
    {
        return direction;
    }

    public boolean isLocked()
    {
        return locked;
    }
}
