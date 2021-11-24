import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
public class GraphDrawing extends JFrame implements MouseListener, MouseMotionListener, KeyListener {
    class Backup {
        ArrayList<Vertex> VertexsBackup;
        ArrayList<Edge> Edge_sBackup;
        public Backup() {
            this.VertexsBackup = Vertexs;
            this.Edge_sBackup = Edges;
            VertexsBackup.forEach((v) -> {
                v.isSelect = false;
            });
            Edges.forEach((e) -> {
                e.isSelect = false;
            });
        }
    }
    class PrimTableModel extends AbstractTableModel{
        ArrayList<String> columnNames;
        LinkedList<Object> rowData;
        public PrimTableModel(ArrayList<Vertex> Vertexs) {
            columnNames = new ArrayList<>();
            rowData = new LinkedList<>();
            columnNames.add("N");
            for(Vertex v : Vertexs) 
                columnNames.add(v.name);
        }
        public PrimTableModel() {
            columnNames = new ArrayList<>();
            rowData = new LinkedList<>();
            columnNames.add("ชนิดของกราฟ");
        }
        @Override
        public int getRowCount() {
            return rowData.size()/columnNames.size();
        }
        @Override
        public int getColumnCount() {
            return columnNames.size();
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rowData.get(getColumnCount()*rowIndex+columnIndex);
        }
        @Override
        public void setValueAt(Object value, int row, int col) {
            rowData.addLast(value);
            fireTableCellUpdated(row,col);
        }
        @Override
        public String getColumnName(int columnIndex) {
            return columnNames.get(columnIndex);
        }
        public void deleteCurrentRow() {
            for(int i=0;i<getColumnCount();i++)
                rowData.removeLast();
            fireTableRowsDeleted(getRowCount(), getRowCount());
        }
    }
    class DijkstraTableModel extends AbstractTableModel{
        String[] columnNames;
        Object[][] rowData;
        public DijkstraTableModel(ArrayList<Vertex> Vertexs) {
            setFont(THSarabunFont);
            columnNames = new String[3];
            rowData = new Object[Vertexs.size()][3];
            columnNames[0] = "จุดยอด(v)";
            columnNames[1] = "dist(v)";
            columnNames[2] = "prev(v)";
            int count=0;
            for(Vertex v : Vertexs) {
                rowData[count][0] = v.name;
                rowData[count][1] = '\u221E';
                rowData[count++][2] = "null";
            }
        }
        @Override
        public int getRowCount() {
            return rowData.length;
        }
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rowData[rowIndex][columnIndex];
        }
        @Override
        public void setValueAt(Object value, int row, int col) {
            rowData[row][col] = value;
            fireTableCellUpdated(row,col);
        }
        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }
    }
    class Display implements MouseMotionListener, MouseListener, MouseWheelListener {
        JLabel buttonLabel = new JLabel();
        int x,y;
        boolean saveText,openText,clearText,helpText,searchText,undoText,redoText,homeText,backText,nextText,swapText;
        public Display() {
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
            buttonLabel.setFont(new Font("TH Sarabun New",Font.BOLD,20));
            buttonLabel.setText("");
            buttonLabel.setHorizontalAlignment(SwingConstants.CENTER);
            buttonLabel.setVerticalAlignment(SwingConstants.CENTER);
            buttonLabel.setOpaque(true);
            buttonLabel.setBackground(new Color(254,255,208));
            buttonLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            buttonLabel.setSize(new Dimension(100,100));
            getLayeredPane().add(buttonLabel,JLayeredPane.PALETTE_LAYER);
            buttonLabel.setVisible(false);
        }
        @Override
        public void mouseMoved(MouseEvent me) {
            x = me.getX();
            y = me.getY();
            if(!isVisible())
                return ;
            setValue();
        }
        @Override
        public void mouseDragged(MouseEvent me) {
        }

        @Override
        public void mouseClicked(MouseEvent me) {
        }

        @Override
        public void mousePressed(MouseEvent me) {
        }

        @Override
        public void mouseReleased(MouseEvent me) {
        }

        @Override
        public void mouseEntered(MouseEvent me) {
            x = me.getX();
            y = me.getY();
            if(!isVisible())
                return ;
            setValue();
        }

        @Override
        public void mouseExited(MouseEvent me) {
            x = me.getX();
            y = me.getY();
            if(!isVisible())
                return ;
            setValue();
        }
        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            if(startVertex==null)
                return;
            if(mwe.getWheelRotation()==1 && algorithmToolbar.isVisible()) {
                swapButt.setIcon(new ImageIcon("Icon/Graph.png"));
                graphButtAction(null);
                setWeight();
                answerLabel.setVisible(currentRound==Vertexs.size()-1);
            }
            else if(mwe.getWheelRotation()==-1 && algorithmToolbar.isVisible()) {
                swapButt.setIcon(new ImageIcon("Icon/Table.png"));
                tableButtAction(null);
                setWeight();
                answerLabel.setVisible(false);
            }
        }
        public void setDisplay(boolean v) {
            buttonLabel.setVisible(v);
        }
        public void setValue() {
            Graphics2D g = (Graphics2D) getGraphics();
            FontMetrics met = g.getFontMetrics(new Font("TH Sarabun New",Font.BOLD,20));
            int height = met.getHeight();
            saveText =  x>=saveButt.getX()  && x<=saveButt.getX()+saveButt.getWidth() &&
                        y>=saveButt.getY()  && y<=saveButt.getY()+saveButt.getHeight() && mainToolbar.isVisible();
            openText =  x>=openButt.getX()  && x<=openButt.getX()+openButt.getWidth() &&
                        y>=openButt.getY()  && y<=openButt.getY()+openButt.getHeight() && mainToolbar.isVisible();
            clearText = x>=clearButt.getX() && x<=clearButt.getX()+clearButt.getWidth() &&
                        y>=clearButt.getY() && y<=clearButt.getY()+clearButt.getHeight() && mainToolbar.isVisible();
            helpText =  x>=helpButt.getX()  && x<=helpButt.getX()+helpButt.getWidth() &&
                        y>=helpButt.getY()  && y<=helpButt.getY()+helpButt.getHeight() && mainToolbar.isVisible();
            searchText =  x>=searchButt.getX() && x<=searchButt.getX()+searchButt.getWidth() &&
                        y>=searchButt.getY() && y<=searchButt.getY()+searchButt.getHeight() && mainToolbar.isVisible();
            undoText =  x>=undoButt.getX()  && x<=undoButt.getX()+undoButt.getWidth() &&
                        y>=undoButt.getY()  && y<=undoButt.getY()+undoButt.getHeight() && mainToolbar.isVisible();
            redoText =  x>=redoButt.getX()  && x<=redoButt.getX()+redoButt.getWidth() &&
                        y>=redoButt.getY()  && y<=redoButt.getY()+redoButt.getHeight() && mainToolbar.isVisible();
            homeText =  x>=homeButt.getX() && x<=homeButt.getX()+homeButt.getWidth() &&
                        y>=homeButt.getY() && y<=homeButt.getY()+homeButt.getHeight() && algorithmToolbar.isVisible();
            backText =  x>=backButt.getX() && x<=backButt.getX()+backButt.getWidth() &&
                        y>=backButt.getY() && y<=backButt.getY()+backButt.getHeight() && algorithmToolbar.isVisible();
            nextText =  x>=nextButt.getX() && x<=nextButt.getX()+nextButt.getWidth() &&
                        y>=nextButt.getY() && y<=nextButt.getY()+nextButt.getHeight() && algorithmToolbar.isVisible();
            swapText =  x>=swapButt.getX() && x<=swapButt.getX()+swapButt.getWidth() &&
                        y>=swapButt.getY() && y<=swapButt.getY()+swapButt.getHeight() && algorithmToolbar.isVisible();
        
            String text = saveText ? "บันทึก (Ctrl+S)" : openText ? "เปิด (Ctrl+O)" : clearText ? "ล้าง (Ctrl+C)" : helpText ? "คำแนะนำ (Ctrl+I)" : 
                          searchText ? "ค้นหา (Ctrl+P)" : undoText ? "เลิกทำ (Ctrl+Z)" : redoText ? "ทำซ้ำ (Ctrl+Y)" :
                          homeText ? "หน้าแรก (Ctrl+H)" : backText ? "ย้อนกลับ (Left Arrow Keys)" : nextText ? "ถัดไป (Right Arrow Keys)" : swapText ? "เลื่อน (Scroll or Up/Down Arrow Keys)" : "";
            Color color = new Color(188,234,236);
            saveButt.setContentAreaFilled(saveText);
            saveButt.setBackground(saveText ? color : Color.white);
            openButt.setContentAreaFilled(openText);
            openButt.setBackground(openText ? color : Color.white);
            clearButt.setContentAreaFilled(clearText);
            clearButt.setBackground(clearText ? color : Color.white);
            helpButt.setContentAreaFilled(helpText);
            helpButt.setBackground(helpText ? color : Color.white);
            searchButt.setContentAreaFilled(searchText);
            searchButt.setBackground(searchText ? color : Color.white);
            undoButt.setContentAreaFilled(undoText);
            undoButt.setBackground(undoText ? color : Color.white);
            redoButt.setContentAreaFilled(redoText);
            redoButt.setBackground(redoText ? color : Color.white);
            homeButt.setContentAreaFilled(homeText);
            homeButt.setBackground(homeText ? color : Color.white);
            backButt.setContentAreaFilled(backText);
            backButt.setBackground(backText ? color : Color.white);
            nextButt.setContentAreaFilled(nextText);
            nextButt.setBackground(nextText ? color : Color.white);
            buttonLabel.setText(text);
            buttonLabel.setSize(met.stringWidth(text)+10,height);
            buttonLabel.setLocation(x+5,y+5);
            buttonLabel.setVisible(saveText||openText||clearText||helpText||searchText||undoText||redoText||
                                   homeText||backText||nextText||swapText);
        }
    }
    boolean inSelect;
    int currentRound = 0;
    double sumWeight = 0;
    Display display = new Display();
    String startVertex, filePath="";
    String[] weightChange, rowChange, rowUnchange;
    ArrayList<ArrayList<Vertex>> pointPath = new ArrayList<>();
    ArrayList<ArrayList<Edge>> shortestPath = new ArrayList<>();
    Stack<ArrayList<Object>> listUndo = new Stack<>();
    Stack<ArrayList<Object>> listRedo = new Stack<>();
    Stack<ArrayList<Object>> undo = new Stack<>();
    Stack<ArrayList<Object>> redo = new Stack<>();
    JScrollPane tableScroll;
    JScrollPane solutionScroll;
    JScrollPane roundTabScroll;
    JComboBox roundTab = new JComboBox();
    JButton swapButt = new JButton(new ImageIcon("Icon/Table.png"));
    JButton searchButt = new JButton(new ImageIcon("Icon/Search.png"));
    JButton homeButt = new JButton(new ImageIcon("Icon/Home.png"));
    JButton saveButt = new JButton(new ImageIcon("Icon/Save.png"));
    JButton openButt = new JButton(new ImageIcon("Icon/Open.png"));
    JButton helpButt = new JButton(new ImageIcon("Icon/Help.png"));
    JButton clearButt = new JButton(new ImageIcon("Icon/Delete.png"));
    JButton nextButt = new JButton(new ImageIcon("Icon/Next.png"));
    JButton backButt = new JButton(new ImageIcon("Icon/Back.png"));
    JButton undoButt = new JButton(new ImageIcon("Icon/Undo.png"));
    JButton redoButt = new JButton(new ImageIcon("Icon/Redo.png"));
    JLabel helpLabel = new JLabel();
    JLabel answerLabel = new JLabel();
    JLabel solutionLabel = new JLabel();
    JLabel graphTypeLabel = new JLabel();
    JLabel detailLabel = new JLabel();
    JLabel vertexLabel = new JLabel();
    JLabel edgeLabel = new JLabel();
    JPanel mainToolbar = new JPanel();
    JPanel algorithmToolbar = new JPanel();
    JPanel boxHelp = new JPanel();
    JTextField vertexText = new JTextField();
    JTextField edgeText = new JTextField();
    JFrame frameHelp = new JFrame("คู่มือการใช้งานโปรแกรมเบื้องต้น");
    JFrame dijkstraTableFrame = new JFrame("ตาราง");
    PrimTableModel primModel = new PrimTableModel();
    DijkstraTableModel dijkstraModel;
    ArrayList<DijkstraTableModel> dijkstraTable;
    ArrayList<Vertex> Vertexs = new ArrayList<>();
    ArrayList<Edge> Edges = new ArrayList<>();
    LinkedList<Edge> T = new LinkedList<>();
    LinkedList<Vertex> A = new LinkedList<>();
    LinkedList<Vertex> N = new LinkedList<>();
    Vertex sourceVertex, targetVertex;
    Canvas showingArea, drawingArea;
    Object selected = null;
    TempEdge TempEdge = null;
    String mode = "Vertex";
    Font THSarabunFont = new Font("TH Sarabun New", Font.BOLD, 28);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    BufferedImage grid = null;
    public static void main(String[] args) {
        try {
            GraphDrawing gui = new GraphDrawing();
        } 
        catch (Exception ex) {}
    }
    public GraphDrawing() {
        setTitle("Untitled - Graph Drawing");
//        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void enclosing(WindowEvent e) {
                Object[] options = {"Save","Don't Save","Cancel"};
                int n = JOptionPane.showOptionDialog(new JFrame(),
                    "Do you want to save changes to " + (filePath.length()==0 ? "Untitled" : 
                    filePath.substring(filePath.lastIndexOf("\\")+1,filePath.lastIndexOf(".json"))) + "?", "Graph Drawing",
                    JOptionPane.YES_NO_CANCEL_OPTION, -1, null, options, options[2]);
                if(n==0) {
                    try {
                        if(filePath.length()>0)
                            save(filePath);
                        else
                            saveButtAction(null);
                    }
                    catch(IOException ex) {}
                }
                if((n==0&&filePath.length()>0) || n==1) 
                    System.exit(0);
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                detailLabel.setVisible(false);
            }
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                    Backup backup = new GsonBuilder().create().fromJson(bufferedReader, Backup.class);
                    ArrayList<Vertex> vertex = backup.VertexsBackup;
                    ArrayList<Edge> edge = backup.Edge_sBackup;
                    if(vertex.size()+edge.size() == Vertexs.size()+Edges.size()) {
                        boolean currentSave = true;
                        for(int i=0;i<Vertexs.size() && currentSave;i++) {
                            Vertex v1 = Vertexs.get(i);
                            Vertex v2 = vertex.get(i);
                            if(v1.degree!=v2.degree || !v1.name.equals(v2.name) || v1.x!=v2.x || v1.y!=v2.y ||
                               v1.x_name!=v2.x_name || v1.y_name!=v2.y_name)
                                currentSave = false;
                        }
                        for(int i=0;i<Edges.size() && currentSave;i++) {
                            Edge e1 = Edges.get(i);
                            Edge e2 = edge.get(i);
                            if(!e1.weight.equals(e2.weight) || e1.x_center!=e2.x_center || e1.y_center!=e2.y_center || 
                               e1.x_weight!=e2.x_weight || e1.y_weight!=e2.y_weight)
                                currentSave = false;
                        }
                        if(currentSave)
                            System.exit(0);
                    }
                    enclosing(e);
                } 
                catch (FileNotFoundException ex) {
                    if(!undo.isEmpty() || !Vertexs.isEmpty() || !Edges.isEmpty())
                        enclosing(e);
                    else
                        System.exit(0);
                }
            }
        });
        
        drawingArea = new Canvas() {
            @Override
            public void paint(Graphics g) {
                draw(1);
                detailLabel.setVisible(false);
            }
        };
        drawingArea.setBackground(Color.white);
        drawingArea.addMouseListener(this);
        drawingArea.addMouseMotionListener(this);
        drawingArea.addKeyListener(this);
        drawingArea.setBounds(0, 0, screenSize.width, screenSize.height);
        
        showingArea = new Canvas() {
            @Override
            public void paint(Graphics g) {
                draw(2);
            }
        };
        showingArea.setBackground(Color.white);
        showingArea.setBounds(0, 0, screenSize.width, screenSize.height);
        
        saveButt.setBounds(10, 12, 30, 30);
        saveButt.setFocusPainted(false);
        saveButt.setBorder(null);
        saveButt.setContentAreaFilled(false);
        saveButt.addActionListener((ActionEvent e) -> {
            try {
                saveButtAction(e);
            } 
            catch (IOException ex) {}
        });
        
        openButt.setBounds(50, 12, 30, 30);
        openButt.setFocusPainted(false);
        openButt.setBorder(null);
        openButt.setContentAreaFilled(false);
        openButt.addActionListener((ActionEvent e) -> {
            try {
                openButtAction(e);
            } 
            catch (IOException ex) {}
        });
        
        undoButt.setBounds(90, 12, 30, 30);
        undoButt.setFocusPainted(false);
        undoButt.setBorder(null);
        undoButt.setContentAreaFilled(false);
        undoButt.addActionListener(this::undoButtAction);
        undoButt.setEnabled(false);
        
        redoButt.setBounds(130, 12, 30, 30);
        redoButt.setFocusPainted(false);
        redoButt.setBorder(null);
        redoButt.setContentAreaFilled(false);
        redoButt.addActionListener(this::redoButtAction);
        redoButt.setEnabled(false);
        
        clearButt.setBounds(170, 12, 30, 30);
        clearButt.setFocusPainted(false);
        clearButt.setBorder(null);
        clearButt.setContentAreaFilled(false);
        clearButt.addActionListener(this::clearButtAction);
        clearButt.setEnabled(false);
        
        searchButt.setBounds(210 ,12, 30, 30);
        searchButt.setFocusPainted(false);
        searchButt.setBorder(null);
        searchButt.setContentAreaFilled(false);
        searchButt.addActionListener(this::searchButtAction);
        searchButt.setEnabled(false);
        
        boxHelp.setBackground(Color.white);
        helpLabel.setFont(THSarabunFont);
        helpButt.setBounds(250, 12, 30, 30);
        helpButt.setFocusPainted(false);
        helpButt.setBorder(null);
        helpButt.setContentAreaFilled(false);
        helpButt.addActionListener(this::helpButtAction);
        frameHelp.add(boxHelp);
        
        graphTypeLabel.setFont(THSarabunFont);
        graphTypeLabel.setText("");
        graphTypeLabel.setBackground(Color.white);
        graphTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        graphTypeLabel.setVerticalTextPosition(SwingConstants.CENTER);
        graphTypeLabel.setBounds(SwingConstants.CENTER,12, screenSize.width, 30);
        
        mainToolbar.setLayout(null);
        mainToolbar.addMouseMotionListener(display);
        mainToolbar.addMouseListener(display);
        mainToolbar.add(saveButt);
        mainToolbar.add(openButt);
        mainToolbar.add(clearButt);
        mainToolbar.add(helpButt);
        mainToolbar.add(searchButt);
        mainToolbar.add(undoButt);
        mainToolbar.add(redoButt);
        mainToolbar.add(graphTypeLabel);
        mainToolbar.setBounds(0,0,screenSize.width,60);
        
        homeButt.setBounds(10, 12, 30, 30);
        homeButt.setFocusPainted(false);
        homeButt.setBorder(null);
        homeButt.setContentAreaFilled(false);
        homeButt.addActionListener(this::homeButtAction);
        
        backButt.setBounds(50,12,30,30);
        backButt.setFocusPainted(false);
        backButt.setBorder(null);
        backButt.setContentAreaFilled(false);
        backButt.addActionListener(this::backButtAction);
        
        nextButt.setBounds(90,12,30,30);
        nextButt.setFocusPainted(false);
        nextButt.setBorder(null);
        nextButt.setContentAreaFilled(false);
        nextButt.addActionListener(this::nextButtAction);
        
        swapButt.setBounds(130, 12, 30, 30);
        swapButt.setFocusPainted(false);
        swapButt.setBorder(null);
        swapButt.setContentAreaFilled(false);
        swapButt.addActionListener(this::swapButtAction);
        
        roundTabScroll = new JScrollPane();
        roundTabScroll.setBounds(170, 2, 150, 55);
        
        answerLabel.setText("");
        answerLabel.setFont(THSarabunFont);
        answerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        answerLabel.setVerticalAlignment(SwingConstants.CENTER);
        answerLabel.setBounds(320,0,screenSize.width-320,60);
        
        solutionLabel.setFont(THSarabunFont);
        solutionLabel.setText("");
        solutionLabel.setBackground(Color.white);
        solutionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        solutionLabel.setVerticalAlignment(SwingConstants.CENTER);
        solutionScroll = new JScrollPane(solutionLabel);
        solutionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        solutionScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        solutionScroll.setBounds(0, 60, screenSize.width, 250);
        solutionScroll.setBorder(null);
        solutionScroll.getViewport().setBackground(Color.white);
        getContentPane().add(solutionScroll);
        solutionScroll.setVisible(false);
        
        tableScroll = new JScrollPane();
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setBounds(0, 310, screenSize.width, screenSize.height-getHeight()-310);
        tableScroll.setBorder(null);
        getContentPane().add(tableScroll);
        tableScroll.setVisible(false);
        
        algorithmToolbar.setLayout(null);
        algorithmToolbar.addMouseMotionListener(display);
        algorithmToolbar.addMouseListener(display);
//        showingArea.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyTyped(KeyEvent ke) {
//                int key = ke.getKeyCode();
//                System.out.println(key);
//            }
//
//            @Override
//            public void keyPressed(KeyEvent ke) {
//                int key = ke.getKeyCode();
//                switch (key) {
//                    case KeyEvent.VK_LEFT: 
//                        if(backButt.isEnabled())
//                            backButtAction(null);
//                        break;
//                    case KeyEvent.VK_RIGHT:
//                        if(nextButt.isEnabled())
//                            nextButtAction(null);
//                        break;
//                    case KeyEvent.VK_UP:
//                        swapButt.setIcon(new ImageIcon("Icon/Graph.png"));
//                        graphButtAction(null);
//                        setWeight();
//                        answerLabel.setVisible(currentRound==Vertexs.size()-1);
//                        break;
//                    case KeyEvent.VK_DOWN:
//                        swapButt.setIcon(new ImageIcon("Icon/Table.png"));
//                        tableButtAction(null);
//                        setWeight();
//                        answerLabel.setVisible(false);
//                        break;
//                    default:
//                        break;
//                }
//
//            }
//
//            @Override
//            public void keyReleased(KeyEvent ke) {
//
//            }
//        });
        
        algorithmToolbar.add(homeButt);
        algorithmToolbar.add(backButt);
        algorithmToolbar.add(nextButt);
        algorithmToolbar.add(swapButt);
        algorithmToolbar.add(roundTabScroll);
        algorithmToolbar.add(answerLabel);
        algorithmToolbar.setBounds(0,0,screenSize.width,60);
        
        detailLabel.setFont(new Font("TH Sarabun New",Font.BOLD,20));
        detailLabel.setText("");
        detailLabel.setBackground(Color.white);
        detailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailLabel.setVerticalAlignment(SwingConstants.CENTER);
        detailLabel.setBackground(Color.white);
        detailLabel.setSize(new Dimension(100,100));
        getContentPane().add(detailLabel);
        detailLabel.setVisible(false);
        
        vertexLabel.setFont(THSarabunFont);
        vertexLabel.setText("ชื่อจุดยอด");
        vertexLabel.setLocation(screenSize.width-340,5);
        vertexLabel.setSize(new Dimension(90,50));
        getLayeredPane().add(vertexLabel,JLayeredPane.PALETTE_LAYER);
        vertexLabel.setVisible(false);
        
        vertexText.setText("");
        vertexText.setFont(THSarabunFont);
        vertexText.setSize(new Dimension(235,50));
        vertexText.setLocation(screenSize.width-vertexText.getWidth()-5,5);
        vertexText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if(ke.getKeyCode()==10&&vertexText.getText().length()>0)
                    settingName();
                vertexText.setEditable(ke.getKeyCode()!=44&&ke.getKeyCode()!=32);
            }
        });
        getContentPane().add(vertexText);
        vertexText.setVisible(false);
        
        edgeLabel.setFont(THSarabunFont);
        edgeLabel.setText("ค่าน้ำหนักเส้นเชื่อม");
        edgeLabel.setLocation(screenSize.width-415,5);
        edgeLabel.setSize(new Dimension(180,50));
        getLayeredPane().add(edgeLabel,JLayeredPane.PALETTE_LAYER);
        edgeLabel.setVisible(false);
        
        edgeText.setText("");
        edgeText.setFont(THSarabunFont);
        edgeText.setSize(new Dimension(235,50));
        edgeText.setLocation(screenSize.width-edgeText.getWidth()-5,5);
        edgeText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                String value = edgeText.getText();
                if(ke.getKeyCode()==10) {
                    settingWeight();
                    return;
                }
                else if(value.equals("0")&&(ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9'))
                    edgeText.setText("");
                else if(value.length()==1&&ke.getKeyCode()==8)
                    edgeText.setText("0");
                edgeText.setEditable((!value.contains(".")&&ke.getKeyChar()=='.')||((ke.getKeyChar()>='0'&&ke.getKeyChar()<='9'))||(value.length()>1&&ke.getKeyCode()==8));
            }
        });
        getContentPane().add(edgeText);
        edgeText.setVisible(false);
        
        getLayeredPane().add(algorithmToolbar,JLayeredPane.PALETTE_LAYER);
        getContentPane().add(mainToolbar);
        setBounds(0, 0, (screenSize.width - getWidth()), (screenSize.height - getHeight()));
        add(drawingArea);
        getLayeredPane().add(showingArea,JLayeredPane.DEFAULT_LAYER);
        algorithmToolbar.setVisible(false);
        showingArea.setVisible(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        show();
    }
    public void deleteNowUndoState() {
        LinkedList<Vertex> ver = new LinkedList<>();
        LinkedList<Edge> edg = new LinkedList<>();
        for(Vertex v : Vertexs) 
            ver.add(v);
        for(Edge ed : Edges) 
            edg.add(ed);
        if(!listUndo.isEmpty() && listUndo.peek() != null && ver.size()+edg.size()==listUndo.peek().size()) {
            boolean check = false;
            for(Object obj : listUndo.peek()) {
                if(obj instanceof Vertex) {
                    Vertex v1 = (Vertex)obj;
                    Vertex v2 = ver.getFirst();
                    if( v1.degree==v2.degree && v1.name.equals(v2.name) && 
                       v1.x==v2.x && v1.y==v2.y && v1.x_name==v2.x_name && v1.y_name==v2.y_name)
                        ver.removeFirst();
                    else {
                        check = true;
                        break;
                    }
                }
                else {
                    Edge ed1 = (Edge)obj;
                    Edge ed2 = edg.getFirst();
                    if(ed1.weight.equals(ed2.weight) && 
                       ed1.x_center==ed2.x_center && ed1.y_center==ed2.y_center && 
                       ed1.x_weight==ed2.x_weight && ed1.y_weight==ed2.y_weight)
                        edg.removeFirst();
                    else {
                        check = true;
                        break;
                    }
                }
            }
            if(!check) {
                listUndo.pop();
                undo.pop();
            }
        }
        else if(Vertexs.isEmpty() && Edges.isEmpty()&&!undo.isEmpty()) {
            undo.pop();
            listUndo.pop();
        }
    }
    public Vertex insertVertex(Vertex v) {
        Vertex vertex = new Vertex(v.x,v.y);
        Vertex.idGen--;
        vertex.degree = v.degree;
        vertex.name = v.name;
        vertex.x_name = v.x_name;
        vertex.y_name = v.y_name;
        return vertex;
    }
    public Edge insertEdge(Edge ed) {
        Edge edge = new Edge(ed.vertexA,ed.vertexB);
        Edge.amount--;
        edge.weight = ed.weight;
        edge.x_center = ed.x_center;
        edge.y_center = ed.y_center;
        edge.x_weight = ed.x_weight;
        edge.y_weight = ed.y_weight;
        return edge;
    }
    public void setVertex(Vertex vertex, Vertex v) {
        vertex.x = v.x;
        vertex.y = v.y;
        vertex.x_name = v.x_name;
        vertex.y_name = v.y_name;
        vertex.name = v.name;
        vertex.degree = v.degree;
        vertex.isSelect = false;
    }
    public void setEdge(Edge edge, Edge ed) {
        edge.x_center = ed.x_center;
        edge.y_center = ed.y_center;
        edge.x_weight = ed.x_weight;
        edge.y_weight = ed.y_weight;
        edge.weight = ed.weight;
        edge.isSelect = false;
    }
    public void undoButtAction(ActionEvent e) {
        deleteNowUndoState();
        final ArrayList<Object> inputRedo = new ArrayList<>();
        final ArrayList<Object> inputListRedo = new ArrayList<>();
        for(Vertex v : Vertexs) {
            inputListRedo.add(insertVertex(v));
            inputRedo.add(v);
        }
        for(Edge ed : Edges) {
            inputListRedo.add(insertEdge(ed));
            inputRedo.add(ed);
        }
        Vertexs.removeAll(Vertexs);
        Edges.removeAll(Edges);
        if(!listUndo.isEmpty()) {
            if(listUndo.peek() == null) {
                listUndo.pop();
                undo.pop();
            }
            else {
                for(Object obj : undo.pop()) {
                    if(obj instanceof Vertex) {
                        Vertexs.add((Vertex)obj);
                    }
                    else { 
                        Edges.add((Edge)obj);
                    }
                }
                int countV=0,countE=0;
                for(Object obj : listUndo.pop()) {
                    if(obj instanceof Vertex) 
                        setVertex(Vertexs.get(countV++),(Vertex)obj);
                    else
                        setEdge(Edges.get(countE++),(Edge)obj);
                }
            }
        }
        redo.push(!inputRedo.isEmpty() ? inputRedo : null);
        listRedo.push(!inputListRedo.isEmpty() ? inputListRedo : null);
        actionScript();
        draw(1);
        setGraphType();
        searchButt.setEnabled(checkCondition());
        undoButt.setEnabled(!Vertexs.isEmpty() || !Edges.isEmpty() || !undo.isEmpty());
        redoButt.setEnabled(!redo.isEmpty());
        clearButt.setEnabled(!Vertexs.isEmpty() || !Edges.isEmpty());
        vertexLabel.setVisible(false);
        vertexText.setVisible(false);
        edgeLabel.setVisible(false);
        edgeText.setVisible(false);
    }
    public void redoButtAction(ActionEvent e) {
        ArrayList<Object> inputUndo = new ArrayList<>();
        ArrayList<Object> inputListUndo = new ArrayList<>();
        for(Vertex v : Vertexs) {
            inputListUndo.add(insertVertex(v));
            inputUndo.add(v);
        }
        for(Edge ed : Edges) {
            inputListUndo.add(insertEdge(ed));
            inputUndo.add(ed);
        }
        Vertexs.removeAll(Vertexs);
        Edges.removeAll(Edges);
        if(!redo.isEmpty()) {
            if(redo.peek() == null) {
                redo.pop();
                listRedo.pop();
            }
            else {
                for(Object obj : redo.pop()) {
                    if(obj instanceof Vertex) 
                        Vertexs.add((Vertex)obj);
                    else 
                        Edges.add((Edge)obj);
                }
                int countV=0,countE=0;
                for(Object obj : listRedo.pop()) {
                    if(obj instanceof Vertex) 
                        setVertex(Vertexs.get(countV++),(Vertex)obj);
                    else
                        setEdge(Edges.get(countE++),(Edge)obj);
                }
            }
        }
        undo.push(!inputUndo.isEmpty() ? inputUndo : null);
        listUndo.push(!inputListUndo.isEmpty() ? inputListUndo : null);
        actionScript();
        draw(1);
        setGraphType();
        searchButt.setEnabled(checkCondition());
        undoButt.setEnabled(!Vertexs.isEmpty() || !Edges.isEmpty() || !undo.isEmpty());
        redoButt.setEnabled(!redo.isEmpty());
        clearButt.setEnabled(!Vertexs.isEmpty() || !Edges.isEmpty());
        vertexText.setVisible(false);
        edgeText.setVisible(false);
        vertexLabel.setVisible(false);
        edgeLabel.setVisible(false);
    }
    public void settingWeight() {
        if(selected!=null && selected instanceof Edge) {
            Edge e = (Edge)selected;
            String weight = edgeText.getText();
            for(int i=0;i<weight.length();i++) {
                if(weight.charAt(i) != '0') {
                    weight = weight.substring(i);
                    break;
                } 
            }
            if(weight.indexOf('.')==0)
                weight = '0'+weight;
            e.weight = weight;
            e.value = Double.parseDouble(edgeText.getText());
            draw(1);
        }
    }
    public void settingName() {
        if(selected!=null && selected instanceof Vertex) {
            Vertex v = (Vertex)selected;
            for(Vertex ver : Vertexs)
                if(ver.name.equals(vertexText.getText())&&ver!=v) {
                    JOptionPane.showMessageDialog(this,"ชื่อจุดยอดซ้ำ");
                    return;
                }
            v.name = vertexText.getText();
            draw(1);
        }
    }
    public void setGraphType() {
        int type = checkGraphInformation();
        String categorical = "";
        if(!Vertexs.isEmpty() && type>1 && type!=4) 
            categorical = type==2 ? "กราฟหลายเชิง" : type==3 ? "กราฟเทียม" : type==5 ? "กราฟบริบูรณ์" : "กราฟเชิงเดี่ยว";
        graphTypeLabel.setText(categorical);
        Graphics2D g = (Graphics2D)mainToolbar.getGraphics();
        FontMetrics fontMetric = g.getFontMetrics(THSarabunFont);
        int width = fontMetric.stringWidth(categorical);
        graphTypeLabel.setLocation(screenSize.width/2-width/2,12);
        graphTypeLabel.setSize(new Dimension(width,30));
    }
    public boolean checkCondition() {
        return checkGraphInformation()>=5;
    }
    public int checkGraphInformation() {
        if(Vertexs.isEmpty())
            return 0;
        boolean loop = false;
        boolean parallel = false;
        for(Vertex v : Vertexs) {
            if(v.degree == 0)
                return 1;
            ArrayList<Vertex> tempVertex = new ArrayList<>();
            for(Edge e : Edges)
                if(e.vertexA == v || e.vertexB == v) {
                    Vertex side = e.vertexA==v ? e.vertexB : e.vertexA;
                    if(tempVertex.size()>0 && tempVertex.contains(side))
                        parallel = true;
                    if(e.vertexA == e.vertexB)
                        loop = true;
                    tempVertex.add(side);
                }
        }
        if(loop)
            return 3;
        else if(parallel)
            return 2;
        ArrayList<Vertex> selectVertex = new ArrayList<>();
        selectVertex.add(Vertexs.get(0));
        for(int round=0;round<Vertexs.size() && round<selectVertex.size(); round++) {
            Vertex start = selectVertex.get(round);
            ArrayList<Vertex> linkVertex = new ArrayList<>();
            for(Edge e : Edges) 
                if(e.vertexA == start || e.vertexB == start)
                    linkVertex.add(e.vertexA == start ? e.vertexB : e.vertexA);
            for(Vertex v : linkVertex)
                if(!selectVertex.contains(v))
                    selectVertex.add(v);
        }
        return selectVertex.size() != Vertexs.size() ? 4 : Edges.size()==Vertexs.size()*(Vertexs.size()-1)/2 ? 5 : 6;
    }
    public void previousStepPrim() {
        if(currentRound == 1)
            return;
        primModel.deleteCurrentRow();
        Edge uw = T.getLast();
        A.add(N.getLast());
        T.removeLast();
        N.removeLast();
        currentRound--;
        sumWeight -= uw.value;
        uw.stroke = 2;
        uw.isSelect = false;
        setWeight();
    }
    public void nextStepPrim() {
        if(currentRound == Vertexs.size()-1)
            return;
        if(currentRound == 0)
            for(int i=0;i<Vertexs.size();i++)
                if(Vertexs.get(i).name.equals(startVertex)) {
                    A.remove(Vertexs.get(i));
                    N.addLast(Vertexs.get(i));
                    break;
                }
        Vertex u = N.getLast();
        Vertex v = null;
        Edge uv = null;
        primModel.setValueAt(u.name,currentRound,0);
        weightChange[currentRound] = "";
        rowUnchange[currentRound] = "";
        rowChange[currentRound] = "";
        double minWeight = Double.MAX_VALUE;
        int nextIndex = 0;
        for(int j=0;j<Vertexs.size();j++) {
            Edge uw = null;
            Vertex w = Vertexs.get(j);
            String previousCell = currentRound>0 ? primModel.getValueAt(currentRound-1,j+1).toString() : null;
            for (Edge t : Edges)
                if (((t.vertexA == u && t.vertexB == w) || (t.vertexA == w && t.vertexB == u)) && !T.contains(t)) {
                    uw = t;
                    break;
                }
            if(N.contains(w))
                primModel.setValueAt("-",currentRound,j+1);
            else if(uw != null) {
                double weight = uw.value;
                double previousWeight = previousCell!=null && previousCell.length()>1 ? 
                                     Double.parseDouble(previousCell.substring(0,previousCell.indexOf(","))) : 
                                     Double.MAX_VALUE;
                if(weight < previousWeight) {
                    String data = Double.toString(weight);
                    if(weight%1==0 && !data.contains("E"))
                        data = data.substring(0,data.length()-2);
                    rowChange[currentRound] += (uw.vertexA == u ? uw.vertexB.name : 
                                                uw.vertexA.name)+", ";
                    primModel.setValueAt(data+", "+u.name,currentRound,j+1);
                }
                else  {
                    rowUnchange[currentRound] += (uw.vertexA == u ? uw.vertexB.name : 
                                                uw.vertexA.name)+", ";
                    primModel.setValueAt(previousCell,currentRound,j+1);
                }
            }
            else 
                primModel.setValueAt(previousCell != null ? previousCell : '\u221E',currentRound,j+1);
            String currentCell = primModel.getValueAt(currentRound,j+1).toString();
            if(currentCell.length() == 1)
                continue;
            double currentWeight = Double.parseDouble(currentCell.substring(0,currentCell.indexOf(",")));
            if(currentWeight<minWeight) {
                minWeight = currentWeight;
                v = w;
                String adjVertex = currentCell.substring(currentCell.indexOf(",")+2);
                for(Edge e : Edges)
                    if(!T.contains(e) && ((e.vertexA.name.equals(adjVertex) && e.vertexB.name.equals(w.name)) ||
                                          (e.vertexA.name.equals(w.name) && e.vertexB.name.equals(adjVertex)))) {
                        uv = e;
                        String a = e.vertexA.name;
                        String b = e.vertexB.name;
                        if(a.length()!=b.length())
                            weightChange[currentRound] = (a.length()<b.length() ? a+b : b+a) +", ";
                        else
                            for(int i=0;i<a.length();i++)
                                if(a.charAt(i)!=b.charAt(i)) {
                                    weightChange[currentRound] = (a.charAt(i)<b.charAt(i) ? a+b : b+a) +", ";
                                    break;
                                }
                        break;
                    }
            }
            else if(currentWeight == minWeight) {
                String adjVertex = currentCell.substring(currentCell.indexOf(",")+2);
                for(Edge e : Edges)
                    if(!T.contains(e) && ((e.vertexA.name.equals(adjVertex) && e.vertexB.name.equals(w.name)) ||
                                          (e.vertexA.name.equals(w.name) && e.vertexB.name.equals(adjVertex)))) {
                        String a = e.vertexA.name;
                        String b = e.vertexB.name;
                        if(a.length()!=b.length())
                            weightChange[currentRound] += (a.length()<b.length() ? a+b : b+a) +", ";
                        else
                            for(int i=0;i<a.length();i++)
                                if(a.charAt(i)!=b.charAt(i)) {
                                    weightChange[currentRound] += (a.charAt(i)<b.charAt(i) ? a+b : b+a) +", ";
                                    break;
                                }
                        break;
                    }
            }
        }
        if(uv != null) {
            sumWeight += uv.value;
            uv.stroke = 8;
            uv.isSelect = true;
        }
        A.remove(v);
        N.addLast(v);
        T.addLast(uv);
        currentRound++;
        setWeight();
        primModel.fireTableDataChanged();
    }
    public void resetAlgorithm() {
        T.removeAll(T);
        A.removeAll(A);
        N.removeAll(N);
        shortestPath.clear();
        pointPath.clear();
        currentRound = 0;
        sumWeight = 0;
        setWeight();
    }
    public void resetSelect() {
        for(Vertex v : Vertexs)
            v.isSelect = false;
        for(Edge e : Edges) {
            e.isSelect = false;
            e.stroke = 2;
        }
    }
    public void setAlgorithm() {
        for(Vertex vertex : Vertexs)
            A.add(vertex);
        primModel = new PrimTableModel(Vertexs);
        currentRound = 0;
        sumWeight = 0;
        weightChange = new String[Vertexs.size()-1];
        rowUnchange = new String[Vertexs.size()-1];
        rowChange = new String[Vertexs.size()-1];
        setWeight();
    }
    public void setWeight() {
        String data = Double.toString(sumWeight);
        if(sumWeight%1==0 && !data.contains("E"))
            data = data.substring(0,data.length()-2);
        answerLabel.setText("<html><CENTER>น้ำหนักรวมที่น้อยที่สุดของต้นไม้แบบทอดข้ามคือ : "+data+"</CENTER>");
    }
    public void setFrame(int type) {
        vertexLabel.setVisible(false);
        vertexText.setVisible(false);
        edgeLabel.setVisible(false);
        edgeText.setVisible(false);
        answerLabel.setVisible(false);
        display.setDisplay(false);
        dijkstraTableFrame.setVisible(false);
        showingArea.setVisible(type==3);
        algorithmToolbar.setVisible(type==2||type==3);
        tableScroll.setVisible(type==2);
        drawingArea.setVisible(type==1);
        mainToolbar.setVisible(type==1);
        solutionScroll.setVisible(type==2);
    }
    public void setStartVertex() {
        Object[] vertexName = new Object[Vertexs.size()];
        for(int i=0;i<Vertexs.size();i++)
            vertexName[i] = Vertexs.get(i).name;
        startVertex = (String)JOptionPane.showInputDialog(null,"เลือกจุดยอดเริ่มต้น",
                            "หาต้นไม้แบบทอดข้ามที่เล็กที่สุด",-1,null,vertexName,null);
    }
    public void insertTable() {
        JTable prim = new JTable(primModel) {
            DefaultTableCellRenderer renderCenter = new DefaultTableCellRenderer();
            {
                renderCenter.setHorizontalAlignment(SwingConstants.CENTER);
            }
            @Override
            public TableCellRenderer getCellRenderer (int arg0, int arg1) {
                return renderCenter;
            }
        };
        prim.setFont(THSarabunFont);
        prim.setRowHeight(30);
        prim.getTableHeader().setReorderingAllowed(false);
        if(prim.getColumnCount() < 16)
            prim.setAutoscrolls(true);
        else
            prim.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scroll = new JScrollPane(prim);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(0,250,150,150);
        scroll.setBorder(null);
        scroll.setSize(new Dimension(screenSize.width-400,screenSize.height-getHeight()-250));
        tableScroll.setViewportView(scroll);
    }
    public void setSolution() {
        String text;
        if(currentRound == Vertexs.size()-1) {
            text = "<html>รอบที่ "+currentRound+" การมี "+N.get(N.size()-2).name+" เป็นสมาชิกใหม่ใน N ";
            if(rowChange[currentRound-1].length()>0)
                text += "ทำให้มีการปรับค่าน้ำหนักของการเชื่อมของจุดยอด<br>"+rowChange[currentRound-1].substring(0,rowChange[currentRound-1].indexOf(","))+
                        " เป็นน้ำหนัก "+T.getLast().value+" ";
            text += "ทำให้ได้ตารางดังต่อไปนี้<br>จากตารางพบว่าเส้นเชื่อม ";
            String aName = T.getLast().vertexA.name;
            String bName = T.getLast().vertexB.name;
            if(aName.length()!=bName.length())
                text += (aName.length()<bName.length() ? aName+bName : bName+aName);
            else
                for(int i=0;i<aName.length();i++)
                    if(aName.charAt(i)!=bName.charAt(i)) {
                        text += aName.charAt(i)<bName.charAt(i) ? aName+bName : bName+aName;
                        break;
                    }

            text += " เป็นเส้นเชื่อมเดียวที่เหลืออยู่<br>ดังนั้นต้นไม้แบบทอดข้าม T ก็จะเป็น {";
            for(Edge t : T) {
                String a = t.vertexA.name;
                String b = t.vertexB.name;
                if(a.length()!=b.length())
                    text += (a.length()<b.length() ? a+b : b+a)+",";
                else
                    for(int i=0;i<a.length();i++)
                        if(a.charAt(i)!=b.charAt(i)) {
                            text += (a.charAt(i)<b.charAt(i) ? a+b : b+a)+",";
                            break;
                        }
            }
            text = text.substring(0,text.length()-1)+"} ส่วน A ไม่มีสมาชิกจึงเป็นเซตว่าง "+'\u00F8'+"<br>ในขณะที่ N เป็น {";
            for(Vertex n : Vertexs)
                if(!A.contains(n))
                text += n.name+",";
            String data = Double.toString(sumWeight);
            if(sumWeight%1==0 && !data.contains("E"))
                data = data.substring(0,data.length()-2);
            text = text.substring(0,text.length()-1)+"} ซึ่งได้จำนวนเส้นเชื่อมครบแล้ว<br>ดังนั้นน้ำหนักรวมของต้นไม้แบบทอดข้ามเป็น "+data;
        }
        else {
            if(currentRound == 1) {
                text = "<html>รอบที่ "+currentRound+" เริ่มต้นจากจุดยอด "+N.getFirst().name+" กำหนดให้ N เป็น {"+N.getFirst().name+"} ให้ T เป็น "+'\u00F8'+
                       " และ A เป็น {";
                for(int i=0;i<Vertexs.size();i++)
                    if(!Vertexs.get(i).equals(N.getFirst()))
                        text += Vertexs.get(i).name+",";
                text = text.substring(0,text.length()-1)+"}<br>ดังนั้นจึงหาเส้นเชื่อมที่มีจุดปลายใน A " + 
                       "ที่มาตกกระทบกับจุด "+N.getFirst().name+" ซึ่งเก็บได้เป็นตารางต่อไปนี้<br>ในช่องของตาราง" + 
                       "ที่เป็นค่าอนันต์ ("+'\u00F8'+") หมายถึง ไม่มีเส้นเชื่อมระหว่างจุดยอดทั้งสอง สำหรับ" + 
                       "ช่องที่จุดนั้นเป็นสมาชิกใน N จะเป็น - <br>โดยช่องอื่นจะบอกน้ำหนักและจุดยอดใน N " + 
                       "ที่เชื่อมต่อแล้วเกิดน้ำหนักน้อยที่สุด ซึ่งใช้เครื่องหมาย , มาแบ่งแยก2 ส่วนนี้ออกจากกัน<br>" + 
                       "หลังจากนั้นเลือกเส้นเชื่อมที่มีน้ำหนักน้อยที่สุดก็คือ ";
                
                String a = T.getLast().vertexA.name;
                String b = T.getLast().vertexB.name;
                if(a.length()!=b.length())
                    text += a.length()<b.length() ? a+b : b+a;
                else
                    for(int i=0;i<a.length();i++)
                        if(a.charAt(i)!=b.charAt(i)) {
                            text += a.charAt(i)<b.charAt(i) ? a+b : b+a;
                            break;
                        }
                text += " ใส่ลงไปใน T และเพิ่มจุดยอดลงไปใน N เป็น {";
                for(Vertex v : Vertexs) {
                    if(!A.contains(v))
                        text += v.name+",";
                }
                text = text.substring(0,text.length()-1)+"}<br>และ A เหลือ {";
                for(Vertex v : Vertexs) {
                    if(!N.contains(v))
                        text += v.name+",";
                }
                text = text.substring(0,text.length()-1)+"}";
            }
            else {
                text = "<html>รอบที่ "+currentRound+" เมื่อนำจุดยอด "+N.get(N.size()-2).name+" เข้ามาเป็นสมาชิกใหม่ในเซตของจุดยอด N แล้ว พบว่า";
                if(rowChange[currentRound-1].length()==0 && rowUnchange[currentRound-1].length()==0)
                    text += "<br>ไม่มีการปรับค่าน้ำหนักในตาราง เนื่องจากไม่พบเส้นเชื่อมระหว่างเซตของ A กับจุด "+N.get(N.size()-2).name;
                else {    
                    if(rowChange[currentRound-1].length() > 0) {
                        int index=0,indexSub=rowChange[currentRound-1].indexOf(",");
                        while(indexSub!=rowChange[currentRound-1].lastIndexOf(",")) {
                            index = indexSub;
                            indexSub = rowChange[currentRound-1].indexOf(",",indexSub+1);
                        }
                        text += "<br>มีการปรับค่าน้ำหนักใหม่ของการเชื่อมระหว่างเซตของ A และจุด "+N.get(N.size()-2).name+" ที่จุดยอด ";
                        if(rowChange[currentRound-1].indexOf(",") != rowChange[currentRound-1].lastIndexOf(",")) {
                            text += rowChange[currentRound-1].substring(0,index)+
                                    " และ "+rowChange[currentRound-1].substring(index+2);
                            text = text.substring(0,text.length()-2);
                        }
                        else
                            text += rowChange[currentRound-1].substring(0,rowChange[currentRound-1].indexOf(","));
                    }
                    if(rowUnchange[currentRound-1].length() > 0) {
                        int index=0,indexSub=rowUnchange[currentRound-1].indexOf(",");
                        while(indexSub!=rowUnchange[currentRound-1].lastIndexOf(",")) {
                            index = indexSub;
                            indexSub = rowUnchange[currentRound-1].indexOf(",",indexSub+1);
                        }
                        if(rowUnchange[currentRound-1].indexOf(",") != rowUnchange[currentRound-1].lastIndexOf(",")) {
                            text += "<br>"+(rowChange[currentRound-1].length()>0?"ส่วน":"")+"ที่จุดยอด "+
                                    rowUnchange[currentRound-1].substring(0,index)+" และ "+rowUnchange[currentRound-1].substring(index+2);
                            text = text.substring(0,text.length()-2);
                        }
                        else
                            text += "<br>"+(rowChange[currentRound-1].length()>0?"ส่วน":"")+"ที่จุดยอด "+
                                    rowUnchange[currentRound-1].substring(0,rowUnchange[currentRound-1].indexOf(","));
                        text += " การเชื่อมผ่านจุดยอด "+N.get(N.size()-2).name+" ไม่ทำให้น้ำหนักลดลงก็จะไม่ปรับค่า";
                    }
                }
                text += " จะได้ตารางดังต่อไปนี้<br>";
                if(weightChange[currentRound-1].indexOf(",") != weightChange[currentRound-1].lastIndexOf(",")) {
                    int index=0,indexSub=weightChange[currentRound-1].indexOf(",");
                    while(indexSub!=weightChange[currentRound-1].lastIndexOf(",")) {
                        index = indexSub;
                        indexSub = weightChange[currentRound-1].indexOf(",",indexSub+1);
                    }
                    text += "จากนั้นหาเส้นเชื่อมที่มีน้ำหนักน้อยที่สุดต่อไปก็คือ "+weightChange[currentRound-1].substring(0,index) +" หรือ "+
                            weightChange[currentRound-1].substring(index+2);
                    text = text.substring(0,text.length()-2);
                    text += " ในกรณีนี้เลือกเส้นเชื่อม ";
                }
                else
                    text += "จากนั้นเลือกเส้นเชื่อม ";
                String a = T.getLast().vertexA.name;
                String b = T.getLast().vertexB.name;
                if(a.length()!=b.length())
                    text += a.length()<b.length() ? a+b : b+a;
                else
                    for(int i=0;i<a.length();i++)
                        if(a.charAt(i)!=b.charAt(i)) {
                            text += a.charAt(i)<b.charAt(i) ? a+b : b+a;
                            break;
                        }
                if(weightChange[currentRound-1].indexOf(",") != weightChange[currentRound-1].lastIndexOf(","))
                    text += " ก่อน<br>";
                else 
                    text += " เนื่องจากพบว่าเป็นเส้นเชื่อมที่มีค่าน้ำหนักน้อยที่สุด<br>";
                text += "ดังนั้นต้นไม้แบบทอดข้าม T ก็จะเป็น {";
                for(Edge t : T) {
                    String c = t.vertexA.name;
                    String d = t.vertexB.name;
                    if(c.length()!=d.length())
                        text += (c.length()<d.length() ? c+d : d+c)+",";
                    else
                        for(int i=0;i<c.length();i++)
                            if(c.charAt(i)!=d.charAt(i)) {
                                text += (c.charAt(i)<d.charAt(i) ? c+d : d+c)+",";
                                break;
                            }
                }
                text = text.substring(0,text.length()-1)+"} ส่วนสมาชิกของ A เป็น {";
                for(Vertex v : Vertexs) {
                    if(!N.contains(v))
                        text += v.name+",";
                }
                text = text.substring(0,text.length()-1)+"}<br>ในขณะที่เซตของ N เป็น {";
                for(Vertex n : Vertexs)
                    if(!A.contains(n))
                        text += n.name+",";
                text = text.substring(0,text.length()-1)+"}";
            }
        }
        solutionLabel.setText(text);
    }
    public void searchButtAction(ActionEvent e) {
        Object[] algorithm = {"อัลกอริทึมของพริม","อัลกอริทึมของครูสกาวล์","อัลกอริทึมของไดก์สตรา"};
        String search = (String)JOptionPane.showInputDialog(null,"เลือกอัลกอริทึมที่ต้องการหา",
                            "อัลกอริทึม",JOptionPane.PLAIN_MESSAGE,null,algorithm,null);
        display.setDisplay(false);
        if(search == null)
            return;
        if(search.contains("อัลกอริทึมของพริม"))
            spanningTreeButtAction(e);
        else if(search.contains("อัลกอริทึมของไดก์สตรา"))
            shortestPathButtAction(e);
    }
    public void setSourceVertex() {
        Object[] name = new Object[Vertexs.size()];
        for(int i=0;i<Vertexs.size();i++)
            name[i] = Vertexs.get(i).name;
        String source = (String)JOptionPane.showInputDialog(null,"เลือกจุดยอดเริ่มต้น",
                            "หาเส้นทางที่สั้นที่สุด",-1,null,name,null);
//        System.out.println(source);
        if(source != null) {
            for(Vertex v : Vertexs)
                if(v.name.equals(source)) {
                    sourceVertex = v;
                    break;
                }
        }
        else
            sourceVertex = null;
    }
    public void setTargetVertex() {
        if(sourceVertex==null)
            return;
        Object[] name = new Object[Vertexs.size()-1];
        int count=0;
        for(Vertex v : Vertexs)
            if(!v.name.equals(sourceVertex.name))
                name[count++] = v.name;
        String target = (String)JOptionPane.showInputDialog(null,"เลือกจุดยอดสิ้นสุด",
                            "หาเส้นทางที่สั้นที่สุด",-1,null,name,null);
        if(target != null) {
            for(Vertex v : Vertexs)
                if(v.name.equals(target) && v!=sourceVertex) {
                    targetVertex = v;
                    break;
                }
        }
        else 
            targetVertex = null;
    }
    public void setShortestPath() {
        double weight = 0;
        String path = "";
        for(Vertex v : pointPath.get(currentRound-1))
            path += v.name+", ";
        path = path.substring(0,path.length()-2);
        for(Edge e : shortestPath.get(currentRound-1))
            weight += e.value;
        String data = Double.toString(weight);
        if(weight%1==0 && !data.contains("E"))
            data = data.substring(0,data.length()-2);
        answerLabel.setText("<html><CENTER>เส้นทางคือ : "+path+"   ระยะทาง : "+data+"</CENTER>");
        answerLabel.setVisible(currentRound==shortestPath.size());
    }
    public void setPath(int round) {
        resetSelect();
        for(Edge ed : shortestPath.get(round)) {
            ed.isSelect = true;
            ed.stroke = 5;
        }
        draw(2);
    }
    public void dijkstraTable(int round) {
        JTable table = new JTable(dijkstraTable.get(round)) {
            DefaultTableCellRenderer renderCenter = new DefaultTableCellRenderer();
            {
                renderCenter.setHorizontalAlignment(SwingConstants.CENTER);
            }
            @Override
            public TableCellRenderer getCellRenderer (int arg0, int arg1) {
                return renderCenter;
            }
        };
        table.setFont(THSarabunFont);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoscrolls(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(0,250,150,150);
        scroll.setBorder(null);
        if(dijkstraTableFrame.isVisible())
            dijkstraTableFrame.setVisible(false);
        dijkstraTableFrame = new JFrame("ตาราง");
        dijkstraTableFrame.add(scroll);
        dijkstraTableFrame.pack();
        dijkstraTableFrame.setLocation(screenSize.width-dijkstraTableFrame.getWidth()-100,
                                       screenSize.height-dijkstraTableFrame.getHeight()-100);
        dijkstraTableFrame.setVisible(true);
        dijkstraTableFrame.setAlwaysOnTop(true);
    }
    public void shortestPathButtAction(ActionEvent e) {
        display.setDisplay(false);
        setSourceVertex();
        setTargetVertex();
        searchButt.setContentAreaFilled(false);
        if(sourceVertex == null || targetVertex == null)
            return;
        startVertex = null;
        resetSelect();
        setFrame(3);
        resetAlgorithm();
        for(Vertex v : Vertexs)
            A.add(v);
        dijkstraTable = new ArrayList<>();
        dijkstraModel = new DijkstraTableModel(Vertexs);
        dijkstraAlgorithm();
        currentRound = 1;
        setPath(currentRound-1);
        setShortestPath();
        setComboBox(2);
        dijkstraTable(currentRound-1);
        draw(2);
    }
    public void dijkstraAlgorithm() {
        Vertex v = sourceVertex;
        dijkstraModel.setValueAt(0,Vertexs.indexOf(v),1);
        while(v!=targetVertex) {
            A.remove(v);
            ArrayList<Edge> impact = new ArrayList<>();
            double min = Double.MAX_VALUE;
            int index = 0;
            for(Vertex u : A) 
                for(Edge e : Edges) 
                    if((e.vertexA==u&&e.vertexB==v) || (e.vertexA==v&&e.vertexB==u)) 
                        impact.add(e);
            for(Edge e : impact) {
                Vertex u = e.vertexA==v ? e.vertexB : e.vertexA;
                String valueV = dijkstraModel.getValueAt(Vertexs.indexOf(v), 1).toString();
                int indexU = Vertexs.indexOf(u);
                String valueU = dijkstraModel.getValueAt(indexU, 1).toString();
                double distV = Double.parseDouble(valueV);
                double weight = e.value;
                double distU = valueU.equals('\u221E'+"") ? Double.MAX_VALUE : Double.parseDouble(valueU);
                if(distV+weight < distU) {
                    distU = distV + weight;
                    String data = Double.toString(distU);
                    if(distU%1==0 && !data.contains("E"))
                        data = data.substring(0,data.length()-2);
                    dijkstraModel.setValueAt(data,indexU,1);
                    dijkstraModel.setValueAt(v.name,indexU,2);
                }
            }
            for(int i=0;i<dijkstraModel.getRowCount();i++) 
                if(A.contains(Vertexs.get(i))) {
                    String value = dijkstraModel.getValueAt(i, 1).toString();
                    double weight = value.equals('\u221E'+"") ? Double.MAX_VALUE : Double.parseDouble(value);
                    if(weight<min) {
                        min = weight;
                        index = i;
                    }
                }
            v = Vertexs.get(index);
            setInsertTable();
            Stack<Vertex> pointP = new Stack<>();
            ArrayList<Vertex> pointMiddle = new ArrayList<>();
            ArrayList<Edge> path = new ArrayList<>();
            Vertex point = v;
            pointP.push(point);
            while(point!=sourceVertex) {
                String prev = dijkstraModel.getValueAt(Vertexs.indexOf(point),2).toString();
                Vertex prevPoint = null;
                for(Vertex s : Vertexs)
                    if(s.name.equals(prev)) {
                        prevPoint = s;
                        break;
                    }
                for(Edge e : Edges) 
                    if((e.vertexA==point&&e.vertexB==prevPoint) || (e.vertexA==prevPoint&&e.vertexB==point)) {
                        path.add(e);
                        break;
                    }
                point = prevPoint;
                pointP.push(point);
            }
            while(!pointP.isEmpty())
                pointMiddle.add(pointP.pop());
            pointPath.add(pointMiddle);
            shortestPath.add(path);
        }
    }
    public void setInsertTable() {
        DijkstraTableModel dtm = new DijkstraTableModel(Vertexs);
        for(int i=0;i<Vertexs.size();i++) {
            dtm.setValueAt(dijkstraModel.getValueAt(i,1),i,1);
            dtm.setValueAt(dijkstraModel.getValueAt(i,2),i,2);
        }
        dijkstraTable.add(dtm);
    }
    public void setComboBox(int type) {
        int size = type==1 ? Vertexs.size()-1 : shortestPath.size();
        String roundNumber[] = new String[size];
        for(int i=0;i<size;i++)
            roundNumber[i] = "รอบที่ "+(i+1);
        backButt.setEnabled(false);
        nextButt.setEnabled(currentRound!=size-1);
        roundTab = new JComboBox(roundNumber);
        roundTab.setFont(THSarabunFont);
        roundTab.setSelectedIndex(0);
        roundTab.setBounds(170, 2, 150, 55);
        roundTab.setFocusable(false);
        ((JLabel)roundTab.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        roundTab.addActionListener(this::tabBoxAction);
        roundTabScroll.setViewportView(roundTab);
    }
    public void spanningTreeButtAction(ActionEvent e) {
        display.setDisplay(false);
        setStartVertex();
        searchButt.setContentAreaFilled(false);
        if(startVertex==null)
            return;
        sourceVertex = null;
        targetVertex = null;
        resetSelect();
        setFrame(2);
        setAlgorithm();
        insertTable();
        nextStepPrim();
        setWeight();
        answerLabel.setVisible(showingArea.isVisible() && currentRound==Vertexs.size()-1);
        setSolution();
        setComboBox(1);
        nextButt.setEnabled(currentRound!=Vertexs.size()-1);
    }
    public void tabBoxAction(ActionEvent e) {
        if(startVertex!=null) {
            if(currentRound<roundTab.getSelectedIndex()+1)
                while(currentRound!=roundTab.getSelectedIndex()+1)
                    nextStepPrim();
            else
                while(currentRound!=roundTab.getSelectedIndex()+1)
                    previousStepPrim();
            setSolution();
            tableButtAction(e);
            setWeight();
            swapButt.setIcon(new ImageIcon("Icon/Table.png"));
            answerLabel.setVisible(false);
            nextButt.setEnabled(currentRound!=Vertexs.size()-1);
        }
        else {
            currentRound = roundTab.getSelectedIndex()+1;
            setPath(currentRound-1);
            nextButt.setEnabled(currentRound!=shortestPath.size());
            setShortestPath();
            dijkstraTable(currentRound-1);
        }
        backButt.setEnabled(currentRound!=1);
        draw(2);
    }
    public void swapButtAction(ActionEvent e) {
        if(startVertex!=null) {
            if(!showingArea.isVisible())
                graphButtAction(e);
            else
                tableButtAction(e);
            setWeight();
            answerLabel.setVisible(showingArea.isVisible() && currentRound==Vertexs.size()-1);
            swapButt.setIcon(new ImageIcon(!showingArea.isVisible() ? "Icon/Table.png" : "Icon/Graph.png"));
        }
        else 
            dijkstraTable(currentRound-1);
    }
    public void backButtAction(ActionEvent e) {
        nextButt.setEnabled(true);
        if(startVertex!=null) {
            previousStepPrim();
            setSolution();
            setWeight();
            tableButtAction(e);
            swapButt.setIcon(new ImageIcon("Icon/Table.png"));
            answerLabel.setVisible(false);
            roundTab.setSelectedIndex(currentRound-1);
        }
        else {
            currentRound-=2;
            setPath(currentRound);
            roundTab.setSelectedIndex(currentRound);
            setShortestPath();
            dijkstraTable(currentRound-1);
        }
        backButt.setEnabled(currentRound!=1);
    }
    public void nextButtAction(ActionEvent e) {
        backButt.setEnabled(true);
        if(startVertex!=null) {
            nextStepPrim();
            setSolution();
            setWeight();
            tableButtAction(e);
            swapButt.setIcon(new ImageIcon("Icon/Table.png"));
            answerLabel.setVisible(false);
            roundTab.setSelectedIndex(currentRound-1);
            nextButt.setEnabled(currentRound!=Vertexs.size()-1);
        }
        else {
            setPath(currentRound);
            roundTab.setSelectedIndex(currentRound);
            nextButt.setEnabled(currentRound!=shortestPath.size());
            setShortestPath();
            dijkstraTable(currentRound-1);
        }
    }
    public void graphButtAction(ActionEvent e) {
        swapButt.setVisible(false);
        swapButt.setVisible(true);
        showingArea.setVisible(true);
        tableScroll.setVisible(false);
        solutionScroll.setVisible(false);
        draw(2);
    }
    public void tableButtAction(ActionEvent ae) {
        Graphics2D g = (Graphics2D)showingArea.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        tableScroll.setVisible(true);
        solutionScroll.setVisible(true);
        showingArea.setVisible(false);
    }
    public void homeButtAction(ActionEvent e) {
        display.setDisplay(false);
        homeButt.setContentAreaFilled(false);
        resetSelect();
        resetAlgorithm();
        setFrame(1);
        swapButt.setIcon(new ImageIcon("Icon/Table.png"));
        roundTab = null;
    }
    public void helpButtAction(ActionEvent e) {
        display.setDisplay(false);
        String help = "<html>";
        help += "ดับเบิลคลิก(ซ้าย)บนพื้นหลังสีขาวเพื่อสร้างจุดยอด<br>";
        help += "กด spacebar บนแป้นพิมพ์ค้างพร้อมกับลากเมาส์แล้วปล่อยเพื่อสร้างเส้นเชื่อม<br>";
        help += "หลังจากคลิกซ้ายที่จุดยอดหรือค่าเส้นเชื่อม จุดยอดหรือเส้นเชื่อมนั้นจะกลายเป็นสีน้ำเงิน<br>";
        help += "คลิกหนึ่งครั้งที่พื้นที่ว่างเพื่อยกเลิกการเลือกจุดยอดหรือเส้นเชื่อมที่เป็นสีน้ำเงิน<br>";
        help += "เมื่อจุดยอดหรือเส้นเชื่อมเป็นสีน้ำเงิน จะสามารถเปลี่ยนชื่อ/ค่าน้ำหนัก, ลบ หรือเคลื่อนย้ายตำแหน่งได้<br>";
        help += "คลิกซ้ายที่จุดยอดแล้วพิมพ์ตัวอักษรในช่องสี่เหลี่ยมด้านขวาบนเพื่อเปลี่ยนชื่อของจุดยอด<br>";
        help += "คลิกซ้ายที่ค่าน้ำหนักของเส้นเชื่อมแล้วพิมพ์ตัวเลขในช่องสี่เหลี่ยมด้านขวาบนเพื่อเปลี่ยนค่าน้ำหนักของเส้นเชื่อม<br>";
        help += "คลิกซ้ายที่จุดยอดหรือเส้นเชื่อมแล้วกด delete บนแป้นพิมพ์เพื่อลบจุดยอดหรือเส้นเชื่อม<br>";
        help += "คลิกซ้ายที่จุดยอดหรือเส้นเชื่อมแล้วลากเมาส์เพื่อเคลื่อนย้ายตำแหน่งของจุดยอดหรือเส้นเชื่อม<br>";
        help += "คลิกขวาที่ชื่อจุดยอดหรือค่าน้ำหนักของเส้นเชื่อมแล้วลากเมาส์เพื่อเคลื่อนย้ายตำแหน่งชื่อจุดยอดหรือค่าน้ำหนักของเส้นเชื่อม<br>";
        help += "คลิกลูกกลิ้งของเมาส์ค้างเพื่อดูรายละเอียดของจุดยอดหรือเส้นเชื่อมที่เลือก";
        help += "กดปุ่ม บันทึก บนหน้าจอโปรแกรม เพื่อบันทึกกราฟบน canvas ลงไฟล์ json<br>";
        help += "กดปุ่ม เปิด บนหน้าจอโปรแกรม เพื่อเปิดนำเข้ากราฟลงบน canvas จากไฟล์ json<br>";
        help += "กดปุ่ม เลิกทำ บนหน้าจอโปรแกรม เพื่อยกเลิกคำสั่งที่ทำไปแล้ว<br>";
        help += "กดปุ่ม ทำซ้ำ บนหน้าจอโปรแกรม เพื่อทำซ้ำคำสั่งที่เพิ่งทำไป<br>";
        help += "กดปุ่ม ล้าง บนหน้าจอโปรแกรม เพื่อลบข้อมูลของกราฟทั้งหมด<br>";
        help += "กดปุ่ม ค้นหา บนหน้าจอโปรแกรม เพื่อใช้อัลกอริทึมเพื่อค้นหาสิ่งที่สนใจ<br>";
        helpLabel.setText(help);
        boxHelp.add(helpLabel);
        boxHelp.setAutoscrolls(true);
        frameHelp.pack();
        frameHelp.setBounds(screenSize.width/2-frameHelp.getWidth()/2,screenSize.height/2-frameHelp.getHeight()/2 -
                            Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration()).bottom/2,
                            frameHelp.getWidth(), frameHelp.getHeight());
        frameHelp.setVisible(true);
        helpButt.setContentAreaFilled(false);
    }
    public void saveButtAction(ActionEvent e) throws IOException {
        display.setDisplay(false);
        resetSelect();
        FileDialog fd = new FileDialog(this, "Save File", FileDialog.SAVE);
        fd.setFile("*.json");
        fd.setVisible(true);
        if(fd.getFile()!=null) {
            String filepath = fd.getFile();
            if(filepath.contains(".") && !filepath.contains(".json"))
                filepath = filepath.substring(0,filepath.indexOf("."));
            save(fd.getDirectory()+filepath + (filepath.contains(".json") ? "" : ".json"));
        }
        saveButt.setContentAreaFilled(false);
        clearButt.setEnabled(true);
    }
    public void openButtAction(ActionEvent e) throws IOException {
        display.setDisplay(false);
        resetSelect();
        FileDialog fd = new FileDialog(this, "Open File", FileDialog.LOAD);
        fd.setFile("*.json");
        fd.setVisible(true);
        if(fd.getFile()!=null) {
            open(fd.getDirectory()+fd.getFile());
            draw(1);
            setGraphType();
            searchButt.setEnabled(checkCondition());
            actionScript();
        }
        openButt.setContentAreaFilled(false);
        clearButt.setEnabled(true);
    }
    public void clearButtAction(ActionEvent e) {
        clear();
        clearButt.setEnabled(false);
        actionScript();
        
        undo.push(null);
        listUndo.push(null);
    }
    public void save(String path) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try (FileWriter writer = new FileWriter(path)) {
            Backup backup = new Backup();
            writer.write(gson.toJson(backup));
            writer.close();
            filePath = path;
            setTitle(path.substring(path.lastIndexOf("\\")+1,path.lastIndexOf(".json"))+" - Graph Drawing");
        }
    }
    public void open(String path) throws FileNotFoundException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        Backup backup = gson.fromJson(bufferedReader, Backup.class);
        Vertexs = backup.VertexsBackup;
        Edges = backup.Edge_sBackup;
        Vertex.idGen = backup.VertexsBackup.size()+1;
        for (Edge e : Edges) {
            if (e.vertexA != null) {
                int id = e.vertexA.id;
                for (Vertex v : Vertexs) {
                    if (v.id == id) {
                        e.vertexA = v;
                        break;
                    }
                }
            }
            if (e.vertexB != null) {
                int id = e.vertexB.id;
                for (Vertex v : Vertexs) {
                    if (v.id == id) {
                        e.vertexB = v;
                        break;
                    }
                }
            }
        }
        vertexText.setVisible(false);
        edgeText.setVisible(false);
        vertexLabel.setVisible(false);
        edgeLabel.setVisible(false);
        filePath = path;
        setTitle(path.substring(path.lastIndexOf("\\")+1,path.lastIndexOf(".json"))+" - Graph Drawing");
    }
    public void clear() {
        Graphics2D g = (Graphics2D)drawingArea.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        Vertexs.removeAll(Vertexs);
        Edges.removeAll(Edges);
        Vertex.idGen = 0;
        Edge.amount = 0;
        graphTypeLabel.setText("");
        swapButt.setIcon(new ImageIcon("Icon/Table.png"));
        searchButt.setEnabled(false);
    }
    public void selected(int x, int y) {
        Object obj = null;
        for (Vertex s : Vertexs)
            if (s.inCircle(x, y) || s.aroundName(x, y)) {
                s.isSelect = true;
                obj = s;
                break;
            }
        if (obj == null) 
            for (Edge t : Edges) {
                if (t.aroundWeight(x, y)) {
                    t.isSelect = true;
                    obj = t;
                    break;
                }
            }
        if (obj == null) {
            if (selected != null) {
                if (selected instanceof Vertex) {
                    Vertex s = (Vertex) selected;
                    s.isSelect = false;
                } 
                else {
                    Edge t = (Edge) selected;
                    t.isSelect = false;
                }
                selected = null;
            }
        }
        else {
            if (selected == null)
                selected = obj;
            else {
                if (obj != selected) {
                    if (selected instanceof Vertex) {
                        Vertex s = (Vertex) selected;
                        s.isSelect = false;
                    } 
                    else {
                        Edge t = (Edge) selected;
                        t.isSelect = false;
                    }
                    selected = obj;
                }
            }
        }
    }
    public void draw(int type) {
        Graphics2D g =  type == 1 ? (Graphics2D)drawingArea.getGraphics() : (Graphics2D)showingArea.getGraphics();
        g.setFont(THSarabunFont);
        if(grid==null)
            grid = type == 1 ? (BufferedImage)createImage(drawingArea.getWidth(),drawingArea.getHeight()) : 
                               (BufferedImage)createImage(showingArea.getWidth(),showingArea.getHeight()) ;
        Graphics2D g2 = grid.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, getWidth(), getHeight());
        for (Edge t : Edges)
            t.draw(g2);
        if (TempEdge != null)
            TempEdge.line(g2);
        for (Vertex s : Vertexs)
            s.draw(g2);
        g.drawImage(grid, null, 0, 0);
    }
    @Override
    public void keyTyped(KeyEvent ke) {
        int key = (int) ke.getKeyChar();
        //Ctrl + H = 8 
//        if(key == 1) {
//            for(Vertex v : Vertexs)
//                v.isSelect = true;
//            for(Edge e : Edges)
//                e.isSelect = true;
//        }
//        else 
        if (key == 3 && clearButt.isEnabled())
            clearButtAction(null);
        else if (key == 9)
            helpButtAction(null);
        else if (key == 16 && searchButt.isEnabled())
            searchButtAction(null);
        else if (key == 19) {
            try {
                if(filePath.length()>0)
                    save(filePath);
                else
                    saveButtAction(null);
            } 
            catch (IOException ex) {}
        } 
        else if (key == 15) {
            try {
                openButtAction(null);
            } 
            catch (IOException ex) {}
        }
        else if(key == 25 && redoButt.isEnabled()) 
            redoButtAction(null);
        else if(key == 26 && undoButt.isEnabled())
            undoButtAction(null);
        if (selected instanceof Vertex) {
            Vertex s = (Vertex) selected;
            if (key==127) {
                ArrayList<Edge> tempEdge = new ArrayList<>();
                for (Edge t : Edges)
                    if (t.vertexA == selected || t.vertexB == selected) 
                        tempEdge.add(t);
                for (Edge t : tempEdge) {
                    t.vertexA.degree--;
                    t.vertexB.degree--;
                    Edges.remove(t);
                }
                Vertex a = (Vertex)selected;
                if(Vertex.idGen == a.id && Vertexs.size()>1)
                    Vertex.idGen = Vertexs.get(Vertexs.size()-2).id;
                Vertexs.remove((Vertex)selected);
                selected = null;
                vertexText.setVisible(false);
                vertexLabel.setVisible(false);
            } 
        } 
        else if (selected instanceof Edge) {
            Edge t = (Edge) selected;
            if (key==127) {
                t.vertexA.degree--;
                t.vertexB.degree--;
                Edges.remove((Edge)selected);
                selected = null;
                edgeText.setVisible(false);
                edgeLabel.setVisible(false);
            } 
        }
        draw(1);
        setGraphType();
        searchButt.setEnabled(checkCondition());
    }
    @Override
    public void keyPressed(KeyEvent ke) {
        if ((int) ke.getKeyChar() == 32)
            mode = "Edge";
    }
    @Override
    public void keyReleased(KeyEvent ke) {
        mode = "Vertex";
    }
    @Override
    public void mouseMoved(MouseEvent me) {
    }
    public boolean duplicate() {
        LinkedList<Vertex> ver = new LinkedList<>();
        LinkedList<Edge> edg = new LinkedList<>();
        for(Vertex v : Vertexs) 
            ver.addLast(v);
        for(Edge ed : Edges)
            edg.addLast(ed);
        boolean dup = false;
        if(listUndo.peek() != null &&edg.size()+ver.size()==listUndo.peek().size())
            for(Object obj : listUndo.peek()) {
                if((obj instanceof Vertex && ( 
                  ((Vertex)obj).degree!=ver.getFirst().degree || !((Vertex)obj).name.equals(ver.getFirst().name) || 
                  ((Vertex)obj).x!=ver.getFirst().x || ((Vertex)obj).y!=ver.getFirst().y ||
                  ((Vertex)obj).x_name!=ver.getFirst().x_name || ((Vertex)obj).y_name!=ver.getFirst().y_name)) ||
                  (obj instanceof Edge && (!((Edge)obj).weight.equals(edg.getFirst().weight) ||
                  ((Edge)obj).x_center!=edg.getFirst().x_center || ((Edge)obj).y_center!=edg.getFirst().y_center || 
                  ((Edge)obj).x_weight!=edg.getFirst().x_weight || ((Edge)obj).y_weight!=edg.getFirst().y_weight))) {
                    return false;
                }
                else if(obj instanceof Vertex) {
                    dup = true;
                    ver.removeFirst();
                }
                else {
                    dup = true;
                    edg.removeFirst();
                }
            }
        return dup;
    }
    public void actionScript() {
        boolean repeat = true;
        if(!listUndo.isEmpty())
            repeat = duplicate();
        if((!repeat||listUndo.isEmpty())&&(!Vertexs.isEmpty() || !Edges.isEmpty())) {
            ArrayList<Object> listAction = new ArrayList<>();
            for(Vertex v : Vertexs) {
                Vertex ver = new Vertex(v.x,v.y);
                Vertex.idGen--;
                ver.degree = v.degree;
                ver.name = v.name;
                ver.x_name = v.x_name;
                ver.y_name = v.y_name;
                listAction.add(ver);
            }
            for(Edge ed : Edges) {
                Edge edge = new Edge(ed.vertexA,ed.vertexB);
                Edge.amount--;
                edge.weight = ed.weight;
                edge.x_center = ed.x_center;
                edge.y_center = ed.y_center;
                edge.x_weight = ed.x_weight;
                edge.y_weight = ed.y_weight;
                listAction.add(edge);
            }
            ArrayList<Object> action = new ArrayList<>();
            for(Vertex v : Vertexs)
                action.add(v);
            for(Edge ed : Edges) 
                action.add(ed);
            undo.push(action);
            listUndo.push(listAction);
        }
        undoButt.setEnabled(!Vertexs.isEmpty() || !Edges.isEmpty() || !undo.isEmpty());
        clearButt.setEnabled(!Vertexs.isEmpty() || !Edges.isEmpty());
        redoButt.setEnabled(!redo.isEmpty());
        setGraphType();
    }
