package hackathon.faceplant;

import java.io.IOException;

import com.att.m2x.client.M2XClient;
import com.att.m2x.client.M2XDevice;
import com.att.m2x.client.M2XResponse;
import com.att.m2x.client.M2XStream;

public class M2XDoorEvents {
    private final M2XClient client = new M2XClient("6b7abab06dea4e180b4f7b45eb755cb1");
    private final M2XDevice device = client.device("ab9c12c82f5da02754b8d3ac3cb23adf");

    public void unlockDoor() throws IOException {
    	M2XStream stream = device.stream("doorsunlocked");
    	final M2XResponse resp = stream.updateValue("{\"value\": \"1\"}");
    	if (resp.status != 202) {
    		throw new IOException("M2X Failure: " + resp.status);
    	}
    }

    public void lockDoor() throws IOException {
    	M2XStream stream = device.stream("doorslocked");
    	final M2XResponse resp = stream.updateValue("{\"value\": \"1\"}");
    	if (resp.status != 202) {
    		throw new IOException("M2X Failure: " + resp.status);
    	}
    }

	public static void main(String[] args) throws IOException {
		M2XDoorEvents me = new M2XDoorEvents();
		me.unlockDoor();
		me.lockDoor();
	}
}
