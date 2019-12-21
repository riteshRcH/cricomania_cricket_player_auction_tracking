/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package poolsdisplay;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 *
 * @author admin
 */
public class CurrentPoolController implements Initializable{
    @FXML
    Parent root;
    @FXML
    VBox vb;
    @FXML
    Button refresh, full_screen;
    Label l[]=new Label[17];
    @FXML
    Label pool_no,pool_name;
    @FXML
    ImageView img1,img2,img3,img4,img5,img6,img7,img8,img9,img10,img11,img12,img13,img14,img15,img16;
    @FXML
    String available[]=new String[17];
    String sold[]=new String[17];
    int a=0,b=0,count=0;
    static int scene_count=1;
    @FXML
    TextField vendor,ip_port, db, user_name, pwd;
    @FXML
    Button db_init;
    static String jdbc, ip_n_port, database,username, password,url1;
    @FXML
    public void setAndInitDB(ActionEvent event) throws IOException
    {
        System.out.println(this);
        jdbc=vendor.getText();
        ip_n_port=ip_port.getText();
        database=db.getText();
        username=user_name.getText();
        password=pwd.getText();
        url1 = "jdbc:"+jdbc+"://"+ip_n_port+"/"+database;

        if("".equals(jdbc) || "".equals(ip_n_port) || "".equals(database) || "".equals(username) || "".equals(password) )
        {
            Stage dialogStage = new Stage();
            
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setHeight(100);
            dialogStage.setWidth(300);
            dialogStage.setScene(new Scene(VBoxBuilder.create().children(new Text("No field must be kept empty")).alignment(Pos.CENTER).build()));
            dialogStage.showAndWait();
        }
        else
        {
            scene_count++;
            System.out.println("DB INIT SUCESS!");
            System.out.println(url1);
            Stage stageTheEventSourceNodeBelongs = (Stage) ((Node)event.getSource()).getScene().getWindow();
            //stageTheEventSourceNodeBelongs.close();
            Parent root = FXMLLoader.load(getClass().getResource("CurrentPool.fxml"));
            Scene scene = new Scene(root);
            stageTheEventSourceNodeBelongs.setScene(scene);
            stageTheEventSourceNodeBelongs.setFullScreen(true);
            //root.setStyle("-fx-background-image: url('sample.jpg')"); 
            root.setStyle("-fx-background-color: 'white'"); 
            //stageTheEventSourceNodeBelongs.show();
        }
    }
    @FXML
    public void handleFullScreenAction(ActionEvent event)
    {
        System.out.println("button clicked");
        Stage stageTheEventSourceNodeBelongs = (Stage) ((Node)event.getSource()).getScene().getWindow();
    // OR
    Stage stageTheLabelBelongs = (Stage) full_screen.getScene().getWindow();
    // these two of them return the same stage
    // Swap screen
    stageTheLabelBelongs.setFullScreen(true);
    }
    @FXML
    public void handleButtonAction(ActionEvent event) 
    {
        System.out.println(scene_count);
        if(scene_count==2)
        {
            for(int i=1;i<=16;i++)
            {
                l[i]=new Label();
                available[i]=new String();
                sold[i]=new String();
                vb.getChildren().add(l[i]);
                vb.setSpacing(6);
            }
        }
        scene_count++;
        refresh.setStyle("-fx-graphic: url('crico_images/refresh.png')");
        full_screen.setStyle("-fx-graphic: url('crico_images/fullscreen.jpg')");
        for(int i=1;i<=16;i++)
        {
            update_common_player_image(i);
            l[i].setText("");
            l[i].setScaleY(1);
            l[i].setScaleX(1);
            l[i].setTextFill(Color.WHITE);
            //available[i]=new String();
            //sold[i]=new String();
        }
        setAllVisible();
        try{
        Class.forName ("com."+jdbc+".jdbc.Driver").newInstance ();
            try (//Connection conn = DriverManager.getConnection("jdbc:mysql://MySQL5.brinkster.com:3306/mihirsathe", "mihirsathe", "keepitupCSI13")
                   Connection conn = DriverManager.getConnection (url1, username, password); ) {
                PreparedStatement ps=conn.prepareStatement("select * from cricomania_current_pool_player");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    //System.out.println(rs.getInt("pool_id"));
                    a=rs.getInt("pool_id");
                    pool_no.setText(Integer.toString(rs.getInt("pool_id")));
                    //System.out.println(rs.getInt("player_id"));
                    b=rs.getInt("player_id");
                    //b=5;
                }
                
                ps=conn.prepareStatement("select is_available, is_sold from cricomania_players where pool_id="+a);
                rs = ps.executeQuery();
                rs.last();
                count=rs.getRow();
                rs.beforeFirst();
                for(int i=1;i<=count;i++)
                {
                    rs.next();
                    available[i]=rs.getString("is_available");
                    sold[i]=rs.getString("is_sold");
                }
            //    for(int i=1;i<=count;i++)
            //{
              //  System.out.println(available[i]+"  "+sold[i]);
            //}
                conn.close();
                rs.close();                
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        getPoolName();
        getPlayerNames();
    }
    void setAllVisible()
    {
        img6.setVisible(true);
        img7.setVisible(true);
        img8.setVisible(true);
        img9.setVisible(true);
        img10.setVisible(true);
        img11.setVisible(true);
        img12.setVisible(true);
        img13.setVisible(true);
        img14.setVisible(true);
        img15.setVisible(true);
        img16.setVisible(true);       
    }
    void set15Visible()
    {
        setAllVisible();
        img16.setVisible(false); 
    }
    void set14Visible()
    {
        set15Visible();
        img15.setVisible(false);
    }
    void set13Visible()
    {
        set14Visible();
        img14.setVisible(false);
    }
    void set12Visible()
    {
        set13Visible();
        img13.setVisible(false);
    }
    void set11Visible()
    {
        set12Visible();
        img12.setVisible(false);
    }
    void set10Visible()
    {
        set11Visible();
        img11.setVisible(false);
    }
    void set9Visible()
    {
        set10Visible();
        img10.setVisible(false);
    }
    void set8Visible()
    {
        set9Visible();
        img9.setVisible(false);
    }
    void set7Visible()
    {
        set8Visible();
        img8.setVisible(false);
    }
    void set6Visible()
    {
        set7Visible();
        img7.setVisible(false);
    }
    
   private void decideImagesDisplay()
   {
       switch(count)
       {
           case 16:setAllVisible();
               break;
           case 15:set15Visible();
               break;
           case 14:set14Visible();
               break;
           case 13:set13Visible();
               break;
           case 12:set12Visible();
               break;
           case 11:set11Visible();
               break;
           case 10:set10Visible();
               break;
           case 9:set9Visible();
               break;
           case 8:set8Visible();
               break;
           case 7:set7Visible();
               break;
           case 6:set6Visible();
               break;

       }
   }
    
    public void getPoolName()
    {
        try
        {
            String url = "jdbc:"+jdbc+"://"+ip_n_port+"/"+database;    
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            Connection conn = DriverManager.getConnection (url, username, password);
            PreparedStatement ps=conn.prepareStatement("select pool_name from cricomania_pools where pool_id="+a);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                pool_name.setText(rs.getString("pool_name"));
            }
            conn.close();
            rs.close();
        }
        catch(Exception e)
        { 
            System.out.println(e.toString());
        }
    }
    void update_sold_image(int i)
    {
        String sold_image="crico_images/sold.png";
        switch(i)
        {
            case 1:img1.setImage(new Image(sold_image));
                break;
                
            case 2:img2.setImage(new Image(sold_image));
                break;
                
            case 3:img3.setImage(new Image(sold_image));
                break;
                
            case 4:img4.setImage(new Image(sold_image));
                break;
               
            case 5:img5.setImage(new Image(sold_image));
                break;
               
            case 6:img6.setImage(new Image(sold_image));
                break;
               
            case 7:img7.setImage(new Image(sold_image));
                break;
               
            case 8:img8.setImage(new Image(sold_image));
                break;
                
            case 9:img9.setImage(new Image(sold_image));
                break;
                
            case 10:img10.setImage(new Image(sold_image));
                break;
                
            case 11:img11.setImage(new Image(sold_image));
                break;
                
            case 12:img12.setImage(new Image(sold_image));
                break;
                
            case 13:img13.setImage(new Image(sold_image));
                break;
                
            case 14:img14.setImage(new Image(sold_image));
                break;
                
            case 15:img15.setImage(new Image(sold_image));
                break;
                   
            case 16:img16.setImage(new Image(sold_image));
                break;
            default:
                break;
        }
    }
    
