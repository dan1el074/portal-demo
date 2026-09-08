package br.com.metaro.portal.util.smb;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

public class SmbConnection implements AutoCloseable {
    private SMBClient client;
    private Connection connection;
    private Session session;
    private DiskShare share;

    public synchronized DiskShare getShare(String hostname, String username, String password, String shareName) throws Exception {
        if (share == null || !share.isConnected()) {
            close();
            try {
                client = new SMBClient();
                connection = client.connect(hostname);
                session = connection.authenticate(new AuthenticationContext(username, password.toCharArray(), null));
                share = (DiskShare) session.connectShare(shareName);
            } catch (Exception e) {
                close();
                throw e;
            }
        }
        return share;
    }

    @Override
    public synchronized void close() {
        try { if (share != null) share.close(); } catch (Exception ignored) {}
        try { if (session != null) session.close(); } catch (Exception ignored) {}
        try { if (connection != null) connection.close(); } catch (Exception ignored) {}
        try { if (client != null) client.close(); } catch (Exception ignored) {}
        share = null;
        session = null;
        connection = null;
        client = null;
    }
}
