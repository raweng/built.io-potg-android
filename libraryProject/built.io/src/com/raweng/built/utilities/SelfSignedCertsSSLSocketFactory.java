package com.raweng.built.utilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * This socket factory will create ssl socket that accepts self signed certificate
 * 
 * @author olamy, updated (added new {@link #createSocket(Socket, String, int, boolean)} method.
 * @modified by raw engineer.
 * @version $Id: EasySSLSocketFactory.java 765355 2009-04-15 20:59:07Z evenisse $
 * @since 1.2.3
 */
public final class SelfSignedCertsSSLSocketFactory implements SocketFactory,LayeredSocketFactory{

    private static final SelfSignedCertsSSLSocketFactory DEFAULT_FACTORY = new SelfSignedCertsSSLSocketFactory();

    public static SelfSignedCertsSSLSocketFactory getSocketFactory(){
        return DEFAULT_FACTORY;
    }

    private SSLContext sslcontext = null;

    public SelfSignedCertsSSLSocketFactory(){}

    private static SSLContext createEasySSLContext() throws IOException{
        try{
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new EasyX509TrustManager(null)}, null);
            System.setProperty("http.keepAlive", "false");
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(final String hostname, final SSLSession session){
                    return true;
                }
            });

            return context;
        }catch (Exception e){
            throw new IOException(e.getMessage());
        }
    }

    private synchronized SSLContext getSSLContext() throws IOException{
        if (sslcontext == null){
            sslcontext = createEasySSLContext();
        }
        return sslcontext;
    }

    @Override
    public Socket connectSocket(final Socket sock, final String host, final int port,
            final InetAddress localAddress, int localPort, final HttpParams params)
            throws IOException, UnknownHostException, ConnectTimeoutException{
        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);

        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
        SSLSocket sslsock = (SSLSocket) (sock != null ? sock : createSocket());

        if (localAddress != null || localPort > 0){
            // we need to bind explicitly
            if(localPort < 0){
                localPort = 0; // indicates "any"
            }
            InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
            sslsock.bind(isa);
        }

        sslsock.connect(remoteAddress, connTimeout);
        sslsock.setSoTimeout(soTimeout);
        return sslsock;
    }

    @Override
    public Socket createSocket() throws IOException{
        return getSSLContext().getSocketFactory().createSocket();
    }

    @Override
    public boolean isSecure(final Socket socket) throws IllegalArgumentException{
        return true;
    }

    @Override
    public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException{
        return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    // -------------------------------------------------------------------
    // javadoc in org.apache.http.conn.scheme.SocketFactory says :
    // Both Object.equals() and Object.hashCode() must be overridden
    // for the correct operation of some connection managers
    // -------------------------------------------------------------------
    @Override
    public boolean equals(final Object obj){
        return obj != null && obj.getClass().equals(SelfSignedCertsSSLSocketFactory.class);
    }

    @Override
    public int hashCode(){
        return SelfSignedCertsSSLSocketFactory.class.hashCode();
    }
}