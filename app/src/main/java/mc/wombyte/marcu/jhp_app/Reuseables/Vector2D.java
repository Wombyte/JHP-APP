package mc.wombyte.marcu.jhp_app.Reuseables;


/**
 * Created by marcu on 10.12.2017.
 */

public class Vector2D {
    public int x;
    public int y;

    public double length;

    /*
     * Constructor
     */
    public Vector2D(int x, int y) {
        setXY(x, y);
    }

    public Vector2D(int ax, int ay, int bx, int by) {
        setXY( bx - ax, by - ay);
    }

    //******************************************************* static methods *******************************************************//
    /*
     * @return dot-product of the two transferred vectors
     */
    public static int dotProduct(Vector2D a, Vector2D b) {
        return a.x * b.x + a.y * b.y;
    }

    /*
     * @return cross-product of the two transferred vectors
     */
    public static int crossProduct(Vector2D a, Vector2D b) {
        return a.y * b.x - a.x * b.y;
    }

    /*
     * @return angle between the two transferred vectors
     */
    public static double angle(Vector2D a, Vector2D b) {
        double alpha = a.basicAngle();
        double beta = b.basicAngle();
        double result = beta - alpha;
        if(alpha > beta) {
            return 2*Math.PI + result;
        }
        return result;

    }

    /*
     * @return sum of the two transferred vectors
     */
    public static Vector2D sum(Vector2D a, Vector2D b) {
        return new Vector2D(a.x + b.x, a.y + b.y);
    }

    /*
     * @return difference of the two transferred vectors
     */
    public static Vector2D diff(Vector2D a, Vector2D b) {
        return new Vector2D(a.x - b.x, a.y - b.y);
    }

    //******************************************************* non-static methods *******************************************************//
    /*
     * @return length of vector: pythagoras
     */
    public double length() {
        return Math.sqrt( Math.pow(x, 2) + Math.pow(y, 2));
    }

    /*
     * @return angel to (1|0)
     */
    public double basicAngle() {
        double result = Math.acos(this.x / this.length);
        return (this.y < 0)? 2*Math.PI - result : result;
    }

    //Getter
    public int getX() { return x; }
    public int getY() { return y; }
    public int[] getXY() { return new int[] {x, y}; }
    public double getLength() { return length; }

    //Setter
    public void setX(int x) {
        this.x = x;
        length = length();
    }
    public void setY(int y) {
        this.y = y;
        length = length();
    }
    public void setXY(int x, int y) {
        setX(x);
        setY(y);
    }
}
