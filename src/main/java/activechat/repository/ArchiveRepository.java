package activechat.repository;

import activechat.model.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("archiveRepository")
public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    List<Archive> findAllByWhiteOrBlack(String white, String black);

}
