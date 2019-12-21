/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javafxapplication1;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author admin
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    Button refresh, full_screen;
    @FXML
    private Label label,type_label,country_label, age_label,bp_label;
    @FXML
    private Label ipl_mtchs, ipl_runs,ipl_50s,ipl_100s,ipl_sr,ipl_avg,ipl_wkts,ipl_econ,ipl_5wi,ipl_ct,ipl_st;
    @FXML
    private Label int_mtchs,int_runs,int_50s,int_100s,int_sr,int_avg,int_wkts,int_econ,int_5wi,int_ct,int_st;
    @FXML
    private Label name_label;
    @FXML
    ImageView img,titlebar;
    public int a=0,b=0;
    static String photos,player_name,age,country,type,bp,jdbc, ip_n_port, database,username, password,url1;
    @FXML
    TextField vendor,ip_port, db, user_name, pwd;
    @FXML
    Button db_init;
    
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
            System.out.println("DB INIT SUCESS!");
            System.out.println(url1);
            Stage stageTheEventSourceNodeBelongs = (Stage) ((Node)event.getSource()).getScene().getWindow();
            //stageTheEventSourceNodeBelongs.close();
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
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
    private void onButtonClicked(ActionEvent event){
        refresh.setStyle("-fx-graphic: url('crico_images/refresh.png')");
        full_screen.setStyle("-fx-graphic: url('crico_images/fullscreen.jpg')");
        
        try{
        
               System.out.println(this);
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            System.out.println(database);
            try (Connection conn = DriverManager.getConnection (url1, username, password);) {
                //GETTING CURRENT PLAYER FROM BRINKSTER SERVER
                PreparedStatement ps=conn.prepareStatement("select * from cricomania_current_pool_player");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    System.out.println(rs.getInt("pool_id"));
                    a=rs.getInt("pool_id");
                    System.out.println(rs.getInt("player_id"));
                    b=rs.getInt("player_id");
                    System.out.println(b);
                }
                
                rs.close();
                //conn.close();
                
                     
                //Class.forName ("com.mysql.jdbc.Driver").newInstance ();
                //Connection conn1 = DriverManager.getConnection (url1, username, password);
                //GETTING PLAYER's PERSONAL DETAILS FROM LOCAL SERVER
                PreparedStatement ps1=conn.prepareStatement("select player_name,photo_file_path,age,country,type_of_player,base_price from cricomania_players where player_id="+b);
                ResultSet rs1= ps1.executeQuery();
                while(rs1.next())
                {
                    System.out.println("In the players details");
                    player_name=rs1.getString("player_name");
                    age=Integer.toString((rs1.getInt("age")));
                    country=rs1.getString("country");
                    type=rs1.getString("type_of_player");
                    bp=Double.toString(rs1.getInt("base_price")/10000000.0);
                    photos=rs1.getString("photo");
                    //System.out.println(rs1.getString("photo"));
                }
                
                ps1=conn.prepareStatement("select * from cricomania_stats_international where player_id="+b);
                rs1=ps1.executeQuery();
                while(rs1.next())
                {
                    int_50s.setText(rs1.getString("num_50s"));
                    int_100s.setText(rs1.getString("num_100s"));
                    int_runs.setText(rs1.getString("num_runs"));
                    int_mtchs.setText(rs1.getString("num_matches"));
                    int_avg.setText(rs1.getString("batting_avg"));
                    int_sr.setText(rs1.getString("str_rate"));
                    int_wkts.setText(rs1.getString("wickets"));
                    int_5wi.setText(rs1.getString("5wi"));
                    int_econ.setText(rs1.getString("econ"));
                    int_ct.setText(rs1.getString("catches"));
                    int_st.setText(rs1.getString("stumpings"));
                }
                ps1=conn.prepareStatement("select * from cricomania_stats_ipl where player_id="+b);
                rs1=ps1.executeQuery();
                while(rs1.next())
                {
                    ipl_50s.setText(rs1.getString("num_50s"));
                    ipl_100s.setText(rs1.getString("num_100s"));
                    ipl_runs.setText(rs1.getString("num_runs"));
                    ipl_mtchs.setText(rs1.getString("num_matches"));
                    ipl_avg.setText(rs1.getString("batting_avg"));
                    ipl_sr.setText(rs1.getString("str_rate"));
                    ipl_wkts.setText(rs1.getString("wickets"));
                    ipl_5wi.setText(rs1.getString("5wi"));
                    ipl_econ.setText(rs1.getString("econ"));
                    ipl_ct.setText(rs1.getString("catches"));
                    ipl_st.setText(rs1.getString("stumpings"));
                }
                conn.close();
                rs1.close();
            }
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        System.out.println("Refreshed");
        name_label.setText(player_name);
        type_label.setText(type);
        country_label.setText(country);
        age_label.setText(age);
        bp_label.setText(bp);
        setIPLStats();
        setINTLStats();
        String photo_url="crico_images/crico"+b+".png";
        //Image a=new Image("file:"+photo_url);
        Image a=new Image(photo_url);
        img.setImage(a);
        
    }
    public void onEnter()
    {
        System.out.println("enter clicked");
        
    // OR
    Stage stageTheLabelBelongs = (Stage) full_screen.getScene().getWindow();
    // these two of them return the same stage
    // Swap screen
    stageTheLabelBelongs.setFullScreen(true);
    }
    private void setIPLStats()
    {
        try
        {
            //String url = "jdbc:"+jdbc+"://"+ip_n_port+"/"+database;    
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            Connection conn = DriverManager.getConnection (url1, username, password);
            PreparedStatement ps=conn.prepareStatement("select * from cricomania_stats_ipl where player_id="+b);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                System.out.println("hello" + b);
                ipl_mtchs.setText(rs.getString("num_matches"));
                ipl_runs.setText(rs.getString("num_runs"));
                ipl_50s.setText(rs.getString("num_50s"));
                ipl_100s.setText(rs.getString("num_100s"));
                ipl_avg.setText(rs.getString("batting_avg"));
                ipl_sr.setText(rs.getString("str_rate"));
                ipl_wkts.setText(rs.getString("wickets"));
                ipl_5wi.setText(rs.getString("5wi"));
                ipl_econ.setText(rs.getString("econ"));
                ipl_ct.setText(rs.getString("catches"));
                ipl_st.setText(rs.getString("stumpings"));
            }
            conn.close();
            rs.close();
        }
        catch(Exception e)
        { 
            System.out.println(e.toString());
        }
    }
    
    private void setINTLStats()
    {
        try
        {
            //String url = "jdbc:"+jdbc+"://"+ip_n_port+"/"+database;    
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            Connection conn = DriverManager.getConnection (url1, username, password);
            PreparedStatement ps=conn.prepareStatement("select * from cricomania_stats_international where player_id="+b);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                System.out.println(b);
                int_mtchs.setText(rs.getString("num_matches"));
                int_runs.setText(rs.getString("num_runs"));
                int_50s.setText(rs.getString("num_50s"));
                int_100s.setText(rs.getString("num_100s"));
                int_avg.setText(rs.getString("batting_avg"));
                int_sr.setText(rs.getString("str_rate"));
                int_wkts.setText(rs.getString("wickets"));
                int_5wi.setText(rs.getString("5wi"));
                int_econ.setText(rs.getString("econ"));
                int_ct.setText(rs.getString("catches"));
                int_st.setText(rs.getString("stumpings"));
            }
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
        
        // TODO
        //jdbc="mysql";
        //ip_n_port="localhost:3306";
        //database="cricomania";
        //username="root";
        //password="abc";
        //refresh.setStyle("-fx-graphic: url('crico_images/refresh.png')");
        //full_screen.setStyle("-fx-graphic: url('crico_images/fullscreen.jpg')");
        //url1 = "jdbc:"+jdbc+"://"+ip_n_port+"/"+database;
    }    

    private void setScene(Scene scene) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}