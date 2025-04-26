package com.sparta.paymentservice.common.util;

import com.siot.IamportRestClient.request.CardInfo;

public class TestCardInfo {
    public static CardInfo testCard1() {
        return new CardInfo(
                "5388-1500-0000-0000",
                "2025-12",
                "700101",
                "11"
        );
    }

    public static CardInfo testCard2() {
        return new CardInfo(
                "1234-0000-0000-0000",
                "2020-01",
                "990101",
                "00"
        );
    }

}