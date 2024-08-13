package com.example.couponapi.service;

import com.example.couponapi.controller.dto.CouponIssueRequestDto;
import com.example.couponcore.component.DistributeLockExecutor;
import com.example.couponcore.service.AsyncCouponIssueServiceV1;
import com.example.couponcore.service.AsyncCouponIssueServiceV2;
import com.example.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CouponIssueRequestService {
  private final CouponIssueService couponIssueService;
  private final AsyncCouponIssueServiceV1 asyncCouponIssueServiceV1;
  private final AsyncCouponIssueServiceV2 asyncCouponIssueServiceV2;

  public void issueRequestV1(CouponIssueRequestDto couponIssueRequestDto) {
    couponIssueService.issue(couponIssueRequestDto.couponId(), couponIssueRequestDto.userId());
    log.info("쿠폰 발급 완료. couponId: %s, userId: %s", couponIssueRequestDto.couponId(), couponIssueRequestDto.userId());
  }

  public void asyncIssueRequestV1(CouponIssueRequestDto couponIssueRequestDto) {
    asyncCouponIssueServiceV1.issue(couponIssueRequestDto.couponId(), couponIssueRequestDto.userId());
  }

  public void asyncIssueRequestV2(CouponIssueRequestDto couponIssueRequestDto) {
    asyncCouponIssueServiceV2.issue(couponIssueRequestDto.couponId(), couponIssueRequestDto.userId());
  }
}
