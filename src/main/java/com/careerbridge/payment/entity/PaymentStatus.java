package com.careerbridge.payment.entity;
/*
READY : 서버에서 결제 요청 생성
RAID : PG 승인 검증 완료
FAILED : 결제 실패
CANCELED : 결제 취소
*/
public enum PaymentStatus {
    READY,  PAID, FAILED, CANCELED
}
