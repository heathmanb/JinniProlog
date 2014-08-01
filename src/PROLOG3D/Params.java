package prolog3d;

import prolog.core.Cat; // ?!

import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.*;
import java.io.*;
import java.beans.XMLDecoder;
import static java.lang.Math.sqrt;
import static prolog.core.RankedGraph.showInfo;
import static prolog3d.Prolog3D.pp;
import static prolog3d.Simple.makeApp;

/**
 *
 * @author Brad
 */
public class Params {

    /**
     *
     */
    public Params() {
  }
  
    /**
     *
     */
    public static boolean reuseTopWindow=false;

    /**
     *
     */
    public static String winTitle="Prolog3D";

    /**
     *
     */
    public static int layoutSleep=10;

    /**
     *
     */
    public static int tick=40;

    /**
     *
     */
    public static int speed=5;

    /**
     *
     */
    public static int printH=240;

    /**
     *
     */
    public static int printW=240;
  
    /**
     *
     */
    public static Color3f textColor=new Color3f(0.6f,0.0f,0.0f);  

    /**
     *
     */
    public static Appearance textApp=makeApp(textColor,0.0f);

    /**
     *
     */
    public static int maxText=20;

    /**
     *
     */
    public static boolean stopAll=false;  
  static private boolean applet=false;

    /**
     *
     */
    public static int verbose=0;

    /**
     *
     */
    public static int interactive=1;

    /**
     *
     */
    public static String bgfile="bg.jpg";
  
    /**
     *
     */
    public static Color3f bgColor = new Color3f(1, 1, 1);

    /**
     *
     */
    public static Color3f bgLightColor = new Color3f(0.5f, 0.5f, 0.5f);

    /**
     *
     */
    public static Color3f bgAmbientColor = new Color3f(0.5f, 0.5f, 0.5f);

    /**
     *
     */
    public static Vector3f bgLightDir  = new Vector3f(16.0f, -16.0f, -16.0f);
  
    /**
     *
     * @param r
     * @param g
     * @param b
     */
    static public void setBgColor(double r,double g,double b) {
    bgColor=new Color3f((float)r, (float)g, (float)b);
  }

    /**
     *
     */
    static public void setApplet() {
    applet=true;
  }  
  
    /**
     *
     * @param i
     */
    static public void setInteractive(int i) {
    interactive=i;
  }  

    /**
     *
     * @return
     */
    static public boolean isApplet() {
    return applet;
  }  
    
  static Random random=new Random();
  
    /**
     *
     * @param max
     * @return
     */
    public static int ri(int max) {
    return random.nextInt(max);
  }
  
    /**
     *
     * @return
     */
    public static final float rf() {
    return rf(1);
  }
  
    /**
     *
     * @param r
     * @return
     */
    public static final float rf(float r) {
    return 2*r*random.nextFloat()-r;
  }
  
    /**
     *
     * @return
     */
    public static final float rf01() {
    return random.nextFloat();
  }
  
    /**
     *
     * @param max
     * @return
     */
    public static final float r(float max) {
    return random.nextFloat()*max;
  }
  
    /**
     *
     * @param r
     * @return
     */
    public static Point3f rs(float r) {
    float a=0;
    float b=0;
    int max=100;
    for(int i=0;i<max;i++) {
      a=rf(1);
      b=rf(1);
      if(a*a+b*b < 1) {
          break;
            }
      if(i==max-1) {
          return null;
            }
    }
    float c=a*a+b*b;
    float s=(float)sqrt(1-c);
    float x=2*a*s;
    float y=2*b*s;
    float z=1-2*c;
    return new Point3f(x*r,y*r,z*r);
  } 

    /**
     *
     * @param seed
     * @param v0
     * @param v
     * @param e0
     * @param e
     * @return
     */
    public static Cat randomCat(int seed,int v0,int v,int e0,int e) {
    Cat RG=new Cat();
    RG.randomize(seed,v0+ri(v),e0+ri(e));
    return RG;
  }
     
    /**
     *
     * @param seed
     * @param v0
     * @param v
     * @param e0
     * @param e
     * @param giant
     * @param m
     * @return
     */
    public static Cat randomRanked(int seed,int v0,int v,int e0,int e,int giant,int m) {
    Cat RG=randomCat(seed,v0,v,e0,e);
        pp(showInfo(RG));
    if(giant>0) {
        RG=(Cat)RG.trimRankedGraph(giant,m);
        }
    //if(g>0) RG=(Cat)RG.trim(g>0,m);
    return RG;
  }
  
    /**
     *
     * @param ms
     */
    static public void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } 
    catch (InterruptedException e) {}
  }
}
