package hackathon.faceplant;

import java.io.IOException;

import com.att.m2x.client.M2XClient;
import com.att.m2x.client.M2XDevice;
import com.att.m2x.client.M2XResponse;
import com.att.m2x.client.M2XStream;

public class M2XDoorEvents {
    private final M2XClient client = new M2XClient("6b7abab06dea4e180b4f7b45eb755cb1");
    private final M2XDevice device = client.device("ab9c12c82f5da02754b8d3ac3cb23adf");

    public void unlockDoor() {
    	logEvent("doorsunlocked");
    }

    public void lockDoor() {
    	logEvent("doorslocked");
    }

    private void logEvent(String type) {
    	M2XStream stream = device.stream(type);
    	try {
    		final M2XResponse resp = stream.updateValue("{\"value\": \"1\"}");
    		if (resp.status != 202) {
    			throw new RuntimeException("M2X Failure: " + resp.status);
    		}
    	} catch (IOException e) {
    		throw new RuntimeException("M2X Failure", e);
    	}
    }
}
