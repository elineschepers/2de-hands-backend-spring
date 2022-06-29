package be.ucll.tweedehandsbackend.Services;

import be.ucll.tweedehandsbackend.Exceptions.RecordNotFoundException;
import be.ucll.tweedehandsbackend.Jobs.ConvertMediaImages;
import be.ucll.tweedehandsbackend.Jobs.DeleteMediaFiles;
import be.ucll.tweedehandsbackend.Models.Media;
import be.ucll.tweedehandsbackend.Models.Offer;
import be.ucll.tweedehandsbackend.Repositories.MediaRepository;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    private ConvertMediaImages convertMediaImages;

    @Autowired
    private DeleteMediaFiles deleteMediaFiles;

    /**
     * Get a Media object (by id) from the database
     *
     * @param id the id of the Media object
     * @return the requested Media object
     */
    public Media getById(Long id) {
        return mediaRepository.getById(id);
    }

    /**
     * Get a Media object (by UUID) from the database
     *
     * @param uuid the UUID of the media object
     * @return the requested Media object
     * @throws RecordNotFoundException if no media exists with the given uuid
     */
    public Media getByUuid(String uuid) {
        return mediaRepository.getByUuid(uuid);
    }

    /**
     * Get all media associated to an offer
     *
     * @param offer the offer to get the Media from
     * @return a list of Media objects attached to the offer
     */
    public List<Media> getByOffer(Offer offer) {
        if (offer == null) {
            throw new RecordNotFoundException("offer.notFound");
        }

        return mediaRepository.findAllByOfferIdOrderBySortOrder(offer.getId());
    }

    /**
     * Adds a file to an offer and creates a media object
     *
     * @param offer the offer object to add the files to
     * @param file the files to add
     * @return the created Media object
     */
    public Media add(Offer offer, MultipartFile file) {
        Media media = new Media(file);
        media.setOffer(offer);
        mediaRepository.save(media);

        try {
            // Base64 encode the file
            String base64EncodedImg = Base64.getEncoder().encodeToString(file.getBytes());

            // Get id
            Long id = media.getId();

            // Dispatch the job to convert the image
            jobScheduler.enqueue(() -> convertMediaImages.dispatch(id, base64EncodedImg));
        } catch (IOException ignored) {}

        return media;
    }

    /**
     * Updates the sort order of the media. The order is based on the order of uuid list.
     *
     * @param mediaList The list of media to update.
     * @param uuidOrderList The new order of the uuid list.
     */
    public void updateSortOrder(List<Media> mediaList, List<String> uuidOrderList) {
        if (mediaList == null || uuidOrderList == null) {
            return;
        }

        for (Media media : mediaList) {
            if (media == null) {
                continue;
            }

            String uuid = media.getUuid();

            // The media must be in the list
            if (!uuidOrderList.contains(uuid)) {
                return;
            }

            // Get the index of the media in the uuidOrderList
            int order = uuidOrderList.indexOf(uuid);
            // Update the sort order
            media.setSortOrder((short) order);
        }

        // Save the media
        mediaRepository.saveAll(mediaList);
    }

    /**
     * Delete a given Media object from the database and
     * delete the files attached to it from the server.
     *
     * @param media the media object to delete
     */
    public void delete(Media media) {
        if (media == null) {
            return;
        }

        String uuid = media.getUuid();

        mediaRepository.delete(media);

        // Delete the files from the filesystem associated with the media
        jobScheduler.enqueue(() -> deleteMediaFiles.dispatch(uuid));
    }

}
