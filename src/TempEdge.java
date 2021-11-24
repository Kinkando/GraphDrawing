import java.awt.*;
public class TempEdge {
    Vertex vertexA;
    int x1, y1;
    public TempEdge(int x, int y) {
        this.x1 = x;
        this.y1 = y;
    }
    public void setA(Vertex a) {
        this.vertexA = a;
    }
    public void line(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(2));
        if(vertexA == null)
            return;
        if(vertexA.inCircle(x1, y1)){
            double angle = Math.atan2(y1 - vertexA.y, x1 - vertexA.x);
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            int rc = (int)((vertexA.r-2)*4*Math.sqrt(2));
            int xloop = vertexA.x - (vertexA.r-2)*4 + (int)(dx*rc);
            int yloop = vertexA.y - (vertexA.r-2)*4 + (int)(dy*rc);
            g.drawArc(xloop, yloop , (vertexA.r-2)*8, (vertexA.r-2)*8, 0, 360); 
        }
        else
            g.drawLine(vertexA.x, vertexA.y, x1, y1);
    }
}
