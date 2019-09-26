/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import corbacorr.ConnClient;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import static java.lang.Math.abs;
import java.net.Socket;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import conn.Conn;
import conn.ConnHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;



/**
 *
 * @author User
 */
public class Client_frame extends javax.swing.JFrame {

    static final String CMD_PREFIX = "/";
    static final String CMD_CREATE = CMD_PREFIX + "create ";
    static final String CMD_LIST   = CMD_PREFIX + "list";
    static final String CMD_JOIN   = CMD_PREFIX + "join ";
    
    private Scanner inMessage;
    // исходящее сообщение
    private PrintWriter outMessage;
    
    static Conn connImpl;
    static String token;
    
    int point_x = 0;
    int point_y = 0;
    
    int cur_send_number = 0;

    int b_cross = -1;
    int number = 100;
    int cur_number = -1;
    int score_1 = 0;
    int score_2 = 0;
    static int flag = 0;
    ArrayList< Pair<Integer, Integer>> points;
    ArrayList< Cross > cross;
    ArrayList< Integer > used;

    
     public static class Input implements Runnable {

        public void run() {
            if(flag == 0)
            {
                Scanner in = new Scanner(System.in);
                while (true) {
                    String s = in.nextLine();
                    parse(s);
                }
            }
        }

        void parse(String str) {
            if (str.startsWith(CMD_CREATE)) {
                String name = str.substring(CMD_CREATE.length());
                connImpl.createChatRoom(token, name);
            } else if (str.startsWith(CMD_LIST)) {
                connImpl.listChatRooms(token);
            } else if (str.startsWith(CMD_JOIN)) {
                String name = str.substring(CMD_JOIN.length());
                connImpl.joinChatRoom(token, name);
                flag = 1;
            } else {
                connImpl.sendMessage(token, str);
            }
        }
    }
     
