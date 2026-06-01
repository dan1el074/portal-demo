package br.com.metaro.portal.util.smb.projects;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import com.hierynomus.smbj.session.Session;

import java.io.InputStream;

public record SmbFileStream(
        File file,
        InputStream inputStream,
        DiskShare share,
        Session session,
        Connection connection,
        SMBClient client
) {
    public SmbFileStream(File file, InputStream inputStream) {
        this(file, inputStream, null, null, null, null);
    }

    public void close() {
        try {
            inputStream.close();
        } catch (Exception ignored) {}

        try {
            file.close();
        } catch (Exception ignored) {}

        try {
            if (share != null) {
                share.close();
            }
        } catch (Exception ignored) {}

        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception ignored) {}

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ignored) {}

        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception ignored) {}
    }
}
