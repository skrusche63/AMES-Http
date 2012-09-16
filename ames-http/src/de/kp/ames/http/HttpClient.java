package de.kp.ames.http;
/**
 *	Copyright 2012 Dr. Krusche & Partner PartG
 *
 *	AMES-HTTP is free software: you can redistribute it and/or 
 *	modify it under the terms of the GNU General Public License 
 *	as published by the Free Software Foundation, either version 3 of 
 *	the License, or (at your option) any later version.
 *
 *	AMES-HTTP is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 *  See the GNU General Public License for more details. 
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this software. If not, see <http://www.gnu.org/licenses/>.
 *
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

public class HttpClient {

	/** Port for HTTPS communication */
	private static final int DEFAULT_HTTPS_PORT = 443;

	/** Name of HTTPS */
	private static final String HTTPS_NAME = "https";

	/** Label for content-type header */
	private static final String CONTENT_TYPE_LABEL = "Content-type";

	/** Label for content-length header */
	private static final String CONTENT_LENGTH_LABEL = "Content-Length";
	
	/** HTTP content type submitted in HTTP POST request for SOAP calls */
	private static final String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";

	/** Reference to DefaultHttpClient **/
	private DefaultHttpClient httpClient;
	
	public HttpClient() {
		
		try {
			httpClient = createHttpClient();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public Response doGet(String url) throws Exception {
		
		/*
		 * Create HttpGet
		 */
		URI uri = createUri(url);
	    HttpGet httpGet = new HttpGet(uri);

	    /*
	     * Execute request
	     */
	    HttpResponse response =  httpClient.execute(httpGet);
		HttpEntity httpEntity = new BufferedHttpEntity(response.getEntity());

		return new Response(httpEntity.getContent(), response.getStatusLine().getStatusCode());

	}

	/**
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public Response doPost(String url) throws Exception {

		/*
		 * Create HttpPost
		 */
		URI uri = createUri(url);
		HttpPost httpPost = new HttpPost(uri);
		
		/*
		 * Set header
		 */
		httpPost.setHeader(CONTENT_TYPE_LABEL, JSON_CONTENT_TYPE);

	    /*
	     * Execute request
	     */
	    HttpResponse response =  httpClient.execute(httpPost);
		HttpEntity httpEntity = new BufferedHttpEntity(response.getEntity());

		return new Response(httpEntity.getContent(), response.getStatusLine().getStatusCode());

	}

	/**
	 * @param url
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public Response doPost(String url, String data) throws Exception {

		/*
		 * Create HttpPost
		 */
		URI uri = createUri(url);
		HttpPost httpPost = new HttpPost(uri);
		
		/*
		 * Set header
		 */
		byte[] bytes = data.getBytes();
		int length = bytes.length;

		httpPost.setHeader(CONTENT_TYPE_LABEL, JSON_CONTENT_TYPE);
//		httpPost.setHeader(CONTENT_LENGTH_LABEL, String.valueOf(length));

		HttpEntity entity = new StringEntity(data);
		httpPost.setEntity(entity);

	    /*
	     * Execute request
	     */
	    HttpResponse response =  httpClient.execute(httpPost);
		HttpEntity httpEntity = new BufferedHttpEntity(response.getEntity());

		return new Response(httpEntity.getContent(), response.getStatusLine().getStatusCode());

	}

	public Response doPost(String url, InputStream stream, String mimetype) throws Exception {

		/*
		 * Create HttpPost
		 */
		URI uri = createUri(url);
		HttpPost httpPost = new HttpPost(uri);

		/*
		 * Set header
		 */
		httpPost.setHeader(CONTENT_TYPE_LABEL, mimetype);

		long length = getLengthFromInputStream(stream);

		HttpEntity entity = new InputStreamEntity(stream, length);
		httpPost.setEntity(entity);
		
	    HttpResponse response =  httpClient.execute(httpPost);
		HttpEntity httpEntity = new BufferedHttpEntity(response.getEntity());

		return new Response(httpEntity.getContent(), response.getStatusLine().getStatusCode());

	}
	
	/**
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private URI createUri(String url) throws Exception {
		
		URI uri = new URI(url);
		String protocol = uri.getScheme();
		
		if (protocol.equals(HTTPS_NAME) == false)
			throw new Exception("[HttpClient] This protocol is not supported.");
		
		return uri;

	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	private DefaultHttpClient createHttpClient() throws Exception {

	    /*
	     * Register scheme
	     */
		SchemeRegistry registry = new SchemeRegistry();
	    Scheme scheme = new Scheme(HTTPS_NAME, DEFAULT_HTTPS_PORT, createSslSocketFactory());

		registry.register(scheme);
 
		/*
		 * Create Http Client
		 */
		PoolingClientConnectionManager manager = new PoolingClientConnectionManager(registry);
		DefaultHttpClient httpClient = new DefaultHttpClient(manager);

		return httpClient;
		
	}
	
	/**
	 * SSLSocketFactory can be used to validate the identity of the HTTPS server 
	 * against a list of trusted certificates (truststore) and to authenticate to 
	 * the HTTPS server using a private key (clientstore)
	 * 
	 * @return
	 * @throws Exception
	 */
	private SSLSocketFactory createSslSocketFactory() throws Exception {

		/*
		 * Load Truststore (server certificate)  
		 */
		KeyStore trustStore = KeyStoreUtil.getTrustStore();
  
		/*
		 * Load Clientstore (client certificate)
		 */
		KeyStore clientStore = KeyStoreUtil.getClientStore();
  
		/*
		 * Pass client & trust store to Socket Factory
		 * 
		 * The factory is responsible for the verification 
		 * of the server certificate.
		 * 
		 * 			/*
			 * This tells the SSLSocketFactory to accept the certificate even if the hostname doesn't match the information from the certificate. Especially useful when testing using self-signed certificates or changing ip-addresses.
			 */

		SSLSocketFactory socketFactory = new SSLSocketFactory(
			SSLSocketFactory.TLS,
			clientStore, 
			HttpConstants.CLIENTSTORE_KEYPASS, 
			trustStore,
			null,
			(X509HostnameVerifier) SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
		);

		return socketFactory;
		
	}

	private long getLengthFromInputStream(InputStream is) {
		
		byte[] bytes = getByteArrayFromInputStream(is);
		return Long.valueOf(bytes.length);
		
	}
	
	private byte[] getByteArrayFromInputStream(InputStream is) {
    	
    	ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

    	byte[] buffer = new byte[1024];
        int len;
        
        try {
        	while ((len = is.read(buffer, 0, buffer.length)) != -1) {
        		baos.write(buffer, 0, len);
        	}
        	//is.close();

        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        return baos.toByteArray();
    
    }

}

