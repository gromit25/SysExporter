package com.redeye.sysexporter.domain;

/**
 * 호스트 정보 Value Object
 *
 * @param organ 조직명
 * @param region 영역
 * @param host 호스트명
 */
public record HostVO(
  String organ,
  String region,
  String host
) {};
