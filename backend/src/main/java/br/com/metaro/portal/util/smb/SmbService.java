package br.com.metaro.portal.util.smb;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
public class SmbService {
    @Value("${app.smb.hostname}")
    private String hostname;
    @Value("${app.smb.username}")
    private String username;
    @Value("${app.smb.password}")
    private String password;
    @Value("${app.smb.projects-path}")
    private String projectsPath;

    private final Object lock = new Object();
    private SMBClient client;
    private Connection connection;
    private Session session;
    private DiskShare share;

    @PreDestroy
    public void close() {
        try { if (share != null) share.close(); } catch (Exception ignored) {}
        try { if (session != null) session.close(); } catch (Exception ignored) {}
        try { if (connection != null) connection.close(); } catch (Exception ignored) {}
        try { if (client != null) client.close(); } catch (Exception ignored) {}
    }

    private DiskShare getShare() throws Exception {
        synchronized (lock) {
            if (share == null || !share.isConnected()) {
                reconnect();
            }
            return share;
        }
    }

    private void reconnect() throws Exception {
        close();

        client = new SMBClient();
        connection = client.connect(hostname);

        AuthenticationContext ac =
                new AuthenticationContext(username, password.toCharArray(), null);

        session = connection.authenticate(ac);
        share = (DiskShare) session.connectShare(projectsPath);
    }

    public List<String> searchProject(String term) {
        List<String> results = new ArrayList<>();
        String folder = getProjectFolder(term);

        try {
            DiskShare share = getShare();

            for (var file : share.list(folder)) {
                String name = file.getFileName();

                if (name == null) continue;

                if (name.toLowerCase().endsWith(".pdf") && name.startsWith(term)) {
                    results.add(name);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar arquivos SMB", e);
        }

        return results;
    }

    public SmbFileStream getProjectPdfStream(String fileName) {
        try {
            DiskShare share = getShare();
            String fullPath = getProjectFolder(fileName) + "/" + fileName;

            if (!share.fileExists(fullPath)) return null;

            File file = share.openFile(
                    fullPath,
                    EnumSet.of(com.hierynomus.msdtyp.AccessMask.GENERIC_READ),
                    null,
                    com.hierynomus.mssmb2.SMB2ShareAccess.ALL,
                    com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OPEN,
                    null
            );

            InputStream inputStream = file.getInputStream();

            return new SmbFileStream(file, inputStream);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter PDF SMB", e);
        }
    }

    private String getProjectFolder(String projectName) {
        String baseProject = projectName.split("[-_., ]")[0].trim();

        if (!baseProject.matches("\\d+")) return "";

        baseProject = String.valueOf(Integer.parseInt(baseProject));
        String folder = "000";

        if (baseProject.length() == 4) {
            folder = "0" + baseProject.charAt(0) + "000";
        }
        if (baseProject.length() == 5) {
            folder = "%c%c000".formatted(baseProject.charAt(0), baseProject.charAt(1));
        }

        return folder;
    }
}
