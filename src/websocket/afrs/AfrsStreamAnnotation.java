package websocket.afrs;


import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.websocket.OnMessage;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * The three annotated echo endpoints can be used to test with Autobahn and
 * the following command "wstest -m fuzzingclient -s servers.json". See the
 * Autobahn documentation for setup and general information.
 */
@ServerEndpoint("/websocket/afrsStreamAnnotation")
public class AfrsStreamAnnotation {
    
    private AfrsFaceDetection afd = new AfrsFaceDetection();
    
    Writer writer;
    OutputStream stream;

    @OnMessage
    public void afrsTextMessage(Session session, String msg, boolean last)
            throws IOException {
        if (writer == null) {
            writer = session.getBasicRemote().getSendWriter();
        }
        writer.write(msg);
        if (last) {
            writer.close();
            writer = null;
        }
    }

    @OnMessage
    public void afrsBinaryMessage(byte[] msg, Session session, boolean last)
            throws IOException {
        if (stream == null) {
            stream = session.getBasicRemote().getSendStream();
        }
        stream.write(afd.convert(msg));
      //  stream.write(msg);
        stream.flush();
        if (last) {
            stream.close();
            stream = null;
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