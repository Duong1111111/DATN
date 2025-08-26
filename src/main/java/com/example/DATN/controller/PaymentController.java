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
        vnp_Params.put("vnp_OrderInfo", "Thanh toan quang cao cho adId=" + adId);
        vnp_Params.put("vnp_OrderType", VnpayConfig.vnp_OrderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl); // Giữ nguyên returnUrl của backend
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
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
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
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
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String orderInfo = params.get("vnp_OrderInfo");

        // Trích xuất adId từ vnp_OrderInfo
        int adId = -1;
        try {
            adId = Integer.parseInt(orderInfo.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            // Xử lý lỗi nếu không tìm thấy adId
            response.sendRedirect("http://localhost:3000/payment-return?vnp_ResponseCode=99&error=invalid_order");
            return;
        }

        // URL của trang kết quả trên Frontend
        String frontendReturnUrl = "http://localhost:3000/payment-return";

        if ("00".equals(vnp_ResponseCode)) {
            // Thanh toán thành công -> Cập nhật trạng thái quảng cáo
            AdRequest adRequest = new AdRequest();
            adRequest.setStatus(AccountStatus.ACTIVE);
            adRequest.setPaymentStatus(PaymentStatus.SUCCESS);
            adService.update(adId, adRequest);

            // Redirect về trang thành công của Frontend
            String redirectUrl = String.format("%s?vnp_ResponseCode=00&vnp_OrderInfo=%s",
                    frontendReturnUrl, URLEncoder.encode(orderInfo, StandardCharsets.UTF_8));
            response.sendRedirect(redirectUrl);
        } else {
            // Thanh toán thất bại -> Cập nhật trạng thái
            AdRequest adRequest = new AdRequest();
            adRequest.setPaymentStatus(PaymentStatus.FAILED);
            adService.update(adId, adRequest);

            // Redirect về trang thất bại của Frontend
            String redirectUrl = String.format("%s?vnp_ResponseCode=%s&vnp_OrderInfo=%s",
                    frontendReturnUrl, vnp_ResponseCode, URLEncoder.encode(orderInfo, StandardCharsets.UTF_8));
            response.sendRedirect(redirectUrl);
        }
    }

}