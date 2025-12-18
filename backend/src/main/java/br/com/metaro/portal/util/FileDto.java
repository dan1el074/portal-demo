package br.com.metaro.portal.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FileDto {
    private Long id;
    private String name;
    private String path;
    private FileType type;
    private Long post_id;

    public FileDto(File file) {
        id = file.getId();
        name = file.getName();
        path = file.getPath();
        type = file.getType();

        if (file.getPost() != null) post_id = file.getPost().getId();
    }
}
