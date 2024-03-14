package com.example.library_book.Video;

import com.example.library_book.Image.Image;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class VideoService {

    public void AddVideo(MultipartFile file,String pathAudio, String pathImg) throws IOException {
        if (file == null && file.isEmpty()){
            throw new RuntimeException("fail");
        }
        int desiredWidth = 430;
        int desiredHeight = 942;
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(pathImg);
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(pathAudio, desiredWidth, desiredHeight); // 0 là chọn âm thanh

            grabber.start();
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.start();
            recorder.setFormat("mp4a");

            Frame capturedFrame;
            while (true) {
                capturedFrame = grabber.grab();
                if (capturedFrame == null) {
                    break; // Kết thúc vòng lặp nếu không còn frame âm thanh
                }
                recorder.record(capturedFrame);
            }

            grabber.stop();
            recorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

