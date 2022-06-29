package be.ucll.tweedehandsbackend.Jobs;

import be.ucll.tweedehandsbackend.Models.Media;
import be.ucll.tweedehandsbackend.Repositories.MediaRepository;
import org.jobrunr.jobs.annotations.Job;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.*;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class ConvertMediaImages {

    @Autowired
    private MediaRepository mediaRepository;

    private static final ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();
    private static final ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

    @Value("${storage.location}")
    private String StoragePath;

    /**
     * Main method to process the images
     *
     * @param mediaId The uuid of the media to process
     * @param base64EncodedImg The base64 encoded image
     * @throws IOException If the image cannot be written
     */
    @Job
    @Transactional
    public void dispatch(Long mediaId, String base64EncodedImg) throws IOException {
        LoggerFactory.getLogger(ConvertMediaImages.class).info("Processing media with id: " + mediaId);

        Media media;
        try {
            // Get the media
            media = mediaRepository.getById(mediaId);
        } catch (Exception e) {
            LoggerFactory.getLogger(ConvertMediaImages.class).error("Error processing media with id: " + mediaId);
            return;
        }

        // Decode the base64 encoded image
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(
            Base64.getDecoder().decode(base64EncodedImg)
        ));

        // Set default image write parameters
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        // Create the directory recursively
        Files.createDirectories(Paths.get(StoragePath, media.getUuid()));

        // Process the image and store it
        img = processImage(img, media.getUuid());

        // Create a cropped image of the original image
        createCroppedImage(img, media.getUuid());

        // Calculate the ratio of the image
        double ratio = (double) img.getHeight() / img.getWidth();

        // Get the calculated image sizes
        ArrayList<Integer> targetWidths = calculateImageWidths(img, media.getFileSize(), ratio);

        // Generate the responsive images
        HashMap<Integer, String> responsiveImages = generateResponsiveImage(img, targetWidths, media.getUuid(), ratio);

        // Set the generated images to the media
        media.setResponsiveImages(responsiveImages);
        media.setProcessed(true);
        mediaRepository.save(media);
    }

    /**
     * Process the original image, resize it if needed and write it to the storage
     * This image will be used as the main image for all the other converted images
     *
     * @param img The original image
     * @param uuid The uuid of the media
     * @return The processed image
     * @throws IOException If the image cannot be written
     */
    private BufferedImage processImage(BufferedImage img, String uuid) throws IOException {
        int targetWidth = img.getWidth();
        int targetHeight = img.getHeight();

        // Resize to max height of 2500 px
        if (img.getHeight() > 2500) {
            // Calculate the ratio
            double ratio = img.getHeight() / 2500.0;

            // Calculate the new width and height
            targetWidth = (int) (img.getWidth() / ratio);
            targetHeight = (int) (img.getHeight() / ratio);
        }

        BufferedImage processedImg = createScaledImage(targetHeight, targetWidth, img);

        // Save the image
        imageWriteParam.setCompressionQuality(0.75f);
        writeFile(processedImg, Paths.get(StoragePath, uuid, "/main.jpg"));

        return processedImg;
    }

    /**
     * Create a cropped thumbnail of the original image
     *
     * @param img The original image
     * @param uuid The uuid of the media
     * @throws IOException If the image cannot be written
     */
    private void createCroppedImage(BufferedImage img, String uuid) throws IOException {
        // Resize image if it is too big
        if(img.getWidth() > 400 && img.getHeight() > 400)
            img = resize(img);
        // The target width and height is 400 px if the image is bigger than 400 px
        // Otherwise the target width and height is the same as the original image1
        int targetX = Math.min(img.getWidth(), 400);
        int targetY = Math.min(img.getHeight(), 400);

        // Now get the smallest of the two. This is the target width and height
        int targetXY = Math.min(targetX, targetY);

        // Starting point of the crop
        int x = 0, y = 0;

        // If the width is larger than 400 px, crop it from the center
        if (img.getWidth() > 400) {
            x = (img.getWidth() - targetXY) / 2;
        }

        // If the height is larger than 400 px, crop it from the center
        if (img.getHeight() > 400) {
            y = (img.getHeight() - targetXY) / 2;
        }

        // Crop the image
        BufferedImage croppedImage = img.getSubimage(x, y, targetXY, targetXY);

        // Save cropped thumbnail
        imageWriteParam.setCompressionQuality(0.55f);
        writeFile(croppedImage, Paths.get(StoragePath, uuid, "/thumb.jpg"));
    }

    /**
     *  Rescale images too fit in the cards if they are too big
     *
     * @param img The original image
     * @return the resized image
     */
    public BufferedImage resize(BufferedImage img) {
        double newH = 400;
        double newW = 400;
        if (img.getHeight() > img.getWidth()) {
            newH = (newW / ((double) img.getWidth())) * img.getHeight();
        } else {
            newW = (newH / (double) img.getHeight()) * img.getWidth();
        }
        Image tmp = img.getScaledInstance((int) newW, (int) newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage((int) newW, (int) newH, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    /**
     * Generate the responsive images
     *
     * @param img The image to generate the responsive images from
     * @param targetWidths The widths of the responsive images
     * @param uuid The uuid of the media
     * @param ratio The ratio of the image
     * @return A HashMap containing the responsive images
     * @throws IOException If the image cannot be written
     */
    private HashMap<Integer, String> generateResponsiveImage(BufferedImage img, ArrayList<Integer> targetWidths, String uuid, double ratio) throws IOException {
        // Create an HashMap to store the filenames of the generated images
        HashMap<Integer, String> images = new HashMap<>();

        imageWriteParam.setCompressionQuality(0.70f);

        // For each target width
        for (int targetWidth : targetWidths) {
            BufferedImage processedImg = createScaledImage((int) (targetWidth * ratio), targetWidth, img);

            // Store file
            String filename = targetWidth + "w.jpg";
            writeFile(processedImg, Paths.get(StoragePath,uuid, filename));

            // Add filename to arraylist
            images.put(targetWidth, filename);
        }

        return images;
    }

    /**
     * Calculate the image widths to generate
     *
     * @param img The image to calculate the widths for
     * @param predictedFileSize The current file size of the image
     * @param ratio The ratio of the image
     * @return An arraylist of the calculated image widths
     */
    public ArrayList<Integer> calculateImageWidths(BufferedImage img, double predictedFileSize, double ratio) {
        ArrayList<Integer> targetWidths = new ArrayList<>();

        // Calculate the price per pixel
        double pixelPrice = predictedFileSize / (img.getHeight() * img.getWidth());

        while (true) {
            predictedFileSize *= 0.55;

            int newWidth = (int) Math.floor(Math.sqrt((predictedFileSize / pixelPrice) / ratio));

            if (newWidth < 20 || predictedFileSize < (1024 * 10)) {
                return targetWidths;
            }

            targetWidths.add(newWidth);
        }
    }

    /**
     * Create a scaled image with the given width and height
     *
     * @param targetHeight The target height of the image
     * @param targetWidth The target width of the image
     * @param img The image to scale
     * @return The newly scaled image
     */
    private BufferedImage createScaledImage(int targetHeight, int targetWidth, BufferedImage img) {
        // Create a scaled image
        Image rescaledImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);

        // Create a buffered image
        BufferedImage processedImg = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        processedImg.getGraphics().drawImage(rescaledImg, 0, 0, null);

        return processedImg;
    }

    /**
     * Write the image to a file
     *
     * @param img The image to write
     * @param path The path to write the image to
     */
    private void writeFile(BufferedImage img, Path path) throws IOException {
        imageWriter.setOutput(ImageIO.createImageOutputStream(path.toFile()));
        imageWriter.write(null, new IIOImage(img, null, null), imageWriteParam);
    }
}
