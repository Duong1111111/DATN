package com.example.DATN.controller;

import com.example.DATN.config.VnpayConfig;
import com.example.DATN.dto.request.AdRequest;
import com.example.DATN.service.interfaces.AdService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.PaymentStatus;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private AdService adService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @GetMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestParam("amount") long amount, @RequestParam("adId") int adId) throws UnsupportedEncodingException {

        String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VnpayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VnpayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VnpayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan cho quang cao ID: " + adId);
        vnp_Params.put("vnp_OrderType", VnpayConfig.vnp_OrderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnpayConfig.vnp_PayUrl + "?" + queryUrl;

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/vnpay-return")
    public void vnpayReturn(HttpServletResponse response, @RequestParam Map<String, String> params) throws IOException {
//        String frontendReturnUrl = "https://travelsuggest-app-36bf8.web.app/payment-return";
        String frontendReturnUrl = "http://26.112.109.171:3000/payment-return";

        try {
            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String orderInfo = params.get("vnp_OrderInfo");

            logger.info("VNPAY Return Data: {}", params);

            int adId = -1;
            String numericPart = orderInfo.replaceAll("[^0-9]", "");
            if (!numericPart.isEmpty()) {
                adId = Integer.parseInt(numericPart);
            }

            if (adId == -1) {
                throw new Exception("Could not parse Ad ID from order info: " + orderInfo);
            }

            if ("00".equals(vnp_ResponseCode)) {
                logger.info("Payment successful for Ad ID: {}", adId);
                AdRequest adRequest = new AdRequest();
                adRequest.setStatus(AccountStatus.ACTIVE);
                adRequest.setPaymentStatus(PaymentStatus.SUCCESS);
                adService.update(adId, adRequest);

                String redirectUrl = String.format("%s?vnp_ResponseCode=00&vnp_OrderInfo=%s",
                        frontendReturnUrl, URLEncoder.encode(orderInfo, StandardCharsets.UTF_8));
                response.sendRedirect(redirectUrl);
            } else {
                logger.warn("Payment failed for Ad ID: {} with response code: {}", adId, vnp_ResponseCode);
                AdRequest adRequest = new AdRequest();
                adRequest.setPaymentStatus(PaymentStatus.FAILED);
                adService.update(adId, adRequest);

                String redirectUrl = String.format("%s?vnp_ResponseCode=%s&vnp_OrderInfo=%s",
                        frontendReturnUrl, vnp_ResponseCode, URLEncoder.encode(orderInfo, StandardCharsets.UTF_8));
                response.sendRedirect(redirectUrl);
            }
        } catch (Exception e) {
            logger.error("Error processing VNPAY return: ", e);
            // Bất kể lỗi gì, cũng redirect về frontend
            response.sendRedirect(frontendReturnUrl + "?vnp_ResponseCode=99&error=server_error");
        }
    }

}