import java.awt.*;
import java.awt.geom.QuadCurve2D;
public class Edge {
    static int amount = 0;
    Vertex vertexA, vertexB;
    String weight;
    double value;
    boolean isSelect;
    int weightWidth, weightHeight;
    int x_center, y_center, r_center, x_weight, y_weight, stroke=2;
    public Edge(Vertex a, Vertex b) {
        this.vertexA = a;
        this.vertexB = b;
        this.r_center = 50;
        this.weight = "0";
        this.value = 0;
        ++amount;
        this.isSelect = false;
        if (a == null)
            this.weight = null;
    }
    public boolean inLine(int x0, int y0) {
        return ((x0-x_weight)*(x0-x_weight)+(y0-y_weight)*(y0-y_weight)) <= r_center*r_center;
    }
    public boolean aroundLine(int x0, int y0) {
        return ((x0-x_center)*(x0-x_center)+(y0-y_center)*(y0-y_center)) <= r_center*r_center*50;
    }
    public boolean aroundWeight(int x0, int y0) {
        return x0 >= x_weight-weightWidth/2 && x0 <= x_weight+weightWidth/2 &&
               y0 <= y_weight && y0 >= y_weight - weightHeight;
    }
    public void draw(Graphics2D g) {
        g.setFont(new Font("TH Sarabun New",Font.PLAIN,24));
        g.setColor(isSelect ? Color.BLUE : Color.BLACK);
        g.setStroke(new BasicStroke(stroke));
        if(vertexA != vertexB)
            g.draw(new QuadCurve2D.Float(vertexA.x, vertexA.y, x_center, y_center, vertexB.x, vertexB.y));
        else{
            double angle = Math.atan2(y_center - vertexA.y, x_center - vertexA.x);
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            int rc = (int)((vertexA.r-2)*4*Math.sqrt(2));
            int xloop = vertexA.x - (vertexA.r-2)*4 + (int)(dx*rc);
            int yloop = vertexA.y - (vertexA.r-2)*4 + (int)(dy*rc);
            g.drawArc(xloop, yloop, (vertexA.r-2)*8, (vertexA.r-2)*8, 0, 360); 
        }
        weightWidth = g.getFontMetrics(new Font("TH Sarabun New",Font.PLAIN,24)).stringWidth(weight);
        weightHeight = g.getFontMetrics(new Font("TH Sarabun New",Font.PLAIN,24)).getHeight();
        g.drawString(weight, x_weight-weightWidth/2, y_weight);
    }
}