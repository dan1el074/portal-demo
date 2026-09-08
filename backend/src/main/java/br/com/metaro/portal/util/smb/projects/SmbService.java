package br.com.metaro.portal.util.smb.projects;

import br.com.metaro.portal.util.smb.SmbConnection;
import br.com.metaro.portal.util.smb.projects.dto.SmbFileStreamDto;
import br.com.metaro.portal.util.smb.projects.dto.ProjectSearchResultDto;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

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
    @Value("${app.smb.files-path}")
    private String filesPath;
    @Value("${app.smb.legacy-projects-path}")
    private String legacyProjectsPath;

    private static final Pattern VERSION_SUFFIX = Pattern.compile(
            "_\\d{2}\\.\\d{2}\\.\\d{2}(?:\\d{2})?_\\d{2}\\.\\d{2}\\.pdf$", Pattern.CASE_INSENSITIVE);
    private final SmbConnection oldProjects = new SmbConnection();
    private final SmbConnection newProjects = new SmbConnection();

    @PreDestroy
    public void close() {
        oldProjects.close();
        newProjects.close();
    }

    DiskShare getShare(ProjectSource source) throws Exception {
        if (source == ProjectSource.NEW) {
            return newProjects.getShare(hostname, username, password, projectsPath);
        }
        return oldProjects.getShare(hostname, username, password, legacyProjectsPath);
    }

    public List<ProjectSearchResultDto> searchProject(String term) {
        List<ProjectSearchResultDto> results = new ArrayList<>();
        term = term.trim();
        if (term.isEmpty()) return results;

        try {
            for (ProjectSource source : ProjectSource.values()) {
                DiskShare share = getShare(source);
                String folder = source == ProjectSource.NEW ? "" : getProjectFolder(term);
                if (!folder.isEmpty() && !share.folderExists(folder)) continue;
                for (var file : share.list(folder)) {
                    String name = file.getFileName();
                    if ((file.getFileAttributes() & 0x10) == 0 && matchesProject(name, term, source)) {
                        results.add(new ProjectSearchResultDto(name, source));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to search SMB files", e);
        }

        return results;
    }

    static boolean matchesProject(String name, String term, ProjectSource source) {
        return name != null && name.toLowerCase(Locale.ROOT).endsWith(".pdf")
                && name.regionMatches(true, 0, term, 0, term.length())
                && (source == ProjectSource.OLD || !VERSION_SUFFIX.matcher(name).find());
    }

    public SmbFileStreamDto getProjectPdfStream(String fileName, ProjectSource source) {
        if (fileName.contains("/") || fileName.contains("\\") || fileName.contains(":")
                || !fileName.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            return null;
        }
        try {
            DiskShare share = getShare(source);
            String folder = source == ProjectSource.NEW ? "" : getProjectFolder(fileName);
            String fullPath = folder.isEmpty() ? fileName : folder + "/" + fileName;

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

            return new SmbFileStreamDto(file, inputStream);

        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve SMB PDF", e);
        }
    }

    public SmbFileStreamDto getFileStream(String fileName) {
        try {
            SMBClient tempClient = new SMBClient();
            Connection tempConnection = tempClient.connect(hostname);

            AuthenticationContext ac = new AuthenticationContext(username, password.toCharArray(), null);
            Session tempSession = tempConnection.authenticate(ac);
            DiskShare tempShare = (DiskShare) tempSession.connectShare(filesPath);

            String fullPath = "TI/Outros/portal/" + fileName;

            if (!tempShare.fileExists(fullPath)) {
                tempShare.close();
                tempSession.close();
                tempConnection.close();
                tempClient.close();

                return null;
            }

            File file = tempShare.openFile(
                    fullPath,
                    EnumSet.of(AccessMask.GENERIC_READ),
                    null,
                    SMB2ShareAccess.ALL,
                    SMB2CreateDisposition.FILE_OPEN,
                    null
            );

            InputStream inputStream = file.getInputStream();

            return new SmbFileStreamDto(file, inputStream, tempShare, tempSession, tempConnection, tempClient);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve SMB media", e);
        }
    }

    public MediaType resolveMediaType(String fileName) {
        String lower = fileName.toLowerCase();

        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".pdf")) return MediaType.APPLICATION_PDF;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (lower.endsWith(".webp")) return MediaType.parseMediaType("image/webp");

        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private String getProjectFolder(String projectName) {
        String baseProject = projectName.split("[-_., ]")[0].trim();

        if (!baseProject.matches("\\d+")) return "";

        baseProject = baseProject.replaceFirst("^0+(?!$)", "");
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
