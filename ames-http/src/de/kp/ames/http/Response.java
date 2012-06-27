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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Response {

	/** The data response as a stream */
    private InputStream stream;

    /** The HTTP status code as returned by the request */
    private int httpStatus;

	public Response(InputStream stream, int httpStatus) {
		this.stream = stream;
		this.httpStatus = httpStatus;
	}
	
    public InputStream getStream() {
        return this.stream;
    }

    /**
     * Gets the HTTP status code of the response
     *
     * @return The status code as an int.
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * @return
     * @throws Exception
     */
    public String asString() throws Exception {
    	
    	InputStreamReader streamReader = new InputStreamReader(this.stream);
    	BufferedReader bufferedReader = new BufferedReader(streamReader);

		StringBuffer buffer = new StringBuffer();

		String line;
		while ( (line = bufferedReader.readLine()) != null) {
			buffer.append(line);
		}

		return buffer.toString();
    	
    }
    
}
