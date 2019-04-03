import java.awt.*;

import javax.swing.*;

import no.geosoft.cc.graphics.*;



  
@SuppressWarnings("serial")
public class HaNoiTower extends JFrame
{
  private TowersOfHanoi towersOfHanoi;
  private GWindow window;
  private Peg[] pegs;
  private int nDiscs;
  private int time=50;
  
  public HaNoiTower (int nDiscs)
  {
    super ("Tower of HaNoi Graphic");
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    setBounds(700, 300, 450, 300);
    this.nDiscs = nDiscs;
    
    
    // Create the graphic canvas
    window = new GWindow (new Color (236, 233, 242));//152, 208, 245
    getContentPane().add (window.getCanvas());
    
    // Create scene
    GScene scene = new GScene (window);
    double w0[] = {0.0,  0.0, 0.0};
    double w1[] = {4.0,  0.0, 0.0};
    double w2[] = {0.0,  nDiscs * 2, 0.0};
    scene.setWorldExtent (w0, w1, w2);

    // Add title object and add to scene
    scene.add (new Title());
    
    // Create the 3 pegs and add to the scene
    int nPegs = 3;
    pegs = new Peg[nPegs];
    for (int i = 0; i < nPegs; i++) {
      pegs[i] = new Peg (i + 1.0);
      scene.add (pegs[i]);
    }
    
    // Create the discs and add to the first peg
    for (int i = 0; i < nDiscs; i++) {
      Disc disc = new Disc ((double) (nDiscs - i) / nDiscs);
      disc.setPosition (1.0, i);
      pegs[0].add (disc);
    }
    

    pack();
    setSize (new Dimension (500, 500));
    setVisible (true);

    towersOfHanoi = new TowersOfHanoi();
    towersOfHanoi.solve();
    JOptionPane.showMessageDialog(null,"Complete");
    System.exit(0);
  }



  public void discMoved (int source, int destination) 
  {
    Disc disc = (Disc) pegs[source].getChild (pegs[source].getNChildren()-1);
    
    double y0 = disc.getY();
    double y1 = nDiscs + 2.0;
    double x0 = pegs[source].getX();
    double x1 = pegs[destination].getX();
    
    
    double step = 0.2;//0.2
    double y = y0;
    while (y < y1) {
      disc.setPosition (x0, y);
      disc.redraw();
      window.refresh();
      y += step;
      	try {
      		Thread.sleep(time);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
    }
    pegs[source].remove (disc);
    pegs[destination].add (disc);
    
    step = 0.05;//0.05
    double x = x0;
    while (x != x1) {
      disc.setPosition (x, y);
      disc.redraw();
      window.refresh();
      x += (x1 > x0 ? step : -step);
      if (Math.abs (x - x1) < 0.01) x = x1;
      	try {
      		Thread.sleep(time);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
    }
    
    step = 0.2;//0.2
    y  = y1;
    y1 = pegs[destination].getNChildren() - 1;
    while (y > y1) {
      if (Math.abs (y - y1) < 0.01) y = y1;
      disc.setPosition (x, y);
      disc.redraw();
      window.refresh();
      y -= step;
      	try {
      		Thread.sleep(time);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
    }
  }
  

  
  /**
   * Graphics object for canvas title.
   */
  class Title extends GObject
  {
    private GSegment  anchor_;
    
    public Title()
    {
      GStyle style = new GStyle();
      style.setLineStyle (GStyle.LINESTYLE_INVISIBLE);
      style.setForegroundColor (new Color (100, 100, 200));
      style.setFont (new Font ("serif", Font.PLAIN, 36));
      setStyle (style);

      anchor_ = new GSegment();
      addSegment (anchor_);
      
      //GText text = new GText ("Towers of Hanoi", GPosition.SOUTHEAST);
      //anchor_.setText (text);
    }
    
    
    public void draw()
    {
      anchor_.setGeometry (20, 20);
    }
  }
  

  
  /**
   * Graphics representation of a peg.
   */
  class Peg extends GObject
  {
    private double    x;
    private GSegment  peg;
    private double[]  xy;
    
    
    public Peg (double x)
    {
      this.x = x;

      GStyle style = new GStyle();
      style.setBackgroundColor (new Color (115, 103, 103));
      setStyle (style);

      peg = new GSegment();
      addSegment (peg);

      xy = new double[] {x - 0.05, 0.0,
                          x - 0.05, nDiscs + 2,
                          x + 0.05, nDiscs + 2,
                          x + 0.05, 0.0,
                          x - 0.05, 0.0};
    }


    public double getX()
    {
      return x;
    }
    

    public void draw()
    {
      peg.setGeometryXy (xy);
    }
  }


  
  /**
   * Graphics representation of a disc.
   */
  class Disc extends GObject
  {
    private double    size;
    private GSegment  disc;
    private double    x, y;
    

    public Disc (double size)
    {
      this.size = size;
      
      GStyle style = new GStyle();
      style.setForegroundColor (new Color (225, 242, 0));
      style.setBackgroundColor (new Color (29, 197, 255));
      setStyle (style);
      
      disc = new GSegment();
      addSegment (disc);
    }


    public void setPosition (double x, double y)
    {
      this.x = x;
      this.y = y;
    }


    public double getY()
    {
      return y;
    }
    
    
    public void draw()
    {
      double[] xy = new double[] {x - size / 2.0, y,
                                  x - size / 2.0, y + 1.0,
                                  x + size / 2.0, y + 1.0,
                                  x + size / 2.0, y,
                                  x - size / 2.0, y};

      disc.setGeometryXy (xy);
    }
  }
  


  
  /**
   * Class for solving the "Towers of Hanoi" puzzle.
   */   
  class TowersOfHanoi
  {
    public void solve()
    {
      solve (nDiscs, 0, 2, 1);    
    }
    
    
    private void solve (int nDiscs, int source, int destination, int auxiliary)
    {
      if (nDiscs == 1)
        discMoved (source, destination);
      else if (nDiscs > 1) {
        solve (nDiscs - 1, source, auxiliary, destination);
        discMoved (source, destination);
        solve (nDiscs - 1, auxiliary, destination, source);
      }
    }
  }
  
  

  public static void main (String[] args)
  {
	  @SuppressWarnings("unused")
	HaNoiTower TH;
	  int n=0;
	  try{
			  n=Integer.parseInt(JOptionPane.showInputDialog("enter value (1->10): "));
	  }catch(java.lang.NumberFormatException e)
	  {
		  JOptionPane.showMessageDialog(null,e);
		  //n=Integer.parseInt(JOptionPane.showInputDialog("enter value : "));
	  }
	  if(n>0&&n<10)
		  TH = new HaNoiTower (n);
	  else
		  JOptionPane.showMessageDialog(null,"value invalid");
  }
}
