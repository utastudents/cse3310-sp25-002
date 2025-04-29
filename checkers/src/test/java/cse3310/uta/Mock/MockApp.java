import uta.cse3310.App;
import uta.cse3310.PageManager.UserEventReply;
import java.net.InetSocketAddress;

public class MockApp extends App {

    private static final int MOCK_PORT = 9999;

    public MockApp() {
        super(new InetSocketAddress(MOCK_PORT));
        System.out.println("[MockApp] Instance created.");
    }

    @Override
    public void queueMessage(UserEventReply reply) {
        if (reply != null && reply.status != null && reply.recipients != null) {
            System.out.println("[MockApp] queueMessage called for type '" + reply.status.type + "' recipients: " + reply.recipients);
        } else {
            System.out.println("[MockApp] queueMessage called with null or incomplete reply.");
        }
    }

    @Override
    public void onStart() {
        System.out.println("[MockApp] onStart() called, but actual server start prevented.");
    }

    @Override
    public void start() {
        System.out.println("[MockApp] start() called, but actual server start prevented.");
    }


    @Override
    public void stop(int timeout) throws InterruptedException {
        System.out.println("[MockApp] stop() called.");
    }

}