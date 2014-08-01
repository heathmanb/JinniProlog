package prolog3d;

import prolog.core.Cat;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.*;
import static prolog3d.Prolog3D.drawGraph;

/**
 *
 * @author Brad
 */
public class Tools {  

    /**
     *
     */
    public Tools() {
  }
  
    /**
     *
     * @param N
     * @return
     */
    public static String showChildren(Node N) {
    StringBuffer b=new StringBuffer();
    showChildren(N,b,0);
    return b.toString();  
  }
  
    /**
     *
     * @param N
     * @param b
     * @param d
     */
    public static void showChildren(Node N,StringBuffer b,int d) {
    for(int i=0;i<d;i++) {
      b.append(' ');
    }
    Object D=N.getUserData();  if(null!=D) {
        b.append(D).append(":");
        }
    if(N instanceof Link) {
        b.append(N).append("=>\n");
      N=((Link)N).getSharedGroup();
      showChildren(N,b,d+1);
    }
    else if(N instanceof Leaf) {
        b.append(N).append("\n");
    }
    else {
      Group G=(Group)N;
      b.append(showGroup(G)).append("\n");
      //G.setCapability(Group.ALLOW_CHILDREN_READ);
      Enumeration e=G.getAllChildren();
      while(e.hasMoreElements()) {
        Node C=(Node)e.nextElement();
        showChildren(C,b,d+1);
        //if(e.hasMoreElements()) b.append("\n");
      }
    }
  }
  
    /**
     *
     * @param G
     * @return
     */
    public static String showGroup(Group G) {
    if(G instanceof TransformGroup) {
      TransformGroup TG=(TransformGroup)G;
      return G.toString();
    }
    return G.toString();
  }

    /**
     *
     * @param N
     * @return
     */
    public static Cat collectChildren(Node N) {
    Cat cat=new Cat();
    Object O=N.getUserData();
    HashMap H;
    if(O instanceof HashMap) {
        H=(HashMap)O;
        } else {
        H=null;
        }
    if(null==H) {
        H=new HashMap();
        }
    collectChildren(N,cat,0,H);
    N.setUserData(H);   
    return cat;  
  }
  
    /**
     *
     * @param N
     * @param cat
     * @param d
     * @param H
     */
    public static void collectChildren(Node N,Cat cat,int d,HashMap H) {
   
    if(N instanceof Link) {
      cat.setProp(N,"link", d);
      Node G=((Link)N).getSharedGroup();
      collectChildren(G,cat,d+1,H);
      cat.setMorphism(N,G,"child","link");
    }
    else if(N instanceof Leaf) {
      cat.setProp(N,"leaf", d);
    }
    else {
      Group G=(Group)N;
      cat.setProp(G,"group", d);
      collectGroup(G,cat,d);
      Enumeration e=G.getAllChildren();
      while(e.hasMoreElements()) {
        Node C=(Node)e.nextElement();
        collectChildren(C,cat,d+1,H);
      }
    }
    Node P=N.getParent();
    if(null!=P) {
        cat.setMorphism(P,N,"child","group");
        }
    fixUserData(N,H);
  }
  
    /**
     *
     * @param N
     * @param H
     */
    public static void fixUserData(Node N,HashMap H) {
    Object key=N.getUserData();
    if(null==key) {
        return;
        }
    if(key instanceof String) {
      H.put(key,N);
    }
  }
  
    /**
     *
     * @param G
     * @param cat
     * @param d
     */
    public static void collectGroup(Group G,Cat cat,int d) {
    if(G instanceof TransformGroup) {
      TransformGroup TG=(TransformGroup)G;
      TG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      TG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
     
      /*
      Transform3D T=new Transform3D();
      // some transfor tests go here
      TG.getTransform(T);
      cat.setProp(T,"transform",new Integer(d));
      cat.setMorphism(T,TG,"parent","transform");
      */
    }
  }
  
    /**
     *
     * @param G
     */
    public static void drawChildren(Group G) {
    Cat C=collectChildren(G);
    //Prolog3D.pp(Cat.showInfo(C));
        drawGraph(C);
  }
}
