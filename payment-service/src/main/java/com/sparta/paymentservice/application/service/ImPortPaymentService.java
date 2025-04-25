package com.sparta.paymentservice.application.service;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.sparta.paymentservice.application.dto.PaymentRequestDto;
import com.sparta.paymentservice.application.dto.PaymentResponseDto;
import com.sparta.paymentservice.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImPortPaymentService implements PaymentService {
    private final IamportClient iamportClient;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponseDto confirmPayment(PaymentRequestDto request, CardInfo card) {
        try {
            // 테스트용 결제 정보 설정
            String merchantUid = request.getReservationId().toString();
            BigDecimal totalPrice = request.getTotalPrice();

            OnetimePaymentData onetimePaymentData = new OnetimePaymentData(merchantUid, totalPrice, card);
            onetimePaymentData.setPg("kcp");
            // 결제 요청
            IamportResponse<Payment> response = iamportClient.onetimePayment(onetimePaymentData);
            Payment payment = response.getResponse();

            if(payment == null) {
              throw new IllegalArgumentException("결제 실패: " + response.getMessage());
            }

            PaymentResponseDto responseDto = PaymentResponseDto.toDto(payment);
            // 결제 기록 저장
            paymentRepository.save(responseDto.toEntity());
            return responseDto;

        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException("결제 처리 중 오류 발생", e);
        }
    }
}
