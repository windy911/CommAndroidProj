package com.hhp.commandroidproj.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.hhp.commandroidproj.imageloader.Logger;
import com.hhp.commandroidproj.network.exception.RequestException;

public class AsyncHttpGet extends BaseRequest {
	private static final long serialVersionUID = 2L;
	private DefaultHttpClient httpClient;
	private List<RequestParameter> parameter;

	public AsyncHttpGet(ParseHandler handler, String url,
			List<RequestParameter> parameter,
			RequestResultCallback requestCallback) {
		this.handler = handler;
		this.url = url;
		this.parameter = parameter;
		this.requestCallback = requestCallback;
		if (httpClient == null)
			httpClient = new DefaultHttpClient();
	}

	@Override
	public void run() {
		try {
			if (parameter != null && parameter.size() > 0) {
				StringBuilder bulider = new StringBuilder();
				for (RequestParameter p : parameter) {
					if (bulider.length() != 0) {
						bulider.append("&");
					} 
					bulider.append(Utils.encode(p.getName()));
					bulider.append("=");
					bulider.append(Utils.encode(p.getValue()));
				}
				url += "?" + bulider.toString();
			}
			Logger.d(AsyncHttpGet.class.getName(),
					"AsyncHttpGet  request to url :" + url);
			request = new HttpGet(url);
			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				ByteArrayOutputStream content = new ByteArrayOutputStream();
				response.getEntity().writeTo(content);
				String ret = new String(content.toByteArray()).trim();
				content.close();
				Object Object = null;
				if (AsyncHttpGet.this.handler != null) {//如果是有handler传入，传出解析后的结果Object
					Object = AsyncHttpGet.this.handler.handle(ret);
					if (AsyncHttpGet.this.requestCallback != null
							&& Object != null) {
						AsyncHttpGet.this.requestCallback.onSuccess(Object);
						return;
					}
					if (Object == null || "".equals(Object.toString())) {
						RequestException exception = new RequestException(
								RequestException.IO_EXCEPTION, "data error 001");
						AsyncHttpGet.this.requestCallback.onFail(exception);
					}
				} else {//如果传入handler为空，则 返回 数据给上层解析 。
					AsyncHttpGet.this.requestCallback.onSuccess(ret);
				}
			} else {
				RequestException exception = new RequestException(
						RequestException.IO_EXCEPTION, "data error 002");
				AsyncHttpGet.this.requestCallback.onFail(exception);
			}
			Log.d(AsyncHttpGet.class.getName(),
					"AsyncHttpGet  request to url :" + url + "  finished !");
		} catch (java.lang.IllegalArgumentException e) {
			RequestException exception = new RequestException(
					RequestException.IO_EXCEPTION, "IllegalArgumentException");
			AsyncHttpGet.this.requestCallback.onFail(exception);
			Log.d(AsyncHttpGet.class.getName(),
					"AsyncHttpGet  request to url :" + url + "  onFail  "
							+ e.getMessage());
		} catch (org.apache.http.conn.ConnectTimeoutException e) {
			RequestException exception = new RequestException(
					RequestException.SOCKET_TIMEOUT_EXCEPTION, "ConnectTimeoutException");
			AsyncHttpGet.this.requestCallback.onFail(exception);
			Log.d(AsyncHttpGet.class.getName(),
					"AsyncHttpGet  request to url :" + url + "  onFail  "
							+ e.getMessage());
		} catch (java.net.SocketTimeoutException e) {
			RequestException exception = new RequestException(
					RequestException.SOCKET_TIMEOUT_EXCEPTION, "SocketTimeoutException");
			AsyncHttpGet.this.requestCallback.onFail(exception);
			Log.d(AsyncHttpGet.class.getName(),
					"AsyncHttpGet  request to url :" + url + "  onFail  "
							+ e.getMessage());
		} catch (UnsupportedEncodingException e) {
			RequestException exception = new RequestException(
					RequestException.UNSUPPORTED_ENCODEING_EXCEPTION, "UnsupportedEncodingException");
			AsyncHttpGet.this.requestCallback.onFail(exception);
			Log.d(AsyncHttpGet.class.getName(),
					"AsyncHttpGet  request to url :" + url
							+ "  UnsupportedEncodingException  "
							+ e.getMessage());
		} catch (org.apache.http.conn.HttpHostConnectException e) {
			RequestException exception = new RequestException(
					RequestException.CONNECT_EXCEPTION, "HttpHostConnectException");
			AsyncHttpGet.this.requestCallback.onFail(exception);
			Log.d(AsyncHttpGet.class.getName(),
					"AsyncHttpGet  request to url :" + url
							+ "  HttpHostConnectException  " + e.getMessage());
		} catch (ClientProtocolException e) {
			RequestException exception = new RequestException(
					RequestException.CLIENT_PROTOL_EXCEPTION, "ClientProtocolException");
			AsyncHttpGet.this.requestCallback.onFail(exception);
			e.printStackTrace();
			Log.d(AsyncHttpGet.class.getName(),
					"AsyncHttpGet  request to url :" + url
							+ "  ClientProtocolException " + e.getMessage());
		} catch (IOException e) {
			RequestException exception = new RequestException(
					RequestException.IO_EXCEPTION, "IOException");
			AsyncHttpGet.this.requestCallback.onFail(exception);
			e.printStackTrace();
			Log.d(AsyncHttpGet.class.getName(),
					"AsyncHttpGet  request to url :" + url + "  IOException  "
							+ e.getMessage());
		} finally {
			
		}
		super.run();
	}
}
