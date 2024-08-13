package com.example.couponcore.repository.redis.dto;

import com.example.couponcore.exception.CouponIssueException;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.model.CouponType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

import static com.example.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;

public record CouponRedisEntity(
    Long id,
    CouponType type,
    Integer totalQuantity,

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime dateIssueStart,

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime dateIssueEnd
) {

  public CouponRedisEntity(Coupon coupon) {
    this(
        coupon.getId(),
        coupon.getType(),
        coupon.getTotalQuantity(),
        coupon.getDateIssueStart(),
        coupon.getDateIssueEnd()
    );
  }

  private boolean availableIssueDate() {
    LocalDateTime now = LocalDateTime.now();
    return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
  }

  public void checkIssuableCoupon() {
    if (!availableIssueDate()) {
      throw new CouponIssueException(INVALID_COUPON_ISSUE_DATE, INVALID_COUPON_ISSUE_DATE.getMessage());
    }
  }
}
