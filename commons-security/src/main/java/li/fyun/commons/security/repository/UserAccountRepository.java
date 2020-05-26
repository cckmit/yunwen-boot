package li.fyun.commons.security.repository;

import li.fyun.commons.core.jpa.BaseRepository;
import li.fyun.commons.security.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAccountRepository extends BaseRepository<UserAccount, Long> {

    long countByUsername(String username);

    long countByMobile(String mobile);

    long countByEmail(String email);

    UserAccount findByUsername(String username);

    UserAccount findByEmail(String email);

    UserAccount findByMobile(String mobile);

    UserAccount findByUsernameOrMobileOrEmail(String username, String mobile, String email);

    @Query("update UserAccount u set u.accountDisabled = false where u.id = ?1")
    @Modifying
    void enable(Long id);

    @Query("update UserAccount u set u.accountDisabled = true where u.id = ?1")
    @Modifying
    void disable(Long id);

    String QUERY_FIND_BY_KEYWORD = "select a from UserAccount a " +
            "where a.username like :keyword or a.nickname like :keyword " +
            "or a.mobile like :keyword or a.email like :keyword";

    @Query(QUERY_FIND_BY_KEYWORD)
    List<UserAccount> findByKeyword(@Param("keyword") String keyword);

    @Query(QUERY_FIND_BY_KEYWORD)
    Page<UserAccount> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
