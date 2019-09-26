/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author User
 */
class Pair<T1, T2> { 
    T1 object1; 
    T2 object2; 
 
    Pair(T1 one, T2 two) { 
        object1 = one; 
        object2 = two; 
    } 
 
    public T1 getFirst() { 
        return object1; 
    } 
 
    public T2 getSecond() { 
        return object2; 
    } 
} 

class Cross {
    
    Pair<Integer, Integer> p1;
    Pair<Integer, Integer> p2;
    Pair<Integer, Integer> p3;
    Pair<Integer, Integer> p4;
    
    boolean p1_p2;
    boolean p2_p3;
    boolean p3_p4;
    boolean p1_p4;
    
    
    static int count = 25;
    
    Cross( Pair<Integer, Integer> _p1,  Pair<Integer, Integer> _p2,
            Pair<Integer, Integer> _p3, Pair<Integer, Integer> _p4)
    {
        p1 = _p1;
        p2 = _p2;
        p3 = _p3;
        p4 = _p4;
        p1_p2 = p2_p3 = p3_p4 = p1_p4 = false;
    }
    
    public void point_in_segment (Pair<Integer, Integer> t)
    {
    if(t.getFirst() % 50 == 0)
    {
        if(p1.getFirst() <= t.getFirst() && t.getFirst() <= p4.getFirst() &&
                p1.getSecond() <= t.getSecond() && t.getSecond() <= p4.getSecond())
            p1_p4 = true;
        
        if(p2.getFirst() <= t.getFirst() && t.getFirst() <= p3.getFirst() &&
                p2.getSecond() <= t.getSecond() && t.getSecond() <= p3.getSecond())
            p2_p3 = true;   
    }
    else if(t.getSecond() % 50 == 0)
    {
        if(p1.getFirst() <= t.getFirst() && t.getFirst() <= p2.getFirst() &&
                p1.getSecond() <= t.getSecond() && t.getSecond() <= p2.getSecond())
            p1_p2 = true;
        
        if(p4.getFirst() <= t.getFirst() && t.getFirst() <= p3.getFirst() &&
                p4.getSecond() <= t.getSecond() && t.getSecond() <= p3.getSecond())
            p3_p4 = true;
    }
    }
    public boolean selected()
    {
        if (p1_p2 && p2_p3 && p3_p4 && p1_p4 == true) return true;
        return false;
    }
    
    public void print_cross()
    { 
        System.out.println(this.selected() + " "); 
            
    }
    
    public void Set_p1_p2(Boolean _p1_p2)
    {
        p1_p2 = _p1_p2;
    }
    
    public void Set_p2_p3(Boolean _p2_p3)
    {
        p2_p3 = _p2_p3;
    }
    
    public void Set_p3_p4(Boolean _p3_p4)
    {
        p3_p4 = _p3_p4;
    }
    public void Set_p1_p4(Boolean _p1_p4)
    {
        p1_p4 = _p1_p4;
    }

}