//    public void autoInsertRandom(int x, int y) {
//        boolean area = true;
//        Random rand = new Random();
//        int count = 0;
//        while(area) {
//            int weight = rand.nextInt(100)+0;
//            Vertex TempVertex = new Vertex(x, y);
//            Vertexs.add(TempVertex);
//            if(Vertexs.size()>1) {
//                Vertex s = Vertexs.get(rand.nextInt(Vertexs.size()-1)+0);
//                Edge edge = new Edge(TempVertex, s);
//                edge.x_center = (TempVertex.x + s.x)/2;
//                edge.y_center = (TempVertex.y + s.y)/2;
//                edge.x_weight = (TempVertex.x + s.x)/2;
//                edge.y_weight = (TempVertex.y + s.y)/2;
//                TempVertex.degree++;
//                s.degree++;
//                edge.weight = ""+weight;
//                edge.value = weight;
//                Edges.add(edge);
//            }
//            draw(1);
//            count++;
//            if(count == 100)
//                area = false;
//            x = rand.nextInt(screenSize.width-10)+20;
//            y = rand.nextInt(screenSize.height-170)+70;
//            actionScript();
//        }
//    }
//
//    public void insert(Vertex TempVertex) {
//        int count = 1;
//        if(Vertexs.size()>1) {
//            Vertex s = Vertexs.get(Vertexs.size()-2);
//            for (Vertex s : Vertexs) {
//                if(s==TempVertex)
//                    break;
//                Edge edge = new Edge(TempVertex, s);
//                edge.x_center = (TempVertex.x + s.x)/2;
//                edge.y_center = (TempVertex.y + s.y)/2;
//                edge.x_weight = (TempVertex.x + s.x)/2;
//                edge.y_weight = (TempVertex.y + s.y)/2;
//                TempVertex.degree++;
//                s.degree++;
//                edge.weight = ""+count;
//                edge.value = count++;
//                Edges.add(edge);
//            }
//        }
//    }
//    public void autoInsert(int x, int y) {
//        boolean area = true, swap = true;
//        while(area) {
//            Vertex TempVertex = new Vertex(x, y);
//            ArrayList<Vertex> temp = new ArrayList<>();
//            String name = "v1";
//            boolean found=false;
//            Vertexs.add(TempVertex);
//            insert(TempVertex);
//            // 70<y<screenSize.height-100
//            // 12<x<screenSize.width-30
//            if(swap)
//                x += 40;
//            else
//                x -= 40;
//            if(x > screenSize.width-30 || x < 20) {
//                swap = !swap;
//                if(swap)
//                    x+=40;
//                else
//                    x -= 40;
//                y += 40;
//            }
//            if(y>screenSize.height-80)
//                area = false;
//            actionScript();
//            draw(1);
//        }
//    }
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if(SwingUtilities.isLeftMouseButton(e))
            selected(x, y);
        else if(SwingUtilities.isRightMouseButton(e) && !SwingUtilities.isMiddleMouseButton(e)) {
            if (selected != null) {
                if (selected instanceof Vertex) {
                    Vertex s = (Vertex) selected;
                    s.isSelect = false;
                } 
                else {
                    Edge t = (Edge) selected;
                    t.isSelect = false;
                }
                selected = null;
            }
        }
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();
            if(x<12||y<70||x>screenSize.width-30||y>screenSize.height-Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration()).bottom*2)
                return;
            if (selected == null || (selected instanceof Vertex && !Vertexs.contains((Vertex)selected))) {
                Vertex TempVertex = new Vertex(x, y);
                ArrayList<Vertex> temp = new ArrayList<>();
                String name = "v1";
                boolean found = false;
                if(!Vertexs.isEmpty()) {
                    for(Vertex v : Vertexs)
                        temp.add(v);
                    for(int i=0;i<Vertexs.size();i++) {
                        boolean checkRepeat = false;
                        for(Vertex v : temp) {
                            if(v.name.equals("v"+(i+1))) {
                                temp.remove(v);
                                checkRepeat = true;
                                break;
                            }
                        }
                        if(!checkRepeat) {
                            found=true;
                            name = "v"+(i+1);
                            break;
                        }
                    }
                    TempVertex.name = found==true ? name : "v"+(Vertexs.size()+1);
                }
                else
                    TempVertex.name = name;
                Vertexs.add(TempVertex);
//                autoInsertRandom(x,y);
//                autoInsert(x,y);
//                insert(TempVertex);
            } 
        }
        if(selected!=null && selected instanceof Vertex) {
            Vertex v = (Vertex)selected;
            vertexText.setText(v.name);
            vertexText.setEditable(false);
            vertexText.setEditable(true);
            vertexText.setHorizontalAlignment(JTextField.CENTER);
            vertexText.requestFocusInWindow();
            vertexText.setVisible(false);
            vertexText.setVisible(true);
            vertexLabel.setVisible(true);
            edgeText.setVisible(false);
            edgeLabel.setVisible(false);
        }
        else if(selected!=null && selected instanceof Edge) {
            Edge ed = (Edge)selected;
            edgeText.setText(ed.weight);
            edgeText.setEditable(false);
            edgeText.setEditable(true);
            edgeText.setHorizontalAlignment(JTextField.CENTER);
            edgeText.requestFocusInWindow();
            edgeText.setVisible(false);
            edgeText.setVisible(true);
            edgeLabel.setVisible(true);
            vertexLabel.setVisible(false);
            vertexText.setVisible(false);
        }
        else {
            vertexText.setVisible(false);
            edgeText.setVisible(false);
            vertexLabel.setVisible(false);
            edgeLabel.setVisible(false);
        }
        draw(1);
        setGraphType();
        searchButt.setEnabled(checkCondition());
        int before = undo.size();
        actionScript();
        if(undo.size()!=before)
            redo.clear();
        redoButt.setEnabled(!redo.isEmpty());
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if(x<12||y<70||x>screenSize.width-30||y>screenSize.height-Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration()).bottom*2)
            return;
        if (mode.equals("Vertex")) {
            if (selected != null) {
                if (selected instanceof Vertex) {
                    Vertex s = (Vertex) selected;
                    if(SwingUtilities.isLeftMouseButton(e) && inSelect) {
                        for (Edge t : Edges) {
                            if (t.vertexA == s || t.vertexB == s) {
                                if (t.vertexA != t.vertexB) {
                                    if (t.vertexA != null) {
                                        t.x_center = (t.vertexA.x + t.vertexB.x)/2;
                                        t.y_center = (t.vertexA.y + t.vertexB.y)/2;
                                        t.x_weight = t.x_center;
                                        t.y_weight = t.y_center;
                                    } 
                                }
                                else {
                                    t.x_center += (x-s.x);
                                    t.y_center += (y-s.y);
                                    t.x_weight = t.x_center;
                                    t.y_weight = t.y_center;
                                }
                            }
                        }
                        s.x_name += (x-s.x);
                        s.y_name += (y-s.y);
                        s.x = x;
                        s.y = y;
                    }
                    else if(SwingUtilities.isRightMouseButton(e) && inSelect && s.aroundCircle(x, y)) {
                        s.x_name = x;
                        s.y_name = y+10;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                } 
                else {
                    Edge t = (Edge) selected;
                    if(SwingUtilities.isLeftMouseButton(e) && inSelect) {
                        t.x_center = x;
                        t.y_center = y;
                        t.x_weight = x;
                        t.y_weight = y;
                    }
                    else if(SwingUtilities.isRightMouseButton(e) && inSelect && t.aroundLine(x, y)) {
                        t.x_weight = x;
                        t.y_weight = y;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                }
            }
        } 
        else if (mode.equals("Edge") && SwingUtilities.isLeftMouseButton(e)) {
            try {
                Vertex Vertex = null;
                for (Vertex s : Vertexs)
                    if (s.inCircle(x, y))
                        Vertex = s;
                if (Vertex != null) {
                    if (Vertex != TempEdge.vertexA) {
                        double angle = Math.atan2(TempEdge.vertexA.y - Vertex.y, TempEdge.vertexA.x - Vertex.x);
                        double dx = Math.cos(angle);
                        double dy = Math.sin(angle);
                        TempEdge.x1 = Vertex.x + (int) (Vertex.r * dx);
                        TempEdge.y1 = Vertex.y + (int) (Vertex.r * dy);
                    } 
                    else {
                        TempEdge.x1 = x;
                        TempEdge.y1 = y;
                    }
                } 
                else {
                    TempEdge.x1 = x;
                    TempEdge.y1 = y;
                }
            } 
            catch (Exception ex) {}
        }
        draw(1);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (mode.equals("Vertex"))
            TempEdge = null;
        else if (mode.equals("Edge")) {
            try {
                TempEdge.x1 = x;
                TempEdge.y1 = y;
                Vertex vertexB = null;
                for (Vertex s : Vertexs) {
                    if (s.inCircle(x, y)) {
                        TempEdge.x1 = s.x;
                        TempEdge.y1 = s.y;
                        vertexB = s;
                        Edge edge = new Edge(TempEdge.vertexA, vertexB);
                        if(s!=TempEdge.vertexA){
                            edge.x_center = (TempEdge.vertexA.x + vertexB.x)/2;
                            edge.y_center = (TempEdge.vertexA.y + vertexB.y)/2;
                            edge.x_weight = (TempEdge.vertexA.x + vertexB.x)/2;
                            edge.y_weight = (TempEdge.vertexA.y + vertexB.y)/2;
                        }
                        else{
                            double angle = Math.atan2(y - TempEdge.vertexA.y, x - TempEdge.vertexA.x);
                            double dx = Math.cos(angle);
                            double dy = Math.sin(angle);
                            int rc = 3*TempEdge.vertexA.r;
                            edge.x_center = TempEdge.vertexA.x + (int)(dx*rc);
                            edge.y_center = TempEdge.vertexA.y + (int)(dy*rc);
                            edge.x_weight = TempEdge.vertexA.x + (int)(dx*rc);
                            edge.y_weight = TempEdge.vertexA.y + (int)(dy*rc);
                        }
                        TempEdge.vertexA.degree++;
                        vertexB.degree++;
                        Edges.add(edge);
                        break;
                    }
                }
                TempEdge = null; 
            } 
            catch (Exception ex) {}
        }
        draw(1);
        setGraphType();
        searchButt.setEnabled(checkCondition());
        detailLabel.setVisible(false);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        int before = undo.size();
        actionScript();
        if(undo.size()>before)
            redo.clear();
        redoButt.setEnabled(!redo.isEmpty());
    }
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (mode.equals("Edge") && SwingUtilities.isLeftMouseButton(e)) {
            TempEdge = new TempEdge(x, y);
            for (Vertex s : Vertexs) 
                if (s.inCircle(x, y)) {
                    TempEdge.setA(s);
                    break;
                }
        }
        if(!Vertexs.isEmpty() && selected instanceof Vertex) {
            Vertex v = (Vertex)selected;
            inSelect = (SwingUtilities.isLeftMouseButton(e) && !SwingUtilities.isRightMouseButton(e) &&
                       !SwingUtilities.isMiddleMouseButton(e) && v.inCircle(x,y)) ||
                       (SwingUtilities.isRightMouseButton(e) && !SwingUtilities.isLeftMouseButton(e) &&
                       !SwingUtilities.isMiddleMouseButton(e) && v.aroundName(x,y));
        }
        else if(!Edges.isEmpty() && selected instanceof Edge) {
            Edge v = (Edge)selected;
            inSelect = ((SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isRightMouseButton(e)) && v.aroundWeight(x,y));
        }
        if(SwingUtilities.isMiddleMouseButton(e) && !SwingUtilities.isLeftMouseButton(e) && 
          !SwingUtilities.isRightMouseButton(e)) {
            if(selected instanceof Vertex) {
                Vertex v = (Vertex)selected;
                String head = "<html><CENTER>รายละเอียด</CENTER>";
                String vName = "<html>จุดยอด : "+v.name+"<br>";
                String adjVertex = "<html>จุดยอดประชิด : ";
                ArrayList<Vertex> adjacency = new ArrayList<>();
                for(Edge t : Edges) 
                    if((t.vertexA == v) || (t.vertexB == v))
                        adjacency.add(t.vertexA==v ? t.vertexB : t.vertexA);
                for(Vertex t : Vertexs) 
                    if(adjacency.contains(t))
                        adjVertex += t.name+", ";
                adjVertex = (!adjacency.isEmpty() ? adjVertex.substring(0,adjVertex.length()-2) : adjVertex+"ไม่มี")+"<br>";
                String degree = "<html>ดีกรีของจุดยอด : "+v.degree+"<br>";
                String degreeWidth = degree.substring(6,degree.indexOf("<br>"));
                detailLabel.setText(head+vName+adjVertex+degree);
                Graphics2D g = (Graphics2D)drawingArea.getGraphics();
                FontMetrics met = g.getFontMetrics(new Font("TH Sarabun New",Font.BOLD,20));
                int height = met.getHeight()*4;
                vName = vName.substring(6,vName.indexOf("<br>"));
                adjVertex = adjVertex.substring(6,adjVertex.indexOf("<br>"));
                int width = Math.max(Math.max(met.stringWidth(vName),met.stringWidth(adjVertex)),met.stringWidth(degreeWidth));
                detailLabel.setSize(width+10,height);
                detailLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                int x_bounds = v.x+detailLabel.getWidth()>screenSize.width ? v.x-5-detailLabel.getWidth() : v.x+5;
                int y_bounds = v.y+detailLabel.getHeight()>screenSize.height-
                               Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration()).bottom - 50 ?
                               v.y-5-detailLabel.getHeight() : v.y+5;
                detailLabel.setLocation(x_bounds,y_bounds);
                detailLabel.setOpaque(true);
                detailLabel.setBackground(new Color(254, 255, 208));
                detailLabel.setVisible(true);
                setCursor(new Cursor(Cursor.TEXT_CURSOR));
            }
            else if(selected instanceof Edge) {
                Edge t = (Edge)selected;
                String head = "<html><CENTER>รายละเอียด</CENTER>";
                String edgeName = t.vertexA.name+", "+t.vertexB.name;
                if(t.vertexA.name.length() != t.vertexB.name.length()) {
                    edgeName = t.vertexA.name.length()<t.vertexB.name.length() ?
                               t.vertexA.name+", "+t.vertexB.name : t.vertexB.name+", "+t.vertexA.name;
                }
                else if(!t.vertexA.name.equals(t.vertexB.name)) {
                    for(int i=0;i<t.vertexA.name.length();i++) {
                        if(t.vertexA.name.charAt(i)<t.vertexB.name.charAt(i)) {
                            edgeName = t.vertexA.name+", "+t.vertexB.name;
                            break;
                        }
                        else if(t.vertexA.name.charAt(i)>t.vertexB.name.charAt(i)){
                            edgeName = t.vertexB.name+", "+t.vertexA.name;
                            break;
                        }
                    }
                }
                String eName = "<html>เส้นเชื่อม : "+edgeName.substring(0,edgeName.indexOf(", "))+edgeName.substring(edgeName.indexOf(", ")+2) +"<br>";
                double dataWeight = Double.parseDouble(t.weight);
                String dataW = Double.toString(dataWeight);
                String eWeight = "<html>ค่าน้ำหนัก : "+((dataWeight%1==0&&!t.weight.contains("E")) ? dataW.substring(0,dataW.length()-2) : t.weight)+"<br>";
                String incVertex = "<html>จุดยอดตกกระทบ : "+ edgeName + "<br>";

                detailLabel.setText(head+eName+eWeight+incVertex);
                Graphics2D g = (Graphics2D)drawingArea.getGraphics();
                FontMetrics met = g.getFontMetrics(new Font("TH Sarabun New",Font.BOLD,20));
                int height = met.getHeight()*4;
                eWeight = eWeight.substring(6,eWeight.indexOf("<br>"));
                incVertex = incVertex.substring(6,incVertex.indexOf("<br>"));
                int width = Math.max(met.stringWidth(eWeight),met.stringWidth(incVertex));
                detailLabel.setSize(width+10,height);
                detailLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                int x_bounds = t.x_center+detailLabel.getWidth()>screenSize.width ? t.x_center-5-detailLabel.getWidth() : t.x_center+5;
                int y_bounds = t.y_center+detailLabel.getHeight()>screenSize.height-
                               Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration()).bottom - 50 ?
                               t.y_center-5-detailLabel.getHeight() : t.y_center+5;
                detailLabel.setLocation(x_bounds,y_bounds);
                detailLabel.setOpaque(true);
                detailLabel.setBackground(new Color(254, 255, 208));
                detailLabel.setVisible(true);
                setCursor(new Cursor(Cursor.TEXT_CURSOR));
            }
        }
    }
     @Override
    public void mouseExited(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
}