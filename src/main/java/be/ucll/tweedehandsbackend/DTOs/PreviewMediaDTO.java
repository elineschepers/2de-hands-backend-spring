package be.ucll.tweedehandsbackend.DTOs;

import be.ucll.tweedehandsbackend.Models.Media;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;

/**
 * This class is used to transfer media objects between the frontend and the backend.
 * It contains the minimal information needed to display a media object.
 * @author Sigfried
 */
public class PreviewMediaDTO {

    @JsonIgnore
    private String uuid;

    private HashMap<Integer, String> srcSet = new HashMap<>();

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSrcSet() {
        StringBuilder srcSetString = new StringBuilder();

        for (Integer size : srcSet.keySet()) {
            srcSetString
                    .append(Media.buildUrl(this.uuid, srcSet.get(size)))
                    .append(" ")
                    .append(size)
                    .append("w, ");
        }

        // Check if srcset is empty and return empty string if it is
        if (srcSetString.length() == 0) {
            return null;
        }

        // Remove last two characters
        srcSetString.delete(srcSetString.length() - 2, srcSetString.length());

        return srcSetString.toString();
    }

    public void setSrcSet(HashMap<Integer, String> srcSet) {
        this.srcSet = Objects.requireNonNullElseGet(srcSet, HashMap::new);
    }

    @JsonProperty("src")
    public String src() {
        return Media.buildUrl(this.uuid, "main.jpg");
    }

    @JsonProperty("thumb")
    public String thumb() {
        return Media.buildUrl(this.uuid, "thumb.jpg");
    }

}
