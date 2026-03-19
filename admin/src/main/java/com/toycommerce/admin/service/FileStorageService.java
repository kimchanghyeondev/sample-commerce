package com.toycommerce.admin.service;

import com.toycommerce.common.entity.attachment.Attachment;
import com.toycommerce.common.entity.enums.EntityStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8082}")
    private String baseUrl;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        try {
            uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            log.info("파일 저장 디렉터리 생성: {}", uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 디렉터리를 생성할 수 없습니다.", e);
        }
    }

    /**
     * Base64 인코딩된 이미지 저장
     * 
     * @param base64Data Base64 인코딩된 이미지 데이터 (data:image/jpeg;base64,... 형식 지원)
     * @param originalFileName 원본 파일명
     * @return 저장된 Attachment 엔티티
     */
    public Attachment saveBase64Image(String base64Data, String originalFileName) {
        try {
            // Base64 데이터 파싱
            String contentType = "image/jpeg";
            String base64Content = base64Data;
            
            // data:image/jpeg;base64,... 형식인 경우 파싱
            if (base64Data.contains(",")) {
                String[] parts = base64Data.split(",");
                if (parts[0].contains(";base64")) {
                    contentType = parts[0].replace("data:", "").replace(";base64", "");
                }
                base64Content = parts[1];
            }

            // Base64 디코딩
            byte[] imageBytes = Base64.getDecoder().decode(base64Content);

            // 파일 확장자 결정
            String extension = getExtensionFromContentType(contentType);
            if (extension == null && originalFileName != null) {
                extension = StringUtils.getFilenameExtension(originalFileName);
            }
            if (extension == null) {
                extension = "jpg";
            }

            // 고유 파일명 생성
            String storedName = UUID.randomUUID().toString() + "." + extension;

            // 날짜별 디렉터리 생성
            String dateDir = java.time.LocalDate.now().toString().replace("-", "/");
            Path targetDir = uploadPath.resolve(dateDir);
            Files.createDirectories(targetDir);

            // 파일 저장
            Path filePath = targetDir.resolve(storedName);
            Files.write(filePath, imageBytes);

            // 파일 URL 생성
            String relativePath = dateDir + "/" + storedName;
            String fileUrl = baseUrl + "/uploads/" + relativePath;

            log.info("이미지 저장 완료: {} -> {}", originalFileName, filePath);

            return Attachment.builder()
                    .originalName(originalFileName != null ? originalFileName : storedName)
                    .storedName(storedName)
                    .filePath(relativePath)
                    .fileUrl(fileUrl)
                    .contentType(contentType)
                    .fileSize((long) imageBytes.length)
                    .fileExtension(extension)
                    .status(EntityStatus.ACTIVE)
                    .build();

        } catch (IOException e) {
            log.error("이미지 저장 실패: {}", originalFileName, e);
            throw new RuntimeException("이미지 저장에 실패했습니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("Base64 디코딩 실패: {}", originalFileName, e);
            throw new RuntimeException("잘못된 이미지 데이터입니다.", e);
        }
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(Attachment attachment) {
        try {
            Path filePath = uploadPath.resolve(attachment.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 완료: {}", filePath);
            }
        } catch (IOException e) {
            log.warn("파일 삭제 실패: {}", attachment.getFilePath(), e);
        }
    }

    /**
     * Content-Type으로부터 확장자 추출
     */
    private String getExtensionFromContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            case "image/svg+xml" -> "svg";
            case "image/bmp" -> "bmp";
            default -> null;
        };
    }

    public Path getUploadPath() {
        return uploadPath;
    }
}

