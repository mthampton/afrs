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


        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(server
                + "/remoteservices/v1/vehicle/lock/" + vin);
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

    public boolean unlock() {

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(server
                + "/remoteservices/v1/vehicle/unlock/" + vin);
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
