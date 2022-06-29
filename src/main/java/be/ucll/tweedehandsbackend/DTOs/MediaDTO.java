/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 02/03/2022 20:17
 */

package be.ucll.tweedehandsbackend.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used to transfer media objects between the frontend and the backend.
 * This should ONLY be used for displaying media object for an admin or the author of the media object.
 * It extends the PreviewMediaDTO class.
 * @author Sigfried
 */
public class MediaDTO extends PreviewMediaDTO {

    @JsonProperty("id")
    private String uuid;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private short sortOrder;

    private boolean processed = false;

    public MediaDTO() {}

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

    public short getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(short sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
