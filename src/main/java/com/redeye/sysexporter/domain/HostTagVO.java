package com.redeye.sysexporter.domain;

/**
 * 호스트 테그 정보 Value Object
 *
 * @param organCode 조직 코드
 * @param domainCode 도메인 코드
 * @param host 호스트명
 */
public record HostTagVO(
  String organCode,
  String domainCode,
  String hostName
) {
  @Override
  public String toString() {
    return new StringBuilder()
      .append(organCode())
      .append(":")
      .append(domainCode())
      .append(":")
      .append(hostName())
      .toString();
  }
};
