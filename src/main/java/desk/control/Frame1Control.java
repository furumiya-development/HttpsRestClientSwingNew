package main.java.desk.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpURLConnection;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import main.java.desk.LastException;
import main.java.desk.OriginalBusinessException;
import main.java.desk.model.ShohinEntity;
import main.java.desk.service.ShohinService;

public class Frame1Control implements ActionListener, ListSelectionListener {
	private Frame1Design window;
	private ShohinService service;
	private boolean authenticated = false;
	private static final String MSG_DIALOG_ERROR = "システムエラーが発生しました。\nアプリケーションを終了します。\n";
	private static final String MSG_DIALOG_INPUT = "商品番号は半角数値の0～9999でなければなりません。";
	private static final String MSG_DIALOG_TITLE = "メッセージ";

	public void goEvent() {
		window = new Frame1Design();
		window.tableSetting();
		window.getButtonSelect().addActionListener(this);
		window.getButtonInsert().addActionListener(this);
		window.getButtonUpdate().addActionListener(this);
		window.getButtonDelete().addActionListener(this);
		window.getTable1().getSelectionModel().addListSelectionListener(this);
		
		//window.getFwindow().add(window.getFpanel());
		window.getFWindow().getContentPane().add(window.getFPanel(), BorderLayout.CENTER);
		window.getFWindow().setVisible(true);
		
		service = new ShohinService();
		
		//service.initialData();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent listevent) {
		if(listevent.getValueIsAdjusting()) {
			return;
		}
		window.getTableRowSetTextField();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		//service = new ShohinService();
		if (window.getButtonSelect().equals(event.getSource())) {
			findAll();
		} else if (window.getButtonInsert().equals(event.getSource())) {
			create();
		} else if (window.getButtonUpdate().equals(event.getSource())) {
			edit();
		} else if (window.getButtonDelete().equals(event.getSource())) {
			remove();
		}
	}
	
	private void findAll() {
		try {
			service.httpGet(window.getTextUri().getText());
			if (authCheck(false) == true) {
				service.httpGet(window.getTextUri().getText());
				authCheck(true);
			}
		} catch (OriginalBusinessException ex) {
			msgDialogModal(MSG_DIALOG_TITLE, MSG_DIALOG_ERROR, JOptionPane.ERROR_MESSAGE);
			window.getFWindow().dispose();
			return;
		}
		
		if (service.getLastStatusCode() == HttpURLConnection.HTTP_OK) {
			setTable(service.getLastBody());
			window.getLabelArea().append("データを全件取得しました。\n");
		}
	}
	
	private void setTable(String json) {
		window.getDTableModel().setRowCount(0); //表示行クリア
		List<ShohinEntity> list;
		try {
			list = new ObjectMapper().readValue(json, new TypeReference<List<ShohinEntity>>() {});
			for (int i = 0; i < list.size(); i++) {
	            String ldate = ((ShohinEntity)list.get(i)).getEditDate().toString();
	            ldate = ldate.substring(0,4) + "/" + ldate.substring(4,6) + "/" + ldate.substring(6,8);
	            String ltime = ((ShohinEntity)list.get(i)).getEditTime().toString();
	            ltime = String.format("%6s", ltime).replace(" ", "0");
	            ltime = ltime.substring(0,2) + ":" + ltime.substring(2,4) + ":" + ltime.substring(4,6);
	            Object[] Objrs = {((ShohinEntity)list.get(i)).getNumId(),
	            		((ShohinEntity)list.get(i)).getShohinCode(),
	            		((ShohinEntity)list.get(i)).getShohinName(),
	            			ldate,
	            			ltime,
	            		((ShohinEntity)list.get(i)).getNote()};
	            window.getDTableModel().addRow(Objrs);
			}
			window.tableSetting();
	        window.textFieldClear();
		} catch (JsonProcessingException ex) {
			msgDialogModal(MSG_DIALOG_TITLE, MSG_DIALOG_ERROR, JOptionPane.ERROR_MESSAGE);
			String cmethod = new Object(){}.getClass().getEnclosingMethod().getName();
			LastException.setLastException(cmethod, "", ex);
			LastException.logWrite();
			window.getFWindow().dispose();
			return;
			//e.printStackTrace();
		}
	}
	
