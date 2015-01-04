package websocket.afrs;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnMessage;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * The three annotated echo endpoints can be used to test with Autobahn and
 * the following command "wstest -m fuzzingclient -s servers.json". See the
 * Autobahn documentation for setup and general information.
 */
@ServerEndpoint("/websocket/afrsAnnotation")
public class AfrsAnnotation {
    private static final Map<Integer, String> faceIDs = new HashMap<Integer, String>();
    private AfrsFaceRecognizer afr = new AfrsFaceRecognizer();

    static {
    	faceIDs.put(2, "Anthony Martin");
    	faceIDs.put(3, "Joshua Tharp");
    }
    
    @OnMessage
    public void afrsTextMessage(Session session, String msg, boolean last) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(msg, last);
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    @OnMessage(maxMessageSize = 1024*1024)
    public void afrsBinaryMessage(Session session, ByteBuffer bb, boolean last) {
        try {
            if (session.isOpen()) {
            	final StringBuilder msg = new StringBuilder();
            	final FaceMatch face = afr.compareImage(bb);
            	//TODO Check confidence
            	if (faceIDs.containsKey(face.getFace())) {
            		msg.append("{\"name\": \"")
            				.append(faceIDs.get(face.getFace()))
            				.append("\", \"ci\": \"")
            				.append(face.getConfidence())
            				.append("\"}");
            	} else {
            		msg.append("{\"error\": \"You are not authorized to unlock this vehicle\"}");
            	}
                afrsTextMessage(session, msg.toString(), last);
//              session.getBasicRemote().sendBinary(afd.convert(bb), last);
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    /**
     * Process a received pong. This is a NO-OP.
     *
     * @param pm    Ignored.
     */
    @OnMessage
    public void afrsPongMessage(PongMessage pm) {
        // NO-OP
    }
}