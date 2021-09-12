package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.BadRequestException;
import com.skillbox.blogengine.dto.ImageData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class GeneralService {
    private final static Logger LOGGER = LogManager.getLogger(CaptchaService.class);

    private static final String rootFolder = "/upload";
    @Value("${blog_engine.additional.uploadedMaxFileWeight}")
    private int FILE_MAX_WEIGHT;

    public String saveImage(ImageData image) {
        String originalFileName = image.getImage().getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        if (!extension.equals("jpg") && !extension.equals("png")) {
            LOGGER.error("Got error with image {}. Image extension shoud be jpg or png", originalFileName);
            BadRequestException exception = new BadRequestException("Wrong image extension");
            exception.addErrorDescription("image", "Неправильное разрешение файла. Ожидается jpg или png.");
            throw exception;

        }
        if (image.getImage().getSize() > FILE_MAX_WEIGHT) {
            LOGGER.error("Got error with image {}. Weight is more than expected.", originalFileName);
            BadRequestException exception = new BadRequestException("Wrong image weight");
            exception.addErrorDescription("image", "Размер файла превышает допустимый размер");
            throw exception;
        }
        Path uniquePath = generateFilePath(originalFileName);
        Path currentPath = FileSystems.getDefault().getPath("").toAbsolutePath();
        Path fullPath = Path.of(currentPath.toString(), uniquePath.toString());
        LOGGER.info("Save image to path: {}", fullPath);
        fullPath.getParent().toFile().mkdirs();
        try {
            image.getImage().transferTo(new File(fullPath.toString()));
        } catch (IOException e) {
            LOGGER.error("Got error while saving image {} to path {}", originalFileName, fullPath.toString(), e);
        }

        return fullPath.toString();
    }

    private Path generateFilePath(String fileName) {
        // пример: 123e4567-e89b-42d3-a456-556642440000
        String[] pathParts = UUID.randomUUID().toString().split("-");
        Path path = Paths.get(rootFolder);
        for (int i = 0; i < 3; i++) {
            path = path.resolve(pathParts[i]);
        }
        path = path.resolve(fileName);

        return path;
    }
}
