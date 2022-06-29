package be.ucll.tweedehandsbackend.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Type;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "media", indexes = {
    @Index(columnList = "id", name = "media_id_idx", unique = true),
    @Index(columnList = "uuid", name = "media_uuid_idx", unique = true)
})
public class Media {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", length = 36, nullable = false, unique = true)
    private String uuid = UUID.randomUUID().toString();

    private String fileName;

    private String fileType;

    private Long fileSize;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Map<Integer, String> responsiveImages;

    private short sortOrder;

    private boolean processed = false;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    private Offer offer;

    public Media() {}

    public Media(MultipartFile file) {
        this.fileName = file.getOriginalFilename();
        this.fileType = file.getContentType();
        this.fileSize = file.getSize();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Map<Integer, String> getResponsiveImages() {
        return responsiveImages;
    }

    public void setResponsiveImages(HashMap<Integer, String> responsiveImages) {
        this.responsiveImages = responsiveImages;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(short order) {
        this.sortOrder = order;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /*public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }*/

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public static String buildUrl(String uuid, String file) {
        return "/storage/uploads/" + uuid + "/" + file;
    }
}
