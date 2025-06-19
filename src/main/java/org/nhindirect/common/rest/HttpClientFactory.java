

package org.nhindirect.common.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpClientFactory {
    public static final int DEFAULT_CON_TIMEOUT = 20000;
    public static final int DEFAULT_SO_TIMEOUT = 20000;
    protected static final ThreadSafeClientConnManager conMgr = new ThreadSafeClientConnManager();

    public HttpClientFactory() {
    }

    public static HttpClient createHttpClient() {
        HttpClient client = new DefaultHttpClient(conMgr);
        HttpParams httpParams = client.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
        HttpConnectionParams.setSoTimeout(httpParams, 20000);
        return client;
    }

    public static HttpClient createHttpClient(int conTimeOut, int soTimeout) {
        HttpClient client = new DefaultHttpClient(conMgr);
        HttpParams httpParams = client.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, conTimeOut);
        HttpConnectionParams.setSoTimeout(httpParams, soTimeout);
        return client;
    }

    public static void shutdownClients() {
        conMgr.shutdown();
    }
}
