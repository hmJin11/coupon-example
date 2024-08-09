package com.example.couponapi.service;

import com.example.couponapi.controller.dto.CouponIssueRequestDto;
import com.example.couponcore.component.DistributeLockExecutor;
import com.example.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CouponIssueRequestService {
  private final CouponIssueService couponIssueService;
  private final DistributeLockExecutor distributeLockExecutor;

  public void issueRequestV1(CouponIssueRequestDto couponIssueRequestDto) {
//    distributeLockExecutor.execute("lock_"+ couponIssueRequestDto.couponId(), 10000, 10000, () -> {
//      couponIssueService.issue(couponIssueRequestDto.couponId(), couponIssueRequestDto.userId());
//    });
    couponIssueService.issue(couponIssueRequestDto.couponId(), couponIssueRequestDto.userId());
    log.info("쿠폰 발급 완료. couponId: %s, userId: %s", couponIssueRequestDto.couponId(), couponIssueRequestDto.userId());
  }
}
