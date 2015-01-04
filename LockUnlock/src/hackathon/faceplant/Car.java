/*
 * Copyright 2015, AT&T Intellectual Property. All rights reserved.
 */
package hackathon.faceplant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class Car {
	private final M2XDoorEvents doorLog = new M2XDoorEvents();
    private String server;
    private String vin;

    public Car(String server, String vin) {
        this.vin = vin;
        this.server = server;
    }

    public void initialize() {
        lock();
    }

    public boolean lock() {
    	boolean retVal = lockCmd("lock");
    	doorLog.lockDoor();
    	return retVal;
    }

    public boolean unlock() {
    	boolean retVal = lockCmd("unlock");
    	doorLog.unlockDoor();
    	return retVal;
    }

    private boolean lockCmd(String cmd) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(server
                + "/remoteservices/v1/vehicle/" + cmd + '/' + vin);
            // StringEntity input = new StringEntity("product");
            // post.setEntity(input);
            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }

            return true;
        } catch (IOException ioe) {
            return false;

        }
    }
}
