/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */



package matroskastudycoder;


import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;
import java.net.URLEncoder;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * Elemental example for executing a GET request.
 * <p>
 * Please note the purpose of this application is demonstrate the usage of HttpCore APIs.
 * It is NOT intended to demonstrate the most efficient way of building an HTTP client.
 *
 *
 *
 */
public class HttpControl extends AbstractVLCPlayer implements IVideoPlayer {

    HttpParams params;
    HttpProcessor httpproc;
    HttpContext context;
    HttpHost host;
    DefaultHttpClientConnection conn;
    ConnectionReuseStrategy connStrategy;
    HttpRequestExecutor httpexecutor;

    int status = AbstractVLCPlayer.STATUS_UNKNOWN;
    int time = 0;
    String mediaPath;

    int offset = 0;

    boolean initialized = false;
    
    public String getCurrentTimeSec() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getTimeOffsetSec() {
        return offset;
    }

    public int getCurrentStatus() {
        return this.status;
    }

    private void processRequestResponse(String s) {
        SAXBuilder builder = new SAXBuilder();
        
        try {            
            s = s.trim().replaceFirst("^([\\W]+)<","<");
            StringReader in = new StringReader(s);
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            Element statusElement = root.getChild("state");
            Element timeElement = root.getChild("time");

            time = Integer.parseInt(timeElement.getText());
            String statusStr = statusElement.getText();
            
            if(statusStr.equals("playing")) status = AbstractVLCPlayer.STATUS_PLAYING;
            else if(statusStr.equals("stopped")) status = AbstractVLCPlayer.STATUS_STOPPED;
            else if(statusStr.equals("paused")) status = AbstractVLCPlayer.STATUS_PAUSED;
            
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void sendGetRequest(String target) {

        try {

            //String target = "/requests/status.xml?command=pl_stop";
            //String target = "/requests/status.xml";

            if (!conn.isOpen()) {
                Socket socket = new Socket(host.getHostName(), host.getPort());
                conn.bind(socket, params);
            }
            BasicHttpRequest request = new BasicHttpRequest("GET", target);
            System.out.println(">> Request URI: " + request.getRequestLine().getUri());

            request.setParams(params);
            httpexecutor.preProcess(request, httpproc, context);
            HttpResponse response = httpexecutor.execute(request, conn, context);
            response.setParams(params);
            httpexecutor.postProcess(response, httpproc, context);

            //System.out.println("<< Response: " + response.getStatusLine());
            String responseXML = EntityUtils.toString(response.getEntity());
            //System.out.println(responseXML);
            processRequestResponse(responseXML);
            //System.out.println("==============");
            
            if (!connStrategy.keepAlive(response, context)) {
                conn.close();
            } else {
                System.out.println("Connection kept alive...");
            }
        } catch(Exception e) {
            System.err.println("error:" + e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                System.err.println("errore:" + e);
            }
        }


    }

    public void init(String aHost, String aPort) throws Exception {
        params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
        HttpProtocolParams.setUseExpectContinue(params, true);

        httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                // Recommended protocol interceptors
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        httpexecutor = new HttpRequestExecutor();

        context = new BasicHttpContext(null);
        host = new HttpHost(aHost, Integer.parseInt(aPort));
       
        connStrategy = new DefaultConnectionReuseStrategy();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        //Clear the playlist
        sendGetRequest("/requests/status.xml?command=pl_empty");

        //Load the media
        String m = this.mediaPath;
        m = m.replaceAll("\\\\", "\\\\\\\\");

        sendGetRequest("/requests/status.xml?command=in_play&input=" + URLEncoder.encode(m, "UTF-8"));
        sendGetRequest("/requests/status.xml?command=pl_pause");
        initialized=true;
    }

    public void setVideoFile(String fullPath) {
        this.mediaPath = fullPath;
    }

    public void setTimeOffsetSec(int i) {
        offset = i;
    }

    public void seekMasterTime(int i) {
        String u = "/requests/status.xml?command=seek&val=" + (i+offset);
        sendGetRequest(u);
        System.out.println(u);
    }

    public void plPause() {
        sendGetRequest("/requests/status.xml?command=pl_pause");
    }

    public void plPlay() {
        sendGetRequest("/requests/status.xml?command=pl_play");
    }

    public void plStop()  {
        //Send test request
        sendGetRequest("/requests/status.xml?command=pl_stop");
        
    }

    public void reset() {
        initialized = false;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public HttpControl() {
         conn = new DefaultHttpClientConnection();
    }

}
