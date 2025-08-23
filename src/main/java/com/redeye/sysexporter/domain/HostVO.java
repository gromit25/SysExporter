package com.redeye.sysexporter.domain;

/**
 * 호스트 정보 Value Object
 *
 * @param organ 조직명
 * @param domain 영역
 * @param host 호스트명
 */
public record HostVO(
  String organ,
  String domain,
  String host
) {
  @Override
  public String toString() {
    return new StringBuilder()
      .append(organ())
      .append(":")
      .append(domain())
      .append(":")
      .append(host())
      .toString();
  }
};
