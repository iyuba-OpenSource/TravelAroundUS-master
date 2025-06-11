package com.iyuba.concept2.network;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public class BaseXmlRequest extends Request<XmlPullParser> {

	private Listener<XmlPullParser> mListener;

	public BaseXmlRequest(int method, String url,
			Listener<XmlPullParser> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		Log.e("BaseXmlRequest", url);
		mListener = listener;
	}

	public BaseXmlRequest(String url, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		this(Method.GET, url, listener, errorListener);
	}

	public BaseXmlRequest(String url, ErrorListener errorListener) {
		this(url, defaultListener, errorListener);
	}

	public BaseXmlRequest(String url) {
		this(url, defaultErrListener);
	}

	@Override
	protected void deliverResponse(XmlPullParser response) {
		mListener.onResponse(response);
	}

	/**
	 * 默认的listener
	 */
	private static Listener<XmlPullParser> defaultListener = new Listener<XmlPullParser>() {
		@Override
		public void onResponse(XmlPullParser response) {
			// TODO 自动生成的方法存根
			Log.d("BaseXmlRequest", response.toString());
		}
	};
	/**
	 * 默认的errlistener
	 */
	private static ErrorListener defaultErrListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			// TODO 自动生成的方法存根
			error.printStackTrace();
		}
	};

	@Override
	protected Response<XmlPullParser> parseNetworkResponse(
			NetworkResponse response) {
		try {
			String xmlString = new String(response.data, "utf-8");
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new StringReader(xmlString));
			return Response.success(xmlPullParser,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (XmlPullParserException e) {
			return Response.error(new ParseError(e));
		}
	}

	/**
	 * 要处理返回的数据，必须调用此函数，否则默认只Log返回信息
	 * 
	 * @param resListener
	 */
	public void setResListener(Listener<XmlPullParser> resListener) {
		this.mListener = resListener;
	}

}
