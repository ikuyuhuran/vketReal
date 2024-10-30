package javaproject1;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JFrame1 extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static String epos = "http://www.epson-pos.com/schemas/2011/03/epos-print";

    private int rNumber = 10;  // 受付番号カウンター
    private int pNumber;
    private int puNumber =0;
    private JTextField ipField; // IPアドレス入力欄
    private JButton printButton;
    private JButton reserve_printButton;
    private JLabel rNumberLabel;  
    private JLabel uaruJLabel; // 受付番号表示用ラベル
    private JButton re_reserve_printButton;
    private JButton re_printButton;
    private  String req ;

    public JFrame1() {
        this.setTitle("印刷入力画面");
        this.setLayout(new BorderLayout());
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // 入力フィールドとボタンのパネルを作成
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));

        JLabel ipLabel = new JLabel("IPアドレス:");
        ipField = new JTextField(10); // 幅を狭くするために10に変更

        // 受付番号表示ラベルを追加
            
            JLabel displayRNumberLabel = new JLabel("現在受付");
        displayRNumberLabel.setFont(new Font("MS Gothic", Font.BOLD, 30)); // 日本語フォントを指定

        rNumberLabel = new JLabel(String.valueOf( "番号:"+rNumber));  // 初期値を表示
        rNumberLabel.setFont(new Font("MS Gothic", Font.BOLD, 30)); // 太くて大きいフォントを指定
 //
 uaruJLabel = new JLabel();  // 初期値を表示
 uaruJLabel.setFont(new Font("MS Gothic", Font.BOLD, 30)); // 太くて大きいフォントを指定

 inputPanel.add(ipLabel);
 inputPanel.add(ipField);
 inputPanel.add(uaruJLabel);
 inputPanel.add(uaruJLabel); 

 //
        inputPanel.add(ipLabel);
        inputPanel.add(ipField);
        inputPanel.add(displayRNumberLabel);
        inputPanel.add(rNumberLabel);  // 受付番号を表示するラベルをパネルに追加
        //
       
        ImageIcon printIcon = new ImageIcon("src/resources/co.png"); // 画像ファイル名を指定
        printButton = new JButton("印刷", printIcon);  // ボタンにアイコンとテキストを設定
        printButton.addActionListener(this);
        inputPanel.add(printButton);

        reserve_printButton = new JButton("控え印刷");  // ボタンにアイコンとテキストを設定
        reserve_printButton.addActionListener(this);
        inputPanel.add(reserve_printButton);
        
        //再印字
        re_reserve_printButton = new JButton("控え印刷再印字");  // ボタンにアイコンとテキストを設定
        re_reserve_printButton.addActionListener(this);
        inputPanel.add(re_reserve_printButton);
        re_printButton = new JButton("再印字");  // ボタンにアイコンとテキストを設定
        re_printButton.addActionListener(this);
        inputPanel.add(re_printButton);


        this.add(inputPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // IPアドレスの取得とデフォルト設定
            String ip = ipField.getText().trim();
            if (ip.isEmpty()) {
                ip = "192.168.100.7";  // デフォルトのIPアドレス
            }

           
            
            if(e.getSource()==printButton){
                String address = "http://"+ ip + "/cgi-bin/epos/service.cgi?devid=local_printer&timeout=10000";

                URL url = new URL(address);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
                conn.setRequestProperty("SOAPAction", "\"\"");
             req = 
            "<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'>" +
            "<s:Body>" +
            "<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>"+
            "<text lang='ja'/>"+
            "<text align='center'/>"+
            "<text smooth='true'/>"+
            "<text reverse='true' ul='false' em='false' color='color_1'/>"+
            "<text width='1' height='2'/>"+
            "<text>　　　　　　　　　　　　　　　　　　&#10;</text>"+
            "<text width='3' height='3'/>"+
            "<text>　受付番号　&#10;</text>"+
            "<text width='1' height='1'/>"+
            "<text>　　　　　　　　　　　　　　　　　　&#10;</text>" + 
            "<text width='3' height='3'/>"+
            "<text reverse='false' ul='false' em='false' color='color_1'/>"+
            "<feed line='1'/>"+
            "<text>"+String.format("%04d",rNumber)+"&#10;</text>"+
            "<text width='1' height='1'/>"+
            "<text>------------------------------------------&#10;</text>"+
            " <text>リアルVRネームプレート注文情報入力フォーム&#10;こちら&#10;</text>\r\n" + //
                        //"    <logo key1='32' key2='35'/>\r\n" + //
                        "<symbol type='qrcode_model_1' level='level_h' width='3' height='0' size='0'>https://docs.google.com/forms/d/e/1FAIpQLSdHxo0rTZYeSkm1lvKmCX885Ydih39N0s3IK3k3QiwTM4Jyuw/viewform</symbol>"+
                        "    <layout type='receipt_bm' width='580' height='0' margin-top='3000' margin-bottom='15' offset-cut='0' offset-label='0'/>\r\n" + //
                        "    <text>------------------------------------------&#10;</text>\r\n" + //
                        "    <text>X(旧twitter)↓&#10;</text>"+
                        //"<symbol type='qrcode_model_1' level='level_h' width='6' height='3' size='0'>https://x.com/nsd244</symbol>"+
                        "<logo key1='32' key2='34'/>"+
                        
    //"<image width='8' height='48'>8PDw8A8PDw/w8PDwDw8PD/Dw8PAPDw8P8PDw8A8PDw/w8PDwDw8PD/Dw8PAPDw8P</image>" + 
    "<cut type='feed'/>"+
    "</epos-print>"+
    "</s:Body>" +
        "</s:Envelope>"; try (OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
            w.write(req);
        }

        conn.connect();

        StreamSource source = new StreamSource(conn.getInputStream());
        DOMResult result = new DOMResult();
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);

        //Document doc = (Document) result.getNode();
        //Element el = (Element) doc.getElementsByTagNameNS(epos, "response").item(0);
        //JOptionPane.showMessageDialog(this, "印刷成功: " + el.getAttribute("success"));

        // 受付番号をインクリメントしてラベルを更新
        rNumber++;
        rNumberLabel.setText(String.valueOf("番号:"+(rNumber-1)));
    }else if(e.getSource()==reserve_printButton){
        puNumber =pNumber;
            String address = "http://" + ip + "/cgi-bin/epos/service.cgi?devid=local_printer&timeout=10000";

                URL url = new URL(address);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
                conn.setRequestProperty("SOAPAction", "\"\"");
                req=
                "<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'>" +
            "<s:Body>" +
            "<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>"+
            "<text lang='ja'/>"+
            "<text align='center'/>"+
            "<text width='3' height='3'/>";
                for(int i = pNumber;i<=rNumber+0;i++){
             req +=  
            
                        "<text width='2' height='2'/>"+
                        "<text>　受付番号(控え)　&#10;</text>"+
                        "<text>"+String.format("%04d",pNumber)+"&#10;</text>"+
                        "<cut type='feed'/>" 

                    ;
                    pNumber ++;}req += 
    "</epos-print>"+
    "</s:Body>" +
        "</s:Envelope>"; try (OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
            w.write(req);
        }

        conn.connect();

        StreamSource source = new StreamSource(conn.getInputStream());
        DOMResult result = new DOMResult();
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);
        
        req=null;
        

             // ラベルを更新
    }else if(e.getSource() ==re_printButton){
        String address = "http://"+ ip + "/cgi-bin/epos/service.cgi?devid=local_printer&timeout=10000";

        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        conn.setRequestProperty("SOAPAction", "\"\"");
     req = 
    "<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'>" +
    "<s:Body>" +
    "<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>"+
    "<text lang='ja'/>"+
    "<text align='center'/>"+
    "<text smooth='true'/>"+
    "<text reverse='true' ul='false' em='false' color='color_1'/>"+
    "<text width='1' height='2'/>"+
    "<text>　　　　　　　　　　　　　　　　　　&#10;</text>"+
    "<text width='3' height='3'/>"+
    "<text>　受付番号　&#10;</text>"+
    "<text width='1' height='1'/>"+
    "<text>　　　　　　　　　　　　　　　　　　&#10;</text>" + 
    "<text width='3' height='3'/>"+
    "<text reverse='false' ul='false' em='false' color='color_1'/>"+
    "<feed line='1'/>"+
    "<text>"+String.format("%04d",rNumber-1)+"&#10;</text>"+
    "<text width='1' height='1'/>"+
    "<text>------------------------------------------&#10;</text>"+
    " <text>リアルVRネームプレート注文情報入力フォーム&#10;こちら&#10;</text>\r\n" + //
                //"    <logo key1='32' key2='35'/>\r\n" + //
                "<symbol type='qrcode_model_1' level='level_h' width='3' height='0' size='0'>https://docs.google.com/forms/d/e/1FAIpQLSdHxo0rTZYeSkm1lvKmCX885Ydih39N0s3IK3k3QiwTM4Jyuw/viewform</symbol>"+
                "    <layout type='receipt_bm' width='580' height='0' margin-top='3000' margin-bottom='15' offset-cut='0' offset-label='0'/>\r\n" + //
                "    <text>------------------------------------------&#10;</text>\r\n" + //
                "    <text>X(旧twitter)↓&#10;</text>"+
                //"<symbol type='qrcode_model_1' level='level_h' width='6' height='3' size='0'>https://x.com/nsd244</symbol>"+
                "<logo key1='32' key2='34'/>"+
                
//"<image width='8' height='48'>8PDw8A8PDw/w8PDwDw8PD/Dw8PAPDw8P8PDw8A8PDw/w8PDwDw8PD/Dw8PAPDw8P</image>" + 
"<cut type='feed'/>"+
"</epos-print>"+
"</s:Body>" +
"</s:Envelope>"; try (OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
    w.write(req);
}

conn.connect();

StreamSource source = new StreamSource(conn.getInputStream());
DOMResult result = new DOMResult();
TransformerFactory factory = TransformerFactory.newInstance();
Transformer transformer = factory.newTransformer();
transformer.transform(source, result);
    }else if (e.getSource() == re_reserve_printButton){
        String address = "http://" + ip + "/cgi-bin/epos/service.cgi?devid=local_printer&timeout=10000";

                URL url = new URL(address);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
                conn.setRequestProperty("SOAPAction", "\"\"");
                req=
                "<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'>" +
            "<s:Body>" +
            "<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>"+
            "<text lang='ja'/>"+
            "<text align='center'/>"+
            "<text width='3' height='3'/>";
                for(int i = puNumber;i<=rNumber+0;i++){
             req +=  
            
                        "<text width='2' height='2'/>"+
                        "<text>　受付番号(控え)　&#10;</text>"+
                        "<text>"+String.format("%04d",puNumber)+"&#10;</text>"+
                        "<cut type='feed'/>" 

                    ;
                    puNumber ++;}req += 
    "</epos-print>"+
    "</s:Body>" +
        "</s:Envelope>"; try (OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
            w.write(req);
        }

        conn.connect();

        StreamSource source = new StreamSource(conn.getInputStream());
        DOMResult result = new DOMResult();
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);
        
        req=null;
        

             // ラベルを更新
    
    }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "エラーが発生しました: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new JFrame1();
    }
}
