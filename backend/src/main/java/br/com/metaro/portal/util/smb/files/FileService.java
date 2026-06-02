package br.com.metaro.portal.util.smb.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    @Cacheable("files")
    public List<FileDto> getFiles() {
        return fileRepository
            .findAll(PageRequest.of(0, 3, Sort.by("createdAt").descending()))
            .stream()
            .map(FileDto::new)
            .toList();
    }
}
