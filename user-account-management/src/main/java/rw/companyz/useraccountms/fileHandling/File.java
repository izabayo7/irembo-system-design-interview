package rw.companyz.useraccountms.fileHandling;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import rw.companyz.useraccountms.audits.UserDateAudit;
import rw.companyz.useraccountms.models.enums.EFileSizeType;
import rw.companyz.useraccountms.models.enums.EFileStatus;

import java.io.Serial;
import java.util.UUID;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "files")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class File extends UserDateAudit {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="path", nullable = false)
    private String path;

    @Transient
    private String url;

    @Column(name="size", nullable = false)
    private int size;

    @Column(name="size_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EFileSizeType sizeType;

    @Column(name="type", nullable = false)
    private String type;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EFileStatus status;

}

