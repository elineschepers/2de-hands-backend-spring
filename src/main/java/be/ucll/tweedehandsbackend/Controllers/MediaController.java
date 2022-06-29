package be.ucll.tweedehandsbackend.Controllers;

import be.ucll.tweedehandsbackend.DTOs.MediaDTO;
import be.ucll.tweedehandsbackend.Models.Media;
import be.ucll.tweedehandsbackend.Models.Offer;
import be.ucll.tweedehandsbackend.Models.Requests.Image;
import be.ucll.tweedehandsbackend.Responses.ResponseHandler;
import be.ucll.tweedehandsbackend.Services.MediaService;
import be.ucll.tweedehandsbackend.Services.OfferService;
import be.ucll.tweedehandsbackend.Util.SecureUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.LoginException;
import java.util.List;

@RestController
@RequestMapping("/api/offers/{offerUuid}/images")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private OfferService offerService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<Object> index(@PathVariable("offerUuid") String offerUuid) throws LoginException {
        // Retrieve the offer
        Offer offer = offerService.getByUuid(offerUuid);

        if(!SecureUser.hasAccesToOffer(offer)) {
            throw new LoginException("no_access");
        }

        // Retrieve the images
        List<Media> images = mediaService.getByOffer(offer);

        return ResponseHandler.generateResponse(images, MediaDTO.class);
    }

    /**
     * Uploads an image to the offer.
     *
     * @param offerUuid The uuid of the offer.
     * @param image The image to upload.
     */
    @PostMapping("")
    public ResponseEntity<Object> store(
            @PathVariable("offerUuid") String offerUuid,
            @RequestParam("images") MultipartFile image
    ) throws LoginException {
        // Retrieve the offer
        Offer offer = offerService.getByUuid(offerUuid);

        if(!SecureUser.hasAccesToOffer(offer)) {
            throw new LoginException("no_access");
        }

        // Add the image
        Media media = mediaService.add(offer, image);

        return new ResponseEntity<>(modelMapper.map(media, MediaDTO.class), null, 200);
    }

    /**
     * Updates the sort order of the images.
     * @param offerUuid The uuid of the offer.
     * @param image the UUIDs of the images. The index of the image in the list is the sort order.
     * @return An empty response.
     */
    @PatchMapping("")
    public ResponseEntity<Object> update(
            @PathVariable("offerUuid") String offerUuid,
            @RequestBody Image image // UUIDs of the images to update
    ) throws LoginException {
        // Retrieve the offer
        Offer offer = offerService.getByUuid(offerUuid);

        if(!SecureUser.hasAccesToOffer(offer)) {
            throw new LoginException("no_access");
        }

        // Get all the media
        List<Media> media = mediaService.getByOffer(offer);

        // Update the order of the images
        mediaService.updateSortOrder(media, image.getImages());

        return new ResponseEntity<>(null, null, 204);
    }

    @GetMapping("/{mediaUuid}")
    public ResponseEntity<Object> show(@PathVariable("mediaUuid") String mediaUuid) {
        Media media = mediaService.getByUuid(mediaUuid);

        return ResponseHandler.generateResponse(media, MediaDTO.class);
    }

    @DeleteMapping("/{mediaUuid}")
    public ResponseEntity<Object> destroy(
            @PathVariable("offerUuid") String offerUuid,
            @PathVariable("mediaUuid") String mediaUuid
    ) throws LoginException {
        Offer offer = offerService.getByUuid(offerUuid);

        if(!SecureUser.hasAccesToOffer(offer)) {
            throw new LoginException("no_access");
        }

        // Retrieve the media
        Media media = mediaService.getByUuid(mediaUuid);

        // Delete the media
        mediaService.delete(media);

        return new ResponseEntity<>(null, null, 204);
    }
}