    public Client_frame() throws IOException 
    {   
        initComponents(); 
        ArrayList< Pair<Integer, Integer>> points = new  ArrayList< Pair<Integer, Integer>>();
        
        cross = new  ArrayList< Cross>();
        used = new ArrayList();
        //textField1.setVisible(false);
        
        int size = 6;

        // точки 
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                points.add(new Pair<Integer, Integer>(200 + j*50 - number, 200 + i*50 - number));
            }
        }
        
        for(int i = 0; i < size-1; i++)
        {
            for(int j = 0; j < size-1; j++)
            {
                cross.add(new Cross(points.get(i * size + j),points.get(i * size + j + 1),
                        points.get((i+1)*size + 1 + j),points.get((i+1)*size + j)));
            }    
        }  
        
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                String str = "";
                connImpl.sendMessage(token, "1");
                str = Integer.toString(point_x);
                connImpl.sendMessage(token, str);
                str = Integer.toString(point_y);
                connImpl.sendMessage(token, str);
                connImpl.sendMessage(token, "-1");
                
                cur_number = 1;   
                point_x = point_y = cur_send_number = 0;
                
                if(b_cross == 1)
                {
                    jButton1.setEnabled(true);
                    jButton2.setEnabled(true);
                    textField1.setText("Ожидание нашего хода: ");
                    b_cross = -1;
                }
                else
                {
                    jButton1.setEnabled(false);
                    jButton2.setEnabled(false);
                    textField1.setText("Ожидание хода другого игрока: ");
                }
                    
                textField1.setVisible(true);
                
                if(score_1 > 13)
                {
                    textField1.setText("Победа: ");
                    jButton1.setEnabled(false); 
                    jButton2.setEnabled(false);
                }
            }
        });
        new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                while(true) {
                    String message = connImpl.receiveMessage(token);
                    if (!message.isEmpty()) {
                        System.out.println(message);
                        if(cur_send_number == 0)
                            cur_send_number = Integer.parseInt(message);
                            
                            if(cur_send_number != cur_number)
                            {
                                if(point_x == 0 && Integer.parseInt(message)> 75)
                                    point_x = Integer.parseInt(message);
                                else if(point_y == 0 && Integer.parseInt(message)> 75)
                                    point_y = Integer.parseInt(message);
                                
                                if(point_x != 0 && point_y != 0)
                                {         
                                    if(b_cross != 1)
                                        textField1.setText("Ожидание нашего хода: ");
                                    else
                                        textField1.setText("Ожидание хода другого игрока: ");
                                    work();  
                                }   
                            }
                            else if(Integer.parseInt(message) == -1)
                                cur_number = -1;
                    } else {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
         
            }
        }).start();
    }
        
     void parse(String str) {
            if (str.startsWith(CMD_CREATE)) {
                String name = str.substring(CMD_CREATE.length());
                connImpl.createChatRoom(token, name);
            } else if (str.startsWith(CMD_LIST)) {
                connImpl.listChatRooms(token);
            } else if (str.startsWith(CMD_JOIN)) {
                String name = str.substring(CMD_JOIN.length());
                connImpl.joinChatRoom(token, name);
                flag = 1;
            } else {
                connImpl.sendMessage(token, str);
            }
        }
    public void paint_line(Color col,Pair<Integer, Integer> p1 ,Pair<Integer, Integer> p2)
    {
        Graphics graf = getGraphics();
        Graphics2D gr2d1 = (Graphics2D) graf;
        gr2d1.setPaint(col);
        gr2d1.setStroke ( new BasicStroke ( 5f ) );

        gr2d1.drawLine(p1.getFirst(), p1.getSecond(), p2.getFirst(), p2.getSecond());

    }
    
     public void paint_cross(Color col,Pair<Integer, Integer> p1 ,Pair<Integer, Integer> p2)
    {
        Graphics graf = getGraphics();
        Graphics2D gr2d1 = (Graphics2D) graf;
        gr2d1.setPaint(col);
        gr2d1.setStroke ( new BasicStroke ( 5f ) );
        
        gr2d1.drawLine(p1.getFirst() - 10, p1.getSecond() - 10, p2.getSecond() - 10, p2.getSecond() - 10);
        gr2d1.drawLine(p2.getSecond() - 10, p1.getSecond() - 10, p1.getSecond() - 10, p2.getSecond() - 10);
    }

    @Override
     public void paint(Graphics g) 
       {
                Graphics2D gr2d = (Graphics2D) g;
                gr2d.setPaint(Color.GRAY);
                gr2d.setStroke ( new BasicStroke ( 5f ) );

                for(int i = 0; i < 6; i++)
                {   gr2d.drawLine(200 - number, 200 + i*50- number, 450- number, 200 + i*50- number);
                    gr2d.drawLine(200 + i*50- number, 200 - number,200 + i*50- number, 450- number);  
                }               
       }
     
    void work()
    {
        if(b_cross == -1)
        {
            jButton1.setEnabled(true);
            jButton2.setEnabled(true);
        }
             
        Boolean way = false;    
        if(point_x != 0 && point_y !=0)
        {
            for(int i = 0; i < 25; i++)
            {
                cross.get(i).point_in_segment(new Pair<Integer, Integer>(point_x,point_y));
                if( cross.get(i).p1_p2)
                    paint_line(Color.BLUE,cross.get(i).p1,cross.get(i).p2);
  
                if( cross.get(i).p2_p3) 
                    paint_line(Color.BLUE,cross.get(i).p2,cross.get(i).p3);
                   
                if( cross.get(i).p3_p4)
                    paint_line(Color.BLUE,cross.get(i).p3,cross.get(i).p4);
                   
                if( cross.get(i).p1_p4)  
                    paint_line(Color.BLUE,cross.get(i).p1,cross.get(i).p4);
  
                if(cross.get(i).selected())
                {
                    used.add(-1);
                    if(!used.contains(i))
                    {
                        textField1.setText("Ожидание хода другого игрока: ");
                        jButton1.setEnabled(false); 
                        jButton2.setEnabled(false);
                        used.add(i);
                        score_2++;
                        paint_line(Color.BLUE,cross.get(i).p1,cross.get(i).p3);
                        paint_line(Color.BLUE,cross.get(i).p4,cross.get(i).p2);      
                    }      
                }          
            }      
        }
        point_x = point_y = 0;
        if(score_2 == 25)
        {
            textField1.setText("Поражение: ");
            jButton1.setEnabled(false); 
            jButton2.setEnabled(false);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jButton1 = new javax.swing.JButton();
        checkbox1 = new java.awt.Checkbox();
        checkbox2 = new java.awt.Checkbox();
        jButton5 = new javax.swing.JButton();
        textField1 = new java.awt.TextField();
        label1 = new java.awt.Label();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(204, 204, 255));
        setSize(new java.awt.Dimension(700, 484));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jButton1.setText("Сделать ход");
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        checkbox1.setVisible(false);

        checkbox2.setVisible(false);

        jButton5.setText("Score: 0");
        jButton5.setEnabled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        label1.setAlignment(java.awt.Label.CENTER);
        label1.setBackground(new java.awt.Color(255, 255, 255));
        label1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        label1.setFont(new java.awt.Font("Minion Pro", 1, 24)); // NOI18N
        label1.setForeground(new java.awt.Color(255, 153, 51));
        label1.setText("Коридорчики");

        jButton2.setText("Random");
        jButton2.setPreferredSize(new java.awt.Dimension(99, 23));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(checkbox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(checkbox2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(83, 83, 83))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(label1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textField1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(287, 287, 287)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(textField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkbox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkbox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
        point_x = evt.getX();
        point_y = evt.getY();

        if(point_x != 0 && point_y !=0)
        {
            for(int i = 0; i < 25; i++)
            {
                cross.get(i).point_in_segment(new Pair<Integer, Integer>(point_x,point_y));
                if( cross.get(i).p1_p2)
                    paint_line(Color.BLUE,cross.get(i).p1,cross.get(i).p2);
  
                if( cross.get(i).p2_p3) 
                    paint_line(Color.BLUE,cross.get(i).p2,cross.get(i).p3);
                   
                if( cross.get(i).p3_p4)
                    paint_line(Color.BLUE,cross.get(i).p3,cross.get(i).p4);
                   
                if( cross.get(i).p1_p4)  
                    paint_line(Color.BLUE,cross.get(i).p1,cross.get(i).p4);
  
                if(cross.get(i).selected())
                {
                    used.add(-1);
                    if(!used.contains(i))
                    {
                        jButton1.setEnabled(true); 
                        jButton2.setEnabled(true);
                        used.add(i);
                        score_1++;
                        score_2 = score_1;
                        paint_line(Color.BLUE,cross.get(i).p1,cross.get(i).p3);
                        paint_line(Color.BLUE,cross.get(i).p4,cross.get(i).p2);
                        jButton5.setText("Score: " + score_1);
                        textField1.setText("Ожидание нашего хода: ");
                        b_cross = 1;
                    }      
                }          
            }        
        }       
    }//GEN-LAST:event_formMouseClicked
    
    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyPressed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int number = (int) (Math.random()*25);
        
        if (number > 13)
        {
            point_x = cross.get(number).p1.getFirst() + 20;
            point_y = cross.get(number).p1.getSecond();
        }
        else
        {
            point_x = cross.get(number).p3.getFirst();
            point_y = cross.get(number).p3.getSecond()+ 20;
        }
        

        if(point_x != 0 && point_y !=0)
        {
            for(int i = 0; i < 25; i++)
            {
                cross.get(i).point_in_segment(new Pair<Integer, Integer>(point_x,point_y));
                if( cross.get(i).p1_p2)
                    paint_line(Color.BLUE,cross.get(i).p1,cross.get(i).p2);
  
                if( cross.get(i).p2_p3) 
                    paint_line(Color.BLUE,cross.get(i).p2,cross.get(i).p3);
                   
                if( cross.get(i).p3_p4)
                    paint_line(Color.BLUE,cross.get(i).p3,cross.get(i).p4);
                   
                if( cross.get(i).p1_p4)  
                    paint_line(Color.BLUE,cross.get(i).p1,cross.get(i).p4);
  
                if(cross.get(i).selected())
                {
                    used.add(-1);
                    if(!used.contains(i))
                    {
                        jButton1.setEnabled(true); 
                        used.add(i);
                        score_1++;
                        paint_line(Color.BLUE,cross.get(i).p1,cross.get(i).p3);
                        paint_line(Color.BLUE,cross.get(i).p4,cross.get(i).p2);
                        jButton5.setText("Score: " + score_1);
                        textField1.setText("Ожидание нашего хода: ");
                        b_cross = 1;
                    }      
                }          
            }        
        }
        
           
    }//GEN-LAST:event_jButton2ActionPerformed

  
    public static void main(String[] args) throws IOException {
        
    try{
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");

            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            String name = "Conn";
            connImpl = ConnHelper.narrow(ncRef.resolve_str(name));

            System.out.println("Obtained a handle on server object: " + connImpl);
            token = connImpl.connect();

            new Thread(new Client_frame.Input()).start();
            //new Thread(new Client_frame.Output()).start();
            new Client_frame().setVisible(true);  

        } catch (Exception e) {
            System.out.println("ERROR : " + e) ;
            e.printStackTrace(System.out);
        }    
    

}
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private java.awt.Checkbox checkbox1;
    private java.awt.Checkbox checkbox2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private java.awt.Label label1;
    private java.awt.TextField textField1;
    // End of variables declaration//GEN-END:variables
}
