import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import java.util.*;

/**
 *
 * @author Erik
 */
public class Escenario extends JFrame{
    private JLabel[][] tablero;     
    private int[][] matrix;
    private int[][] migas;
    private final int dim = 15;
    private HashMap<JLabel,int[]> cords;
    private Set<int[]> naves;

    private ImageIcon robot1;
    private ImageIcon robot2;
    private ImageIcon robot1M;
    private ImageIcon robot2M;
    private ImageIcon huellas;
    private ImageIcon obstacleIcon;
    private ImageIcon sampleIcon;
    private ImageIcon actualIcon;   //0: vacio, 1:robot, 2:nave, 23:muestra, 4: obstaculo
    private ImageIcon motherIcon;
    private int tipo;
    
    private Agente wallE;
    private Agente eva;
   
    private final BackGroundPanel fondo = new BackGroundPanel(new ImageIcon("imagenes/surface.jpg"));
    
    private final JMenu settings = new JMenu("Settigs");
    private final JRadioButtonMenuItem obstacle = new JRadioButtonMenuItem("Obstacle");
    private final JRadioButtonMenuItem sample = new JRadioButtonMenuItem("Sample");
    private final JRadioButtonMenuItem motherShip = new JRadioButtonMenuItem("MotherShip");
    
    public Escenario(){
        this.setContentPane(fondo);
        this.setTitle("Agentes");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setBounds(50,50,dim*50+35,dim*50+85);
        initComponents();
    }
        
    private void initComponents(){
        cords = new HashMap<>();
        naves = new HashSet<>();
        ButtonGroup settingsOptions = new ButtonGroup();
        settingsOptions.add(sample);
        settingsOptions.add(obstacle);       
        settingsOptions.add(motherShip);
        
        JMenuBar barraMenus = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem run  = new JMenuItem("Run");
        
        JMenuItem exit   = new JMenuItem("Exit");
              
        this.setJMenuBar(barraMenus);
        barraMenus.add(file);
        barraMenus.add(settings);
        file.add(run);
        file.add(exit);
        settings.add(motherShip);
        settings.add(obstacle);
        settings.add(sample);
            
        robot1 = new ImageIcon("imagenes/wall-e.png");
        robot1 = new ImageIcon(robot1.getImage().getScaledInstance(50,50,  java.awt.Image.SCALE_SMOOTH));
        
        robot2 = new ImageIcon("imagenes/eva.png");
        robot2 = new ImageIcon(robot2.getImage().getScaledInstance(50,50,  java.awt.Image.SCALE_SMOOTH));
        
        robot1M = new ImageIcon("imagenes/wall-eM.png");
        robot1M = new ImageIcon(robot1M.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
        
        robot2M = new ImageIcon("imagenes/evaM.png");
        robot2M = new ImageIcon(robot2M.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));

        huellas = new ImageIcon("imagenes/huellas.png");
        huellas = new ImageIcon(huellas.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
        
        obstacleIcon = new ImageIcon("imagenes/brick.png");
        obstacleIcon = new ImageIcon(obstacleIcon.getImage().getScaledInstance(50,50,  java.awt.Image.SCALE_SMOOTH));
        
        sampleIcon = new ImageIcon("imagenes/sample.png");
        sampleIcon = new ImageIcon(sampleIcon.getImage().getScaledInstance(50,50,  java.awt.Image.SCALE_SMOOTH));
        
        motherIcon = new ImageIcon("imagenes/mothership.png");
        motherIcon = new ImageIcon(motherIcon.getImage().getScaledInstance(50,50,  java.awt.Image.SCALE_SMOOTH));
        
        this.setLayout(null);
        formaPlano();  
        
        exit.addActionListener(evt -> gestionaSalir(evt));
        run.addActionListener(evt -> gestionaRun(evt));
        obstacle.addItemListener(evt -> gestionaObstacle(evt));
        sample.addItemListener(evt -> gestionaSample(evt));
        motherShip.addItemListener(evt -> gestionaMotherShip(evt));
    
            
        class MyWindowAdapter extends WindowAdapter{
            @Override
            public void windowClosing(WindowEvent eventObject)
            {
		goodBye();
            }
        }
        addWindowListener(new MyWindowAdapter());
        
        // Crea 2 agentes
        wallE = new Agente(2, 3, "Wall-E",robot1, robot1M,matrix, tablero,naves,motherIcon, sampleIcon, huellas); 
        eva = new Agente(10, 11, "Eva",robot2, robot2M ,matrix, tablero,naves,motherIcon, sampleIcon, huellas); 
    }
        
    private void gestionaSalir(ActionEvent eventObject){
        goodBye();
    }
        
    private void goodBye(){
        int respuesta = JOptionPane.showConfirmDialog(rootPane, "Desea salir?","Aviso",JOptionPane.YES_NO_OPTION);
        if(respuesta==JOptionPane.YES_OPTION) System.exit(0);
    }
  
    private void formaPlano(){
        tablero = new JLabel[dim][dim];
        matrix = new int[dim][dim];
        
        for(int i=0; i<dim; i++){
            for(int j=0; j<dim; j++){
                matrix[i][j]=0;
                tablero[i][j] = new JLabel();
                tablero[i][j].setBounds(j*50+10,i*50+10,50,50);
                tablero[i][j].setBorder(BorderFactory.createDashedBorder(Color.white));
                tablero[i][j].setOpaque(false);
                this.add(tablero[i][j]);
                
                int[] aux = {i,j};
                cords.put(tablero[i][j], aux);
                tablero[i][j].addMouseListener(new MouseAdapter() // Este listener nos ayuda a agregar poner objetos en la rejilla
                {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            insertaObjeto(e);
                        }     
                
                });
            }
        }
        
    }
    
    //0: vacio, 1:robot, 2:nave, 3:muestra, 4: obstaculo
    private void gestionaObstacle(ItemEvent eventObject){
        JRadioButtonMenuItem opt = (JRadioButtonMenuItem) eventObject.getSource();
        if(opt.isSelected()){
            //System.out.println("Hola");
            actualIcon = obstacleIcon;
            tipo = 4;
        }
        else{
            actualIcon = null; 
            tipo = 0;
        }       
    }
    
    private void gestionaSample(ItemEvent eventObject){
        JRadioButtonMenuItem opt = (JRadioButtonMenuItem) eventObject.getSource();
        if(opt.isSelected()){
            actualIcon = sampleIcon;
            tipo = 3;
        }
        else{
            actualIcon = null; 
            tipo = 0;
        }    
    }
    
    private void gestionaMotherShip(ItemEvent eventObject){
        JRadioButtonMenuItem opt = (JRadioButtonMenuItem) eventObject.getSource();
        if(opt.isSelected()){
            actualIcon = motherIcon;
            tipo = 2;
        }
        else{
            actualIcon = null; 
            tipo = 0;
        }  
    }
    private void gestionaRun(ActionEvent eventObject){
        if(!wallE.isAlive()){
            wallE.actMat(matrix);
            wallE.start();
        }
        if(!eva.isAlive()){
            eva.actMat(matrix);
            eva.start();
        }
        settings.setEnabled(false);
    }
       
    public void insertaObjeto(MouseEvent e){
        JLabel casilla = (JLabel) e.getSource();
        int i,j;
        
        if(actualIcon!=null){
            i = cords.get(casilla)[0];
            j = cords.get(casilla)[1];
            
            if(matrix[i][j] == 1)   return;
            casilla.setIcon(actualIcon); 
            matrix[i][j] = tipo;
            
            if(tipo == 2){
                int[] cNave = {i,j};
                naves.add(cNave);
            }
        }
    }
    
}
