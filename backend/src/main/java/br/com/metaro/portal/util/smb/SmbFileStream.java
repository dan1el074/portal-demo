package br.com.metaro.portal.util.smb;

import com.hierynomus.smbj.share.File;

import java.io.InputStream;

public record SmbFileStream(File file, InputStream inputStream) {
    public void close() {
        try {
            inputStream.close();
        } catch (Exception ignored) {}

        try {
            file.close();
        } catch (Exception ignored) {}
    }
}
