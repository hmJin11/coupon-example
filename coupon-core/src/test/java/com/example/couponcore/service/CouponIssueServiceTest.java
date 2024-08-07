package com.example.couponcore.service;

import com.example.couponcore.TestConfig;
import com.example.couponcore.exception.CouponIssueException;
import com.example.couponcore.exception.ErrorCode;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.model.CouponIssue;
import com.example.couponcore.model.CouponType;
import com.example.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.example.couponcore.repository.mysql.CouponIssueRepository;
import com.example.couponcore.repository.mysql.CouponJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

class CouponIssueServiceTest extends TestConfig {

  @Autowired
  CouponIssueService couponIssueService;

  @Autowired
  CouponIssueJpaRepository couponIssueJpaRepository;

  @Autowired
  CouponIssueRepository couponIssueRepository;

  @Autowired
  CouponJpaRepository couponJpaRepository;

  @BeforeEach
  void clean() {
    couponJpaRepository.deleteAllInBatch();
    couponIssueJpaRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("쿠폰 발급 내역이 존재하면 예외를 반환한다.")
  public void saveCouponIssue_1() throws Exception {
    //given
    CouponIssue couponIssue = CouponIssue.builder()
        .couponId(1L)
        .userId(1L)
        .build();

    couponIssueJpaRepository.save(couponIssue);

    //when
    CouponIssueException couponIssueException =
        Assertions.assertThrows(CouponIssueException.class, () -> {
          couponIssueService.saveCouponIssue(couponIssue.getCouponId(), couponIssue.getUserId());
        });
    //then
    Assertions.assertEquals(couponIssueException.getErrorCode(), ErrorCode.DUPLICATED_COUPON_ISSUE);
  }

  @Test
  @DisplayName("쿠폰 발급 내역이 존재하지 않는다면 쿠폰을 발급한다.")
  public void saveCouponIssue_2() throws Exception {
    //given
    long couponId = 1L;
    long userId = 1L;

    //when
    CouponIssue couponIssue = couponIssueService.saveCouponIssue(couponId, userId);

    //then
    Assertions.assertTrue(couponIssueJpaRepository.findById(couponIssue.getId()).isPresent());
  }

  @Test
  @DisplayName("발급 수량, 기한, 중복 발급 문제가 없다면 쿠폰을 발급한다.")
  public void issue_1() throws Exception {
    //given
    long userId = 1L;
    Coupon coupon = Coupon.builder()
        .type(CouponType.FIRST_COME_FIRST_SERVED)
        .title("선착순 테스트 쿠폰")
        .totalQuantity(100)
        .issuedQuantity(0)
        .dateIssueStart(LocalDateTime.now().minusDays(1))
        .dateIssueEnd(LocalDateTime.now().plusDays(1))
        .build();
    couponJpaRepository.save(coupon);

    //when
    couponIssueService.issue(coupon.getId(), userId);

    //then
    Coupon couponResult = couponJpaRepository.findById(coupon.getId()).get();
    Assertions.assertEquals(couponResult.getIssuedQuantity(), 1);

    CouponIssue couponIssueResult =
        couponIssueRepository.findFirstCouponIssue(coupon.getId(), userId);
    Assertions.assertNotNull(couponIssueResult);
  }

  @Test
  @DisplayName("발급 수량에 문제가 있다면 예외를 반환한다.")
  public void issue_2() throws Exception {
    //given
    long userId = 1L;
    Coupon coupon = Coupon.builder()
        .type(CouponType.FIRST_COME_FIRST_SERVED)
        .title("선착순 테스트 쿠폰")
        .totalQuantity(100)
        .issuedQuantity(100)
        .dateIssueStart(LocalDateTime.now().minusDays(1))
        .dateIssueEnd(LocalDateTime.now().plusDays(1))
        .build();
    couponJpaRepository.save(coupon);

    // when & then
    CouponIssueException couponIssueException =
        Assertions.assertThrows(CouponIssueException.class, () -> {
          couponIssueService.issue(coupon.getId(), userId);
        });
    Assertions.assertEquals(couponIssueException.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);
  }

  @Test
  @DisplayName("발급 기한에 문제가 있다면 예외를 반환한다.")
  public void issue_3() throws Exception {
    //given
    long userId = 1L;
    Coupon coupon = Coupon.builder()
        .type(CouponType.FIRST_COME_FIRST_SERVED)
        .title("선착순 테스트 쿠폰")
        .totalQuantity(100)
        .issuedQuantity(0)
        .dateIssueStart(LocalDateTime.now().minusDays(2))
        .dateIssueEnd(LocalDateTime.now().minusDays(1))
        .build();
    couponJpaRepository.save(coupon);

    // when & then
    CouponIssueException couponIssueException =
        Assertions.assertThrows(CouponIssueException.class, () -> {
          couponIssueService.issue(coupon.getId(), userId);
        });
    Assertions.assertEquals(couponIssueException.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_DATE);
  }

  @Test
  @DisplayName("중복 발급 검증에 문제가 있다면 예외를 반환한다.")
  public void issue_4() throws Exception {
    //given
    long userId = 1L;
    Coupon coupon = Coupon.builder()
        .type(CouponType.FIRST_COME_FIRST_SERVED)
        .title("선착순 테스트 쿠폰")
        .totalQuantity(100)
        .issuedQuantity(0)
        .dateIssueStart(LocalDateTime.now().minusDays(1))
        .dateIssueEnd(LocalDateTime.now().plusDays(1))
        .build();
    couponJpaRepository.save(coupon);

    CouponIssue couponIssue = CouponIssue.builder()
        .couponId(coupon.getId())
        .userId(userId)
        .build();

    couponIssueJpaRepository.save(couponIssue);

    // when & then
    CouponIssueException couponIssueException =
        Assertions.assertThrows(CouponIssueException.class, () -> {
          couponIssueService.issue(coupon.getId(), userId);
        });
    Assertions.assertEquals(couponIssueException.getErrorCode(), ErrorCode.DUPLICATED_COUPON_ISSUE);
  }

  @Test
  @DisplayName("쿠폰이 존재하지 않는다면 예외를 반환한다.")
  public void issue_5() throws Exception {
    //given
    long couponId = 1L;
    long userId = 1L;

    // when & then
    CouponIssueException couponIssueException =
        Assertions.assertThrows(CouponIssueException.class, () -> {
          couponIssueService.issue(couponId, userId);
        });
    Assertions.assertEquals(couponIssueException.getErrorCode(), ErrorCode.COUPON_NOT_EXIST);
  }
}
