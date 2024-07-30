package rw.companyz.useraccountms.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.enums.EUserStatus;
import rw.companyz.useraccountms.models.enums.EVerificationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<UserAccount, UUID> {
    Page<UserAccount> findAllByStatus(EUserStatus status, Pageable pageable);

    @Query("SELECT u FROM UserAccount u WHERE u.status <> :status AND u.emailAddress <> :email AND (:verificationStatus IS NULL OR u.verificationStatus = :verificationStatus)")
    Page<UserAccount> findAllByStatusNotAndEmailAddressNot(EUserStatus status, String email, EVerificationStatus verificationStatus, Pageable pageable);

    @Query("""
            SELECT u FROM UserAccount u
            WHERE u.id IN :ids
            AND (
                    LOWER(CONCAT(TRIM(u.firstName), ' ', TRIM(u.lastName))) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    LOWER(TRIM(u.firstName)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    LOWER(TRIM(u.lastName)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    LOWER(TRIM(u.emailAddress)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    LOWER(TRIM(u.nationality)) LIKE LOWER(CONCAT('%', :query, '%'))
                )
            """)
    List<UserAccount> findByIdIn(List<UUID> ids, String query);

    @Query(value =
            """
                SELECT DISTINCT u.*
                FROM user_account u
                         LEFT JOIN user_account_role r ON u.id = r.user_id
                WHERE (
                            LOWER(CONCAT(TRIM(u.first_name), ' ', TRIM(u.last_name))) LIKE LOWER(CONCAT('%', :query, '%')) OR
                            LOWER(TRIM(u.first_name)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                            LOWER(TRIM(u.last_name)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                            LOWER(TRIM(u.email_address)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                            LOWER(TRIM(u.phone_number)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                            LOWER(TRIM(u.service_number)) LIKE LOWER(CONCAT('%', :query, '%'))
                    )
                  AND (CAST(:status AS varchar) IS NULL OR u.status = CAST(:status AS varchar))
                  AND (CAST(:loginStatus AS varchar) IS NULL OR u.login_status = CAST(:loginStatus AS varchar))
                  AND (CAST(:roleId AS uuid) IS NULL OR r.role_id = CAST(:roleId AS uuid))
                  AND u.dtype  = 'UserAccount'
            """,
            countQuery = """
                            SELECT COUNT(DISTINCT u.*)
                            FROM user_account u
                                     LEFT JOIN user_account_role r ON u.id = r.user_id
                            WHERE (
                                        LOWER(CONCAT(TRIM(u.first_name), ' ', TRIM(u.last_name))) LIKE LOWER(CONCAT('%', :query, '%')) OR
                                        LOWER(TRIM(u.first_name)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                                        LOWER(TRIM(u.last_name)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                                        LOWER(TRIM(u.email_address)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                                        LOWER(TRIM(u.phone_number)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                                        LOWER(TRIM(u.service_number)) LIKE LOWER(CONCAT('%', :query, '%'))
                            )
                              AND (CAST(:status AS varchar) IS NULL OR u.status = CAST(:status AS varchar))
                              AND (CAST(:loginStatus AS varchar) IS NULL OR u.login_status = CAST(:loginStatus AS varchar))
                              AND (CAST(:roleId AS uuid) IS NULL OR r.role_id = CAST(:roleId AS uuid))
                              AND u.dtype  = 'UserAccount'
            """,
            nativeQuery = true)
    Page<UserAccount> searchAll(Pageable pageable, String query,String status, String loginStatus, UUID roleId);

    @Query("SELECT u FROM UserAccount u JOIN UserAccountRole r ON u.id = r.user.id WHERE r.role = :role AND u.status <> 'DELETED'")
    List<UserAccount> findAllByRole(Role role);
    Optional<UserAccount> findByEmailAddress(String emailAddress);

    @Query("SELECT u FROM UserAccount u WHERE u.nidOrPassport = :nidOrPassport")
    Optional<UserAccount> findByNidOrPassport(String nidOrPassport);
    Optional<UserAccount> findByAuthToken(String authToken);
}
