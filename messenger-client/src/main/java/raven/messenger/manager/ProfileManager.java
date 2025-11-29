package raven.messenger.manager;

import net.coobird.thumbnailator.Thumbnails;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.models.other.ModelGender;
import raven.messenger.models.other.ModelImage;
import raven.messenger.models.other.ModelName;
import raven.messenger.models.response.ModelProfile;
import raven.messenger.service.ServiceProfile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProfileManager {
    private static ProfileManager instance;
    private final ServiceProfile serviceProfile;
    private ModelProfile profile;

    public static ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
        }
        return instance;
    }

    private ProfileManager() {
        serviceProfile = new ServiceProfile();
    }

    public void initProfile() throws ResponseException, ConnectException {
        profile = serviceProfile.getProfile();
        FormsManager.getInstance().updateProfile(profile);
    }

    public ModelImage updateProfileImage(BufferedImage image) throws IOException, ResponseException {
        Path tempPath = Files.createTempFile("temp_", "_profile.jpg");
        File output = tempPath.toFile();
        output.deleteOnExit();
        Thumbnails.of(image)
                .scale(1f)
                .toFile(output);
        ModelImage profile = serviceProfile.updateProfileImage(output);
        initProfile();
        return profile;
    }

    public void updateProfileUser(ModelName name) throws ResponseException {
        serviceProfile.updateProfileUser(name);
        if (profile != null) {
            profile.setName(name);
        }
    }

    public void updateProfileGender(ModelGender gender) throws ResponseException {
        serviceProfile.updateProfileGender(gender);
        if (profile != null) {
            profile.setGender(gender);
            FormsManager.getInstance().updateProfile(profile);
        }
    }

    public void updateProfilePhoneNumber(String phoneNumber) throws ResponseException {
        serviceProfile.updateProfilePhoneNumber(phoneNumber);
        if (profile != null) {
            profile.setPhoneNumber(phoneNumber);
        }
    }

    public void updateProfileBios(String bio) throws ResponseException {
        serviceProfile.updateProfileBio(bio);
        if (profile != null) {
            profile.setBio(bio);
        }
    }

    public ModelProfile getProfile() {
        return profile;
    }
}
