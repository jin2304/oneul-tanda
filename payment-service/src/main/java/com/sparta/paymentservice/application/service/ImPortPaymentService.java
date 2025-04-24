package com.sparta.paymentservice.application.service;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.sparta.paymentservice.application.dto.PaymentRequestDto;
import com.sparta.paymentservice.application.dto.PaymentResponseDto;
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

    @Override
    public PaymentResponseDto confirmPayment(PaymentRequestDto request) {
        try {
            // 테스트용 결제 정보 설정
            String merchantUid = request.getReservationId().toString();
            BigDecimal totalPrice = new BigDecimal(request.getTotalPrice());

            String card_number = "5388-1500-0000-0000";
            String expiry = "2025-12";
            String birth = "700101";
            String pwd_2digit = "11";
            CardInfo card = new CardInfo(card_number, expiry, birth, pwd_2digit);

            OnetimePaymentData onetimePaymentData = new OnetimePaymentData(merchantUid, totalPrice, card);
            onetimePaymentData.setPg("kcp");
            // 결제 요청
            IamportResponse<Payment> response = iamportClient.onetimePayment(onetimePaymentData);
            Payment payment = response.getResponse();

            return PaymentResponseDto.builder()
                    .paymentId(payment.getImpUid())
                    .reservationId(UUID.fromString(payment.getMerchantUid()))
                    .totalPrice(payment.getAmount().intValue())
                    .status(payment.getStatus())
                    .build();

        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException("결제 처리 중 오류 발생", e);
        }
    }
}