	private void create() {
		if (shohinNumMatcher(window.getTextShohinNum().getText()) == false) {
			msgDialogModal(MSG_DIALOG_TITLE, MSG_DIALOG_INPUT, JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String jsonStr = createJsonStr();
		try {
			service.httpPost(window.getTextUri().getText() + "/" + window.getLabelNumId().getText(), jsonStr);
			if (authCheck(false) == true) {
				service.httpPost(window.getTextUri().getText() + "/" + window.getLabelNumId().getText(), jsonStr);
				authCheck(true);
			}
		} catch (OriginalBusinessException ex) {
			msgDialogModal(MSG_DIALOG_TITLE, MSG_DIALOG_ERROR, JOptionPane.ERROR_MESSAGE);
			window.getFWindow().dispose();
			return;
		}
		
		
		if (service.getLastStatusCode() == HttpURLConnection.HTTP_CREATED) {
			window.getLabelArea().append("データを1件追加しました。\n");
		}
	}
	
	private void edit() {
		int cnt = window.getDTableModel().getRowCount();
		if (cnt <= 0 || window.getLabelNumId().getText().equals("")) {
			msgDialogModal("商品IDなし", "更新する商品行が選択できていません。", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (shohinNumMatcher(window.getTextShohinNum().getText()) == false) {
			msgDialogModal(MSG_DIALOG_TITLE, MSG_DIALOG_INPUT, JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String jsonStr = createJsonStr();
		try {
			service.httpPut(window.getTextUri().getText() + "/" + window.getLabelNumId().getText(), jsonStr);
			if (authCheck(false) == true) {
				service.httpPut(window.getTextUri().getText() + "/" + window.getLabelNumId().getText(), jsonStr);
				authCheck(true);
			}
		} catch (OriginalBusinessException ex) {
			msgDialogModal(MSG_DIALOG_TITLE, MSG_DIALOG_ERROR, JOptionPane.ERROR_MESSAGE);
			window.getFWindow().dispose();
			return;
		}
		
		
		if (service.getLastStatusCode() == HttpURLConnection.HTTP_OK) {
			window.getLabelArea().append("選択されたレコードを1件更新しました。\n");
		}
	}
	
	private void remove() {
		int cnt = window.getDTableModel().getRowCount();
		if (cnt <= 0 || window.getLabelNumId().getText().equals("")) {
			msgDialogModal("商品IDなし", "削除する商品行が選択できていません。", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (shohinNumMatcher(window.getTextShohinNum().getText()) == false) {
			msgDialogModal(MSG_DIALOG_TITLE, MSG_DIALOG_INPUT, JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		try {
			service.httpDelete(window.getTextUri().getText() + "/" + window.getLabelNumId().getText());
			if (authCheck(false) == true) {
				service.httpDelete(window.getTextUri().getText() + "/" + window.getLabelNumId().getText());
				authCheck(true);
			}
		} catch (OriginalBusinessException e) {
			msgDialogModal(MSG_DIALOG_TITLE, MSG_DIALOG_ERROR, JOptionPane.ERROR_MESSAGE);
			window.getFWindow().dispose();
			return;
		}
		
		
		if (service.getLastStatusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
			window.getLabelArea().append("選択されたレコードを1件削除しました。\n");
		}
	}
	
	private boolean authCheck(boolean retry) {
		var authRetryFlag = false;
		
		window.getTextReqBody().setText(service.getLastBody());
		window.getLabelArea().append(service.getLastHeaders().toString() + "\n");
		if (service.getLastStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
			if (retry == false) {
				//ID、パスワード未設定でのUnauthorizedなので認証処理
				//var dialog = new FrameAuthDesign();
				var auth = new FrameAuthControl();
				auth.goEvent();
				auth.setVisible(true);
				//dialog.getDialog().setVisible(true); //キャンセルを押した場合リトライフラグをONしないことも必要
				authRetryFlag = true;
			}
			else {
				//リトライ(ID、パスワードで試す)でもUnauthorizedなので認証失敗
				authenticated = false;
			}
		}
		else {
			if (authenticated == false) //既に認証済みか？
				authenticated = true; //認証済みにする
			
			if (service.getLastStatusCode() != HttpURLConnection.HTTP_OK && 
					service.getLastStatusCode() != HttpURLConnection.HTTP_CREATED &&
					service.getLastStatusCode() != HttpURLConnection.HTTP_NO_CONTENT) {
				//HTTP_BAD_REQUESTを含むその他のコード
				window.getLabelArea().append("別のステータスコードが返りました。\n");
				msgDialogModal(String.valueOf(service.getLastStatusCode()), service.getLastHeaders().toString(), JOptionPane.ERROR_MESSAGE);
			}
		}
		
		return authRetryFlag;
	}
	
	private String createJsonStr() {
		String str = "{ \"shohinCode\":" + Short.valueOf(window.getTextShohinNum().getText());
		str += ", \"shohinName\": \"" + window.getTextShohinName().getText() +  "\", \"note\": \"" + window.getTextNote().getText() + "\" }";
		
		return str;
	}
	
	private boolean shohinNumMatcher(String txt) {
		return txt.matches("^[0-9]{1,4}$");
	}
	
	private void msgDialogModal(String title, String message, int type) {
		JOptionPane.showMessageDialog(window.getFWindow(), message, title, type);
	}
}