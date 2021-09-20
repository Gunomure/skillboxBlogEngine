package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.BadRequestException;
import com.skillbox.blogengine.dto.ImageData;
import com.skillbox.blogengine.dto.ProfileData;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class GeneralService {
    private final static Logger LOGGER = LogManager.getLogger(CaptchaService.class);

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder encoder;

    private static final String rootFolder = "/upload";
    @Value("${blog_engine.additional.uploadedMaxFileWeight}")
    private int FILE_MAX_WEIGHT;
    @Value("${blog_engine.additional.passwordMinLength}")
    private int PASSWORD_MIN_LENGTH;

    public GeneralService(UserRepository userRepository, CloudinaryService cloudinaryService, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.encoder = encoder;
    }

    public String saveImage(ImageData image) {
        return saveImage(image.getImage(), false);
    }

    private String saveImage(MultipartFile image, boolean cut) {
        String savedImageUrl;
        String originalFileName = image.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        if (!extension.equals("jpg") && !extension.equals("png")) {
            LOGGER.error("Got error with image {}. Image extension should be jpg or png", originalFileName);
            BadRequestException exception = new BadRequestException("Wrong image extension");
            exception.addErrorDescription("photo", "Неправильное разрешение файла. Ожидается jpg или png.");
            throw exception;

        }
        if (image.getSize() > FILE_MAX_WEIGHT) {
            LOGGER.error("Got error with image {}. Weight is more than expected.", originalFileName);
            BadRequestException exception = new BadRequestException("Wrong image weight");
            exception.addErrorDescription("photo", "Размер файла превышает допустимый размер 5Мб");
            throw exception;
        }

        Path uniquePath = generateFilePath();
        Path currentPath = FileSystems.getDefault().getPath("").toAbsolutePath();
        Path fullPath = Path.of(currentPath.toString(), uniquePath.toString(), originalFileName);
        savedImageUrl = fullPath.toString();
        LOGGER.info("Save image to path: {}", fullPath);
        fullPath.getParent().toFile().mkdirs();
        try {
            image.transferTo(new File(fullPath.toString()));
            savedImageUrl = cloudinaryService.uploadImage(fullPath.toString(), uniquePath.toString(), cut);
            Files.delete(fullPath); //после загрузки файла в облако, освобождаем место на диске
        } catch (IOException e) {
            LOGGER.error("Got error while saving image {} to path {}", originalFileName, fullPath.toString(), e);
        }
        LOGGER.info("Image has been saved into cloudinary, url:\n" + savedImageUrl);

        return savedImageUrl;
    }

    private Path generateFilePath() {
        // пример: 123e4567-e89b-42d3-a456-556642440000
        String[] pathParts = UUID.randomUUID().toString().split("-");
        Path path = Paths.get(rootFolder);
        for (int i = 0; i < 3; i++) {
            path = path.resolve(pathParts[i]);
        }

        return path;
    }

    public User updateProfile(String currentUserEmail, ProfileData profileData) {
        if (profileData.getPassword() != null && profileData.getPassword().length() < PASSWORD_MIN_LENGTH) {
            BadRequestException exception = new BadRequestException("Password is too short");
            exception.addErrorDescription("password", "Пароль короче 6-ти символов");
            throw exception;
        }

        Optional<User> user = userRepository.findByEmail(profileData.getEmail());
        if (!currentUserEmail.equals(profileData.getEmail()) && user.isPresent()) {
            BadRequestException exception = new BadRequestException("User already exists");
            exception.addErrorDescription("email", "Этот e-mail уже зарегистрирован");
            throw exception;
        }

        User currentUser = userRepository.findByEmail(currentUserEmail).get();
        if (profileData.getPassword() != null)
            currentUser.setPassword(encoder.encode(profileData.getPassword()));
        if (profileData.getName() != null)
            currentUser.setName(profileData.getName());
        if (profileData.getEmail() != null)
            currentUser.setEmail(profileData.getEmail());
        if (profileData.isRemovePhoto())
            currentUser.setPhoto(null);

        userRepository.save(currentUser);

        return currentUser;
    }

    public void updateProfile(String currentUserEmail, ProfileData profileData, MultipartFile image) {
        User user = updateProfile(currentUserEmail, profileData);
        String imagePath = saveImage(image, true);
        user.setPhoto(imagePath);
        userRepository.save(user);
    }
}
