package com.sparta.paymentservice.application.service;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImPortPaymentService implements PaymentService {
    private final IamportClient iamportClient;

    public IamportResponse<Payment> requestTestPayment(UUID reservationId, Integer amount) throws IamportResponseException, IOException {
        // 테스트용 결제 정보 설정
        String merchantUid = reservationId.toString();
        BigDecimal totalAmount = new BigDecimal(amount);

        String card_number = "5388-1500-0000-0000";
        String expiry = "2025-12";
        String birth = "700101";
        String pwd_2digit = "11";
        CardInfo card = new CardInfo(card_number, expiry, birth, pwd_2digit);

        OnetimePaymentData onetimePaymentData = new OnetimePaymentData(merchantUid, totalAmount, card);
        onetimePaymentData.setPg("kcp");

        // 결제 요청
        IamportResponse<Payment> paymentResponse = iamportClient.onetimePayment(onetimePaymentData);

        if (paymentResponse.getResponse() != null && paymentResponse.getResponse().getStatus().equals("paid")) {
            // 결제 성공
            System.out.println("결제 성공: " + paymentResponse.getResponse().getApplyNum());
        } else {
            // 결제 실패
            System.out.println("결제 실패");
        }

        return paymentResponse;
    }
}
