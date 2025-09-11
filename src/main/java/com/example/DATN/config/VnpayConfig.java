package com.example.DATN.config;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class VnpayConfig {

    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
//    public static String vnp_ReturnUrl = "http://26.118.131.110:8080/api/payment/vnpay-return";
    public static String vnp_ReturnUrl = "https://travelsuggest-app-36bf8.web.app/api/payment/vnpay-return"; // URL trả về sau khi thanh toán
    public static String vnp_TmnCode = "04UM55IG"; // Lấy từ tài khoản VNPAY
    public static String vnp_HashSecret = "6KS8MIUI5IHQOKH2IYPAH2RUVYT24AFW"; // Lấy từ tài khoản VNPAY
    public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";
    public static String vnp_OrderType = "other";

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception e) {
            return "";
        }
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}