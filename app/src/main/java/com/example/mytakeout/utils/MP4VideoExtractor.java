package com.example.mytakeout.utils;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * mp4extractor 分离器
 * 分离mp4 成h264 和 aac
 * 分别生成h264和aac文件(无adts头)
 * <p>
 * android的无法分离高profile的h264文件，这里分离的是Baseline（profile）
 * <p>
 * 合成的时候，MP4,flv,rtmp都不需要adts头
 * 合成的时候：hls,rtp,ts需要adts头
 * 所以这里分离出来的aac是没有adts头的，mediainfo读取到的音频信息是从moov中box中读取的
 */
public class MP4VideoExtractor {
    private static final String TAG = "MP4VideoExtractor";

    private static MP4BufferLisenter mMP4BufferLisenter;

    public static void setMP4BufferLisenter(MP4BufferLisenter MP4BufferLisenter) {
        mMP4BufferLisenter = MP4BufferLisenter;
    }

    //test3.mp4  h264,aac
    public static void exactorMedia(String sdcard_path) {
        FileOutputStream videoOutputStream = null;
        FileOutputStream audioOutputStream = null;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            //分离的视频文件
//            File videoFile = new File(sdcard_path, "output_video.h264");
            File videoFile = new File(sdcard_path, "output_video.yuv");
            //分离的音频文件
            File audioFile = new File(sdcard_path, "output_audio.aac");
            videoOutputStream = new FileOutputStream(videoFile);
            audioOutputStream = new FileOutputStream(audioFile);
            //输入文件,也可以是网络文件
            //oxford.mp4 视频 h264/baseline  音频 aac/lc 44.1k  2 channel 128kb/s
            mediaExtractor.setDataSource(sdcard_path + "/1.mp4");
            //test3.mp4  视频h264 high   音频aac
            //        mediaExtractor.setDataSource(sdcard_path + "/test3.mp4");
            //test2.mp4 视频mpeg4  音频MP3
            //  mediaExtractor.setDataSource(sdcard_path + "/test2.mp4");
            //信道总数
            int trackCount = mediaExtractor.getTrackCount();
            Log.d(TAG, "trackCount:" + trackCount);
            int audioTrackIndex = -1;
            int videoTrackIndex = -1;
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                //视频信道
                if (mineType.startsWith("video/")) {
                    videoTrackIndex = i;
                }
                //音频信道
                if (mineType.startsWith("audio/")) {
                    audioTrackIndex = i;
                }
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            //切换到视频信道
            mediaExtractor.selectTrack(videoTrackIndex);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                LogUtils.e("video:readSampleCount:" + readSampleCount);
                if (readSampleCount < 0) {
                    break;
                }
                //保存视频信道信息
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                videoOutputStream.write(buffer);//buffer 写入到 videooutputstream中
                if (mMP4BufferLisenter != null) {
                    mMP4BufferLisenter.MP4Buffer(buffer);
                }
                byteBuffer.clear();
                mediaExtractor.advance();
            }
            //切换到音频信道
            if (audioTrackIndex != -1) {
                mediaExtractor.selectTrack(audioTrackIndex);
                while (true) {
                    int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                    Log.d(TAG, "audio:readSampleCount:" + readSampleCount);
                    if (readSampleCount < 0) {
                        break;
                    }
                    //保存音频信息
                    byte[] buffer = new byte[readSampleCount];
                    byteBuffer.get(buffer);
                    /************************* 用来为aac添加adts头**************************/
                    byte[] aacaudiobuffer = new byte[readSampleCount + 7];
                    addADTStoPacket(aacaudiobuffer, readSampleCount + 7);
                    System.arraycopy(buffer, 0, aacaudiobuffer, 7, readSampleCount);
                    audioOutputStream.write(aacaudiobuffer);
                    /***************************************close**************************/
                    //  audioOutputStream.write(buffer);
                    byteBuffer.clear();
                    mediaExtractor.advance();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "mediaExtractor.release!\n");
            mediaExtractor.release();
            mediaExtractor = null;
            try {
                videoOutputStream.close();
                audioOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 这里之前遇到一个坑，以为这个packetLen是adts头的长度，也就是7，仔细看了下代码，发现这个不是adts头的长度，而是一帧音频的长度
     *
     * @param packet    一帧数据（包含adts头长度）
     * @param packetLen 一帧数据（包含adts头）的长度
     */
    private static void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2; // AAC LC
        int freqIdx = getFreqIdx(44100);
        int chanCfg = 2; // CPE

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }


    private static int getFreqIdx(int sampleRate) {
        int freqIdx;

        switch (sampleRate) {
            case 96000:
                freqIdx = 0;
                break;
            case 88200:
                freqIdx = 1;
                break;
            case 64000:
                freqIdx = 2;
                break;
            case 48000:
                freqIdx = 3;
                break;
            case 44100:
                freqIdx = 4;
                break;
            case 32000:
                freqIdx = 5;
                break;
            case 24000:
                freqIdx = 6;
                break;
            case 22050:
                freqIdx = 7;
                break;
            case 16000:
                freqIdx = 8;
                break;
            case 12000:
                freqIdx = 9;
                break;
            case 11025:
                freqIdx = 10;
                break;
            case 8000:
                freqIdx = 11;
                break;
            case 7350:
                freqIdx = 12;
                break;
            default:
                freqIdx = 8;
                break;
        }

        return freqIdx;
    }

}
