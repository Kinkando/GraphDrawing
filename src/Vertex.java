import java.awt.*;
public class Vertex {
    int degree = 0, x, y, r, shift, id, x_name, y_name, stroke=2;
    String name;
    boolean isSelect;
    int nameWidth, nameHeight;
    static int idGen = 0;
    public Vertex(int x, int y) {
        this.id = ++idGen;
        this.r = 6;
        this.shift = 5;
        this.x = x;
        this.y = y;
        this.x_name = x;
        this.y_name = y;
        this.name = "v"+id;
        this.isSelect = false;
    }
    public boolean inCircle(int x0, int y0) {
        return ((x0-x)*(x0-x)+(y0-y)*(y0-y)) <= r*r;
    }
    public boolean aroundCircle(int x0, int y0) {
        return ((x0-x)*(x0-x)+(y0-y)*(y0-y)) <= r*r*100;
    }
    public boolean aroundName(int x0, int y0) {
        return x0 >= x_name-nameWidth/2 && x0 <= x_name+nameWidth/2 &&
               y0 <= (y_name-10) && y0 >= (y_name-10) - nameHeight;
    }
    public void draw(Graphics2D g) {
        g.setFont(new Font("TH Sarabun New",Font.PLAIN,24));
        g.setColor(isSelect ? Color.BLUE : Color.BLACK);
        g.setStroke(new BasicStroke(stroke));
        g.fillOval(x-r, y-r, r*2, r*2);
        g.setColor(isSelect ? Color.BLUE : Color.BLACK);
        g.fillOval(x-r+(r-shift)/2, y-r+(r-shift)/2, r*2-(r-shift), r*2-(r-shift));
        g.setColor(isSelect ? Color.BLUE : Color.BLACK);
        nameWidth = g.getFontMetrics(new Font("TH Sarabun New",Font.PLAIN,24)).stringWidth(name);
        nameHeight = g.getFontMetrics(new Font("TH Sarabun New",Font.PLAIN,24)).getHeight();
        g.drawString(name, x_name-nameWidth/2 , y_name-10);
    }
}
