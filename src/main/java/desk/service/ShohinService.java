package main.java.desk.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;

import javax.net.ssl.SSLParameters;

import main.java.desk.LastException;
import main.java.desk.OriginalBusinessException;
import main.java.desk.http.Authentication;
import main.java.desk.http.HttpSetting;

import java.net.http.HttpResponse;

public class ShohinService {
	private HttpClient httpClient;
	private boolean fAuthentication = true;
	private String authType = "Basic "; //Basic認証orDigest認証
	//private FrameAuthDesign dialog = new FrameAuthDesign();
	private static final String CONTENT_TYPE_NAME = "Content-Type";
	private static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
	private static final String AUTHORIZATION = "Authorization";
	
	public ShohinService() {
		var sslParams = new SSLParameters();
        sslParams.setEndpointIdentificationAlgorithm("HTTPS"); //LDAPS
        sslParams.setProtocols(new String[] {HttpSetting.sslProtocol[3]});
		httpClient = HttpClient.newBuilder().sslParameters(sslParams)
				.connectTimeout(java.time.Duration.ofMillis(1000))
				.version(HttpClient.Version.HTTP_1_1).build();
		//.sslContext(TrustCertificate.CertificateThrough()) //証明書検証スルー
	}
	
	public void httpGet(String uriStr) throws OriginalBusinessException {
		//String json = null;
		
		URI uri = URI.create(uriStr);
		HttpRequest req = requestSetting(HttpRequest.newBuilder().GET(), uri);
		HttpResponse<String> res = httpRequest(httpClient, req);
		//Map<String, List<String>> maps = res.headers().map();
		//Map<String, String> dmap = Authentication.analysis(maps);
		//authType = dmap.get(Authentication.AUTHENTICATE);
		
		/*if (res.statusCode() == HttpURLConnection.HTTP_OK) {
			json = res.body().toString();
		}*/
	}
	
	public void httpPost(String uriStr, String jsonStr) throws OriginalBusinessException {
		URI uri = URI.create(uriStr);
		HttpRequest req = requestSetting(HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(jsonStr)), uri);
		HttpResponse<String> res = httpRequest(httpClient, req);
	}
	
	public void httpPut(String uriStr, String jsonStr) throws OriginalBusinessException {
		URI uri = URI.create(uriStr);
		HttpRequest req = requestSetting(HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString(jsonStr)), uri);
		HttpResponse<String> res = httpRequest(httpClient, req);
	}
	
	public void httpDelete(String uriStr) throws OriginalBusinessException {
		URI uri = URI.create(uriStr);
		HttpRequest req = requestSetting(HttpRequest.newBuilder().DELETE(), uri);
		HttpResponse<String> res = httpRequest(httpClient, req);
	}
	
	private int lastStatusCode;
	private HttpHeaders lastHeaders;
	private String lastBody;
	public int getLastStatusCode() {
		return lastStatusCode;
	}
	public HttpHeaders getLastHeaders() {
		return lastHeaders;
	}
	public String getLastBody() {
		return lastBody;
	}
	
	private HttpResponse<String> httpRequest(HttpClient client, HttpRequest req) throws OriginalBusinessException {
		HttpResponse<String> response = null;
		//String resStr = "";
		
		try {
			response = client.send(req, HttpResponse.BodyHandlers.ofString());
		} catch (HttpConnectTimeoutException ex) {
			//window.showDialog("サーバーに接続できませんでした。", "HTTP接続タイムアウト", JOptionPane.ERROR_MESSAGE);
			String cmethod = new Object(){}.getClass().getEnclosingMethod().getName();
			LastException.setLastException(cmethod, "", ex);
			LastException.logWrite();
			throw new OriginalBusinessException("オリジナル例外");
		} catch (IOException | InterruptedException ex) {
			String cmethod = new Object(){}.getClass().getEnclosingMethod().getName();
			LastException.setLastException(cmethod, "", ex);
			LastException.logWrite();
			throw new OriginalBusinessException("オリジナル例外");
		}
		lastStatusCode = response.statusCode();
		lastHeaders = response.headers();
		lastBody = response.body();
		
		/*if (response.statusCode() == HttpURLConnection.HTTP_OK || 
				response.statusCode() == HttpURLConnection.HTTP_CREATED || 
				response.statusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
			resStr = response.body().toString();
			window.getLabelArea().append(response.headers().toString());
		} else {
			switch (response.statusCode()) {
			case HttpURLConnection.HTTP_UNAUTHORIZED : //認証が必要
				window.getLabelArea().append(response.headers().toString());
				dialog.getDialog().setVisible(true);
				fAuthentication = true;
				break;
			case HttpURLConnection.HTTP_BAD_REQUEST :
				resStr = response.body().toString();
				window.getLabelArea().append(response.headers().toString());
				break;
			default : //その他のエラー
				window.showDialog(response.headers().toString(), String.valueOf(response.statusCode()), JOptionPane.ERROR_MESSAGE);
				break;
			}
		}*/
		//window.getTextReqBody().setText(resStr);
		
		return response;
	}
	
	private HttpRequest requestSetting(Builder builder, URI uri) {
		if (fAuthentication && Authentication.getUserID().equals("") == false) {
			if (authType == Authentication.BASIC) {
				//Basic認証
				String basicStr = Authentication.basicRequestHeader();
				builder.setHeader(AUTHORIZATION, basicStr);
			} else {
				//Digest認証
				String a1 = Authentication.digestResponseA1(Authentication.mapParam.get("realm"));
				String a2 = Authentication.digestResponseA2("GET", uri.toString());
				String res = Authentication.digestResponse(a1, a2, Authentication.mapParam.get("nonce"), Authentication.mapParam.get("qop"));
				String req = Authentication.digestRequest(res);
				builder.header(AUTHORIZATION, req);
			}
		}
		HttpRequest request = builder
				.uri(uri)
				.version(httpClient.version()) //コンストラクタで設定したhttpVer //HttpSetting.getHttpVer())
				.timeout(java.time.Duration.ofMillis(3000)) //タイムアウト3秒固定
				.setHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON).build();
		
		return request;
	}
}