package com.example.library_book.Image;

import com.example.library_book.Book.Book;
import com.example.library_book.Exception.FindByNoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {
    @Autowired
    public ImageRepository imageRepository;
    @Value("${uploading.videoSaveFolder}")
    private String FOLDER_PATH;
    public void addImages(List<MultipartFile> fileList, Book book) throws IOException {
        if (fileList != null && !fileList.isEmpty()) {
            List<Image> images = new ArrayList<>();

            for (MultipartFile file : fileList) {
                String randomImageName = LocalDateTime.now().toString();
                String pathImg = FOLDER_PATH + "/" + randomImageName;
                file.transferTo(new File(pathImg));
                Image image = Image.builder()
                        .bookId(book)
                        .img(randomImageName)
                        .pathIma(pathImg)
                        .build();
                images.add(image);
            }

            imageRepository.saveAll(images);
        }
    }
    public byte[] uploadFilesImage(String fileName) throws IOException {
        if (fileName.isEmpty() && fileName == null){
            throw new RuntimeException("not found image");
        }
        Image image =imageRepository.findByImg(fileName).orElseThrow(()-> new FindByNoId("not found image"));
        String pathImage = image.getPathIma();
        byte[] images = Files.readAllBytes(new File(pathImage).toPath());
       return images;
    }
}
