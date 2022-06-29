/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 08/03/2022 11:31
 */

package be.ucll.tweedehandsbackend.Models.Requests;

import java.util.List;

public class Image {
    private List<String> images;

    public Image() {}

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
