package br.com.metaro.portal.util.smb.files;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FileDto {
    private Long id;
    private String title;
    private String fileName;

    public FileDto (File file) {
        id = file.getId();
        title = file.getTitle();
        fileName = file.getFileName();
    }
}
