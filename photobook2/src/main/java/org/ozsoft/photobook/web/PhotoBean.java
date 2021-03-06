package org.ozsoft.photobook.web;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.ozsoft.photobook.entities.Photo;
import org.ozsoft.photobook.repositories.PhotoRepository;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;

@ManagedBean
@RequestScoped
public class PhotoBean implements Serializable {

    private static final int THUMBNAIL_SIZE = 150;

    private static final long serialVersionUID = -7768597734064601558L;

    private static final Logger LOG = Logger.getLogger(PhotoBean.class);

    @ManagedProperty(value = "#{photoRepository}")
    private PhotoRepository photoRepository;

    public void setPhotoRepository(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public List<Photo> getPhotos() {
        return photoRepository.findAll();
    }

    public void handleFileUpload(FileUploadEvent e) {
        LOG.debug("Adding photo");
        UploadedFile file = e.getFile();
        String filename = file.getFileName();
        Photo photo = new Photo();
        InputStream is = null;
        try {
            LOG.debug("Reading image file");
            is = new BufferedInputStream(file.getInputstream());
            byte[] content = IOUtils.toByteArray(is);
            is.close();
            try {
                LOG.debug("Extracting EXIF metadata");
                Metadata metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(file.getInputstream()));
                Directory dir = metadata.getDirectory(ExifDirectory.class);
                try {
                    if (dir.containsTag(ExifDirectory.TAG_ORIENTATION)) {
                        int orientation = dir.getInt(ExifDirectory.TAG_ORIENTATION);
                        LOG.debug("orientation = " + orientation);
                    } else {
                        LOG.debug("No orientation set");
                    }
                } catch (MetadataException e1) {
                    LOG.error(e1);
                }
            } catch (ImageProcessingException e1) {
                LOG.error(e1);
            }
            photo.setContent(content);
            photo.setThumbnail(createThumbnail(content, THUMBNAIL_SIZE));
            LOG.debug("Storing photo in the database");
            photoRepository.store(photo);
            LOG.info("Photo added with ID " + photo.getId());
        } catch (IOException ex) {
            LOG.error(String.format("Could not set photo content from file '%s'", filename), ex);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static byte[] createThumbnail(byte[] content, int size) {
        byte[] b = null;
        try {
            LOG.debug("Creating thumbnail");
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
            int orgWidth = image.getWidth();
            int orgHeight = image.getHeight();
            int width = (orgWidth > orgHeight) ? size : (int) (size * ((double) orgWidth / (double) orgHeight));
            int height = (orgHeight > orgWidth) ? size : (int) (size * ((double) orgHeight / (double) orgWidth));
            BufferedImage thumbnail = new BufferedImage((int) width, (int) height, image.getType());
            Graphics2D g = thumbnail.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image, 0, 0, width, height, 0, 0, orgWidth, orgHeight, null);
            g.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "jpg", baos);
            b = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            LOG.error("Could not create thumbnail", e);
        }
        return b;
    }

}