    void update_current_player_image(int i)
    {
        String current_image="crico_images/current.png";
        switch(i)
        {
            case 1:img1.setImage(new Image(current_image));
                break;
                
            case 2:img2.setImage(new Image(current_image));
                break;
                
            case 3:img3.setImage(new Image(current_image));
                break;
                
            case 4:img4.setImage(new Image(current_image));
                break;
               
            case 5:img5.setImage(new Image(current_image));
                break;
               
            case 6:img6.setImage(new Image(current_image));
                break;
               
            case 7:img7.setImage(new Image(current_image));
                break;
               
            case 8:img8.setImage(new Image(current_image));
                break;
                
            case 9:img9.setImage(new Image(current_image));
                break;
                
            case 10:img10.setImage(new Image(current_image));
                break;
                
            case 11:img11.setImage(new Image(current_image));
                break;
                
            case 12:img12.setImage(new Image(current_image));
                break;
                
            case 13:img13.setImage(new Image(current_image));
                break;
                
            case 14:img14.setImage(new Image(current_image));
                break;
                
            case 15:img15.setImage(new Image(current_image));
                break;
                   
            case 16:img16.setImage(new Image(current_image));
                break;
            default:
                break;
        }
    }
    void update_common_player_image(int i)
    {
        String current_image="crico_images/img2.png";
        switch(i)
        {
            case 1:img1.setImage(new Image(current_image));
                break;
                
            case 2:img2.setImage(new Image(current_image));
                break;
                
            case 3:img3.setImage(new Image(current_image));
                break;
                
            case 4:img4.setImage(new Image(current_image));
                break;
               
            case 5:img5.setImage(new Image(current_image));
                break;
               
            case 6:img6.setImage(new Image(current_image));
                break;
               
            case 7:img7.setImage(new Image(current_image));
                break;
               
            case 8:img8.setImage(new Image(current_image));
                break;
                
            case 9:img9.setImage(new Image(current_image));
                break;
                
            case 10:img10.setImage(new Image(current_image));
                break;
                
            case 11:img11.setImage(new Image(current_image));
                break;
                
            case 12:img12.setImage(new Image(current_image));
                break;
                
            case 13:img13.setImage(new Image(current_image));
                break;
                
            case 14:img14.setImage(new Image(current_image));
                break;
                
            case 15:img15.setImage(new Image(current_image));
                break;
                   
            case 16:img16.setImage(new Image(current_image));
                break;
            default:
                break;
        }
    }
    public void getPlayerNames()
    {
        try
        {
            System.out.println("In the pools function");
            String url = "jdbc:"+jdbc+"://"+ip_n_port+"/"+database;    
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            Connection conn = DriverManager.getConnection (url, username, password);
            PreparedStatement ps=conn.prepareStatement("select player_name,player_id from cricomania_players where pool_id="+a);
            ResultSet rs = ps.executeQuery();
            rs.last();
            
            count=rs.getRow();
            
            rs.beforeFirst();
            System.out.println(count);
            int temp=count, t=1;
            for(int j=1;j<=temp;j++)
            {
                rs.next();
                System.out.println(available[j]+"  "+sold[j]);
                l[j].setTextFill(Color.WHITE);
                if("no".equals(available[j]))
                {   
                    //l[j].setText(rs.getString("player_name"));
                   Font f=l[4].getFont();
                   l[j].setFont(f);
                  // l[j].setText(rs.getString("player_name"));
                    count--;
                    continue;
                }    
                if("yes".equals(sold[j]))
                {
                    update_sold_image(t);
                }
                
                l[j].setText(rs.getString("player_name"));
                l[j].setFont(new Font(25));
                
                
                if(rs.getInt("player_id")==b)
                {
                    l[j].setScaleY(1);
                    l[j].setScaleX(1);
                    update_current_player_image(t);
                    l[j].setTextFill(Color.WHITE);
                }
                
                t++;
            }
            decideImagesDisplay();
            conn.close();
            rs.close();
        }
        catch(Exception e)
        { 
            System.out.println(e.toString());
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*Parent root1 = null;
        try {
            root1 = FXMLLoader.load(getClass().getResource("CurrentPool.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(CurrentPoolController.class.getName()).log(Level.SEVERE, null, ex);
        }
            Scene scene = new Scene(root1);
        
        if(root.getScene()==scene)
        {
            for(int i=1;i<=16;i++)
            {
                l[i]=new Label();
                available[i]=new String();
                sold[i]=new String();
                vb.getChildren().add(l[i]);
                vb.setSpacing(6);
            }
        }*/
        //Image refresh_image = new Image(getClass().getResourceAsStream("crico_images/refresh.png"));
        
        //refresh.setGraphic(new ImageView(refresh_image));
        
        //img5.setImage(new Image("crico_images/sold.png"));
       /* test.setOnKeyPressed(new EventHandler<KeyEvent>() {

        @Override
        public void handle(KeyEvent KeyEvent) {
            // TODO Auto-generated method stub
            System.out.println("MyController.initialize().new EventHandler() {...}.handle()");

        }
    });*/
        // TODO
    }    
    
}
