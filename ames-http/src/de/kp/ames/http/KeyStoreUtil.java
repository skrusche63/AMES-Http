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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class KeyStoreUtil {
	
	/**
	 * Load clientstore
	 * 
	 * @return
	 * @throws Exception
	 */
	public static KeyStore getClientStore() throws Exception {

		InputStream stream = new BufferedInputStream(new FileInputStream(HttpConstants.CLIENTSTORE_PATH));
        KeyStore keystore = KeyStore.getInstance("JKS");
        
        try {
        	keystore.load(stream, HttpConstants.CLIENTSTORE_KEYPASS.toCharArray());
        
        } finally {
            stream.close();
        }
		
        return keystore;
		
	}

	/**
	 * Load truststore
	 * 
	 * @return
	 * @throws Exception
	 */
	public static KeyStore getTrustStore() throws Exception {

		InputStream stream = new BufferedInputStream(new FileInputStream(HttpConstants.TRUSTSTORE_PATH));
        KeyStore keystore = KeyStore.getInstance("JKS");
        
        try {
        	keystore.load(stream, HttpConstants.TRUSTSTORE_KEYPASS.toCharArray());
        
        } finally {
            stream.close();
        }
		
        return keystore;
        
	}

}
