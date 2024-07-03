package com.example.WebOnThiTracNghiem.controller;

import com.example.WebOnThiTracNghiem.model.Account;
import com.example.WebOnThiTracNghiem.payment.Config;
import com.example.WebOnThiTracNghiem.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final AccountService accountService;

    public PaymentController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/vnpay")
    public String showPaymentPage() {
        return "Payment/payment"; // Tên của trang HTML chứa form nạp tiền
    }

    @PostMapping("/vnpay")
    public String createPayment(HttpServletRequest req, @RequestParam("amount") long amount) throws IOException {

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        Map<String, String> params = new HashMap<>();
        /*  long amount = Integer.parseInt("1000000");*/
        String bankCode = params.get(" ");
        amount=amount*100L;
        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = Config.getIpAddress(req);

        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = params.get("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
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
        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                // Build query
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
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

        return "redirect:" + paymentUrl;
    }

    @GetMapping("/return")
    public String paymentReturn(HttpServletRequest request, Authentication authentication) {
        Map<String, String> params = new HashMap<>();
        for (Enumeration<String> paramsEnum = request.getParameterNames(); paramsEnum.hasMoreElements();) {
            String paramName = paramsEnum.nextElement();
            String paramValue = request.getParameter(paramName);
            params.put(paramName, paramValue);
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (vnp_SecureHash != null) {
            String responseCode = params.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                String vnp_Amount = params.get("vnp_Amount");
                if (vnp_Amount != null) {
                    long amount = Long.parseLong(vnp_Amount) / 100; // Convert back to original amount

                    // Get username of logged in user
                    String username = authentication.getName();

                    // Update user balance
                    Account user = accountService.findByUsername(username);
                    if (user != null) {
                        user.setBalance(user.getBalance() + amount);
                        accountService.saveWithoutEncoding(user);
                    }
                }
                return "Payment/order-confirmation";
            }
        }

        return "Payment/payment-error";
    }

}
