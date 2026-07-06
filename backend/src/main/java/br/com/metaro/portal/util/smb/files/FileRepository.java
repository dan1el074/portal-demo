package br.com.metaro.portal.util.smb.files;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query(value = """
        SELECT f.id, f.title, f.file_name
        FROM tb_file f
        ORDER BY f.created_at DESC
        LIMIT 3
    """, nativeQuery = true)
    public List<FileDto> findTop3ForHome();
}
