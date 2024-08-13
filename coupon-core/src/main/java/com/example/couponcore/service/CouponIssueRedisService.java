package com.example.couponcore.service;

import com.example.couponcore.exception.CouponIssueException;
import com.example.couponcore.repository.redis.RedisRepository;
import com.example.couponcore.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.couponcore.exception.ErrorCode.DUPLICATED_COUPON_ISSUE;
import static com.example.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;
import static com.example.couponcore.util.CouponRedisUtils.getIssueRequestKey;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {
  private final RedisRepository redisRepository;

  public void checkCouponIssueQuantity(CouponRedisEntity couponRedisEntity, long userId) {
    if (!availableTotalIssueQuantity(couponRedisEntity.totalQuantity(), couponRedisEntity.id())) {
      throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY, INVALID_COUPON_ISSUE_QUANTITY.getMessage());
    }

    if (!availableUserIssueQuantity(couponRedisEntity.id(), userId)) {
      throw new CouponIssueException(DUPLICATED_COUPON_ISSUE, DUPLICATED_COUPON_ISSUE.getMessage());
    }
  }

  public boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
    if (totalQuantity == null) {
      return true;
    }
    String key = getIssueRequestKey(couponId);
    return totalQuantity > redisRepository.sCard(key);
  }

  public boolean availableUserIssueQuantity(long couponId, long userId) {
   String key = getIssueRequestKey(couponId);
   return !redisRepository.sIsMember(key, String.valueOf(userId));
  }
}
