package br.com.metaro.portal.util.smb.projects.dto;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import com.hierynomus.smbj.session.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class SmbFileStreamDto implements AutoCloseable {

    private final File file;
    private final InputStream inputStream;
    private final DiskShare share;
    private final Session session;
    private final Connection connection;
    private final SMBClient client;

    public SmbFileStreamDto(File file, InputStream inputStream) {
        this(file, inputStream, null, null, null, null);
    }

    @Override
    public void close() {
        closeSafely(inputStream);
        closeSafely(file);
        closeSafely(share);
        closeSafely(session);
        closeSafely(connection);
        closeSafely(client);
    }

    private void closeSafely(AutoCloseable resource) {
        if (resource == null) {
            return;
        }

        try {
            resource.close();
        } catch (Exception ignored) {
            // A falha ao encerrar um recurso não deve impedir os demais fechamentos.
        }
    }
}
