package org.hang.shortlink.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class QrCodeUtils {

    private static final int DEFAULT_SIZE = 300;
    private static final String FORMAT = "png";

    /**
     * 生成二维码图片
     */
    public static BufferedImage generateQrCode(String content, int width, int height) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return image;
        } catch (WriterException e) {
            throw new RuntimeException("生成二维码失败", e);
        }
    }

    /**
     * 【新增】生成二维码字节数组（用于Redis缓存）
     */
    public static byte[] generateQrCodeBytes(String content, int width, int height) {
        BufferedImage image = generateQrCode(content, width, height);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, FORMAT, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("二维码转字节数组失败", e);
        }
    }

    // 原有输出流方法（保留）
    public static void writeToStream(String content, OutputStream outputStream) {
        writeToStream(content, DEFAULT_SIZE, DEFAULT_SIZE, outputStream);
    }

    public static void writeToStream(String content, int width, int height, OutputStream outputStream) {
        BufferedImage image = generateQrCode(content, width, height);
        try {
            ImageIO.write(image, FORMAT, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("输出二维码图片失败", e);
        }
    }
}
