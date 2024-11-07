package javaproject1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

public class JFrame1 extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final String EPOS_NAMESPACE = "http://www.epson-pos.com/schemas/2011/03/epos-print";
    private static final String DEFAULT_IP = "192.168.100.7";
    private int rNumber = 0;  // 受付番号カウンター
    private int pNumber = 0;
    private int puNumber = 0;
    private JTextField ipField; // IPアドレス入力欄
    private JButton printButton;
    private JButton reservePrintButton;
    private JButton reReservePrintButton;
    private JButton rePrintButton;
    private JLabel rNumberLabel;

    public JFrame1() {
        setTitle("印刷入力画面");
        setLayout(new BorderLayout());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10)); // 行数を調整

        JLabel ipLabel = new JLabel("IPアドレス:");
        ipField = new JTextField(10); // 幅を狭くするために10に変更

        JLabel displayRNumberLabel = new JLabel("現在受付", JLabel.CENTER);
        displayRNumberLabel.setFont(new Font("MS Gothic", Font.BOLD, 20)); // フォントサイズを調整

        rNumberLabel = new JLabel("番号: " + rNumber, JLabel.CENTER);
        rNumberLabel.setFont(new Font("MS Gothic", Font.BOLD, 20)); // フォントサイズを調整

        // ボタンの作成
        printButton = createButton("印刷", "src/resources/co.png");
        reservePrintButton = new JButton("控え印刷");
        reReservePrintButton = new JButton("控え印刷再印字");
        rePrintButton = new JButton("再印字");

        // ボタンにアクションリスナーを追加
        reservePrintButton.addActionListener(this);
        reReservePrintButton.addActionListener(this);
        rePrintButton.addActionListener(this);

        // パネルにコンポーネントを追加
        inputPanel.add(ipLabel);
        inputPanel.add(ipField);
        inputPanel.add(displayRNumberLabel);
        inputPanel.add(rNumberLabel);
        inputPanel.add(printButton);
        inputPanel.add(reservePrintButton);
        inputPanel.add(reReservePrintButton);
        inputPanel.add(rePrintButton);

        add(inputPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createButton(String text, String iconPath) {
        ImageIcon icon = new ImageIcon(iconPath);
        JButton button = new JButton(text, icon);
        button.addActionListener(this);
        return button;
    }

    private boolean isValidIP(String ip) {
        String ipPattern = "^\\d{1,3}(\\.\\d{1,3}){3}$";
        if (!Pattern.matches(ipPattern, ip)) {
            return false;
        }
        String[] parts = ip.split("\\.");
        for (String part : parts) {
            int num = Integer.parseInt(part);
            if (num < 0 || num > 255) return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // IPアドレスの取得とデフォルト設定
            String ip = ipField.getText().trim();
            if (ip.isEmpty()) {
                ip = DEFAULT_IP;  // デフォルトのIPアドレス
            }

            if (!isValidIP(ip)) {
                showError("無効なIPアドレスです。正しい形式で入力してください。");
                return;
            }

            String address = "http://" + ip + "/cgi-bin/epos/service.cgi?devid=local_printer&timeout=10000";

            if (e.getSource() == printButton) {
                String req = generatePrintRequestXML(rNumber);
                sendRequest(address, req);
                rNumber++;
                updateRNumberLabel();
            } else if (e.getSource() == reservePrintButton) {
                String req = generateReservePrintRequestXML(pNumber, rNumber - 1);
                sendRequest(address, req);
                pNumber = rNumber;
            } else if (e.getSource() == rePrintButton) {
                String req = generateRePrintRequestXML(rNumber - 1);
                sendRequest(address, req);
            } else if (e.getSource() == reReservePrintButton) {
                String req = generateReReservePrintRequestXML(puNumber, rNumber - 1);
                sendRequest(address, req);
                puNumber = rNumber;
            }
        } catch (Exception ex) {
            showError("エラーが発生しました: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendRequest(String address, String req) throws Exception {
        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        conn.setRequestProperty("SOAPAction", "\"\"");

        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
            writer.write(req);
        }

        conn.connect();

        StreamSource source = new StreamSource(conn.getInputStream());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, new DOMResult());

        conn.disconnect();
    }

    private String generatePrintRequestXML(int number) {
        return "<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'>" +
                "<s:Body>" +
                "<epos-print xmlns='" + EPOS_NAMESPACE + "'>" +
                "<text lang='ja'/>" +
                "<text align='center'/>" +
                "<text smooth='true'/>" +
                "<text reverse='true' ul='false' em='false' color='color_1'/>" +
                "<text width='1' height='2'/>" +
                "<text>　　　　　　　　　　　　　　　　　　&#10;</text>" +
                "<text width='3' height='3'/>" +
                "<text>　受付番号　&#10;</text>" +
                "<text width='1' height='1'/>" +
                "<text>　　　　　　　　　　　　　　　　　　&#10;</text>" +
                "<text width='3' height='3'/>" +
                "<text reverse='false' ul='false' em='false' color='color_1'/>" +
                "<feed line='1'/>" +
                "<text>" + String.format("%04d", number) + "&#10;</text>" +
                "<text width='1' height='1'/>" +
                "<text>------------------------------------------&#10;</text>" +
                "<text>リアルVRネームプレート注文情報入力フォーム&#10;こちら&#10;</text>" +
                "<symbol type='qrcode_model_1' level='level_h' width='3' height='0' size='0'>https://docs.google.com/forms/d/e/1FAIpQLSdHxo0rTZYeSkm1lvKmCX885Ydih39N0s3IK3k3QiwTM4Jyuw/viewform</symbol>" +
                "<layout type='receipt_bm' width='580' height='0' margin-top='3000' margin-bottom='15' offset-cut='0' offset-label='0'/>" +
                "<text>------------------------------------------&#10;</text>" +
                "<text>X(旧twitter)↓&#10;</text>" +
                "<logo key1='32' key2='34'/>" +
                "<cut type='feed'/>" +
                "</epos-print>" +
                "</s:Body>" +
                "</s:Envelope>";
    }

    private String generateReservePrintRequestXML(int start, int end) {
        StringBuilder sb = new StringBuilder();
        sb.append("<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'>")
          .append("<s:Body>")
          .append("<epos-print xmlns='").append(EPOS_NAMESPACE).append("'>")
          .append("<text lang='ja'/>")
          .append("<text align='center'/>")
          .append("<text width='3' height='3'/>");

        for (int i = start; i <= end; i++) {
            sb.append("<text width='2' height='2'/>")
            .append("<text>　受付番号(控え)　&#10;</text>")
            .append("<text>").append(String.format("%04d", i))
            .append("&#10;</text>")
            .append("<text width='4' height='4'/>")
            .append("<symbol type='qrcode_model_2' level='level_h' width='3' height='3' size='3'>")
            .append(String.format("%04d", i))
            .append("</symbol>")
            .append("<cut type='feed'/>");
        }

        sb.append("</epos-print>")
          .append("</s:Body>")
          .append("</s:Envelope>");

        return sb.toString();
    }

    private String generateRePrintRequestXML(int number) {
        return "<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'>" +
                "<s:Body>" +
                "<epos-print xmlns='" + EPOS_NAMESPACE + "'>" +
                "<text lang='ja'/>" +
                "<text align='center'/>" +
                "<text smooth='true'/>" +
                "<text reverse='true' ul='false' em='false' color='color_1'/>" +
                "<text width='1' height='2'/>" +
                "<text>　　　　　　　　　　　　　　　　　　&#10;</text>" +
                "<text width='3' height='3'/>" +
                "<text>　受付番号　&#10;</text>" +
                "<text width='1' height='1'/>" +
                "<text>　　　　　　　　　　　　　　　　　　&#10;</text>" +
                "<text width='3' height='3'/>" +
                "<text reverse='false' ul='false' em='false' color='color_1'/>" +
                "<feed line='1'/>" +
                "<text>" + String.format("%04d", number) + "&#10;</text>" +
                "<text width='1' height='1'/>" +
                "<text>------------------------------------------&#10;</text>" +
                "<text>リアルVRネームプレート注文情報入力フォーム&#10;こちら&#10;</text>" +
                "<symbol type='qrcode_model_1' level='level_h' width='3' height='0' size='0'>https://docs.google.com/forms/d/e/1FAIpQLSdHxo0rTZYeSkm1lvKmCX885Ydih39N0s3IK3k3QiwTM4Jyuw/viewform</symbol>" +
                "<layout type='receipt_bm' width='580' height='0' margin-top='3000' margin-bottom='15' offset-cut='0' offset-label='0'/>" +
                "<text>------------------------------------------&#10;</text>" +
                "<text>X(旧twitter)↓&#10;</text>" +
                "<logo key1='32' key2='34'/>" +
                "<cut type='feed'/>" +
                "</epos-print>" +
                "</s:Body>" +
                "</s:Envelope>";
    }

    private String generateReReservePrintRequestXML(int start, int end) {
        StringBuilder sb = new StringBuilder();
        sb.append("<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'>")
          .append("<s:Body>")
          .append("<epos-print xmlns='").append(EPOS_NAMESPACE).append("'>")
          .append("<text lang='ja'/>")
          .append("<text align='center'/>")
          .append("<text width='3' height='3'/>");

        for (int i = start; i <= end; i++) {
            sb.append("<text width='2' height='2'/>")
              .append("<text>　受付番号(控え)　&#10;</text>")
              .append("<text>").append(String.format("%04d", i)).append("&#10;</text>")
              .append("<text width='4' height='4'/>")
              .append("<symbol type='qrcode_model_2' level='level_h' width='3' height='0' size='3'>")
              .append(String.format("%04d", i))
              .append("</symbol>")
              .append("<cut type='feed'/>");
        }

        sb.append("</epos-print>")
          .append("</s:Body>")
          .append("</s:Envelope>");

        return sb.toString();
    }

    private void updateRNumberLabel() {
        rNumberLabel.setText("番号: " + rNumber);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JFrame1::new);
    }
}
