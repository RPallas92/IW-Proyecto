package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Clase que obtiene el contenido de una pagina web y genera un hash del mismo.
 */
public class HTMLPage {
	
	private String html;
	
	public HTMLPage(String url)  throws IOException{
		this.html = getHTML(url);
	}
	
	/**
	 * Devuelve el contenido de la pagina [url]
	 */
	private String getHTML(String url) throws IOException{
		URL page = new URL("http://"+url);
		BufferedReader in = new BufferedReader(new InputStreamReader(page.openStream()));
		String html="";
	    String inputLine;
	    while ((inputLine = in.readLine()) != null){
	    	html = html +inputLine;
	    }
	    in.close();
	    return html;
	}
	
	/**
	 * Devuelve el contenido de la pagina [url]
	 */
	public String getHTML(){
		return html;
	}
	
	/**
	 * Devuelve un hash del contenido de la pagina [url]
	 */
	public String getHash() throws NoSuchAlgorithmException, UnsupportedEncodingException{
	    // Hash
	    byte[] plainText = html.getBytes("UTF8");
	    
	    // get a message digest object using the SHA-1 algorithm
	    MessageDigest md = MessageDigest.getInstance("SHA-1"); 
	    return byteArray2Hex(md.digest(plainText));
	}
	
	/**
	 * Devuelve el hash [hash] en formato hexadecimal
	 */
	private String byteArray2Hex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    String hexa = formatter.toString();
	    formatter.close();
	    return hexa;
	}

}
