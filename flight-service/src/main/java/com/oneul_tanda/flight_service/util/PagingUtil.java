package com.oneul_tanda.flight_service.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PagingUtil {

  private static final int[] ALLOWED_PAGE_SIZES = {10, 30, 50};
  private static final int DEFAULT_PAGE_SIZE = 10;
  private static final String DEFAULT_SORT_FIELD = "createdAt";
  private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;

  // Pageable 조정: 페이지 크기 제한 및 정렬 기본값 적용
  public static Pageable adjustPageable(Pageable pageable) {
    int pageSize = pageable.getPageSize();
    // 허용된 페이지 크기 체크
    if (!isAllowedPageSize(pageSize)) {
      pageSize = DEFAULT_PAGE_SIZE;
    }

    // 정렬 기본값 적용: createdAt DESC, 없으면 updatedAt DESC
    Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(DEFAULT_SORT_DIRECTION, DEFAULT_SORT_FIELD);
    if (!sort.isSorted()) {
      sort = Sort.by(DEFAULT_SORT_DIRECTION, "updatedAt");
    }

    return PageRequest.of(pageable.getPageNumber(), pageSize, sort);
  }

  // 허용된 페이지 크기인지 확인
  private static boolean isAllowedPageSize(int pageSize) {
    for (int allowedSize : ALLOWED_PAGE_SIZES) {
      if (pageSize == allowedSize) {
        return true;
      }
    }
    return false;
  }

  // 기본 Pageable 생성 (컨트롤러에서 @PageableDefault 대신 사용 가능)
  public static Pageable getDefaultPageable(int page, int size) {
    int adjustedSize = isAllowedPageSize(size) ? size : DEFAULT_PAGE_SIZE;
    return PageRequest.of(page, adjustedSize, Sort.by(DEFAULT_SORT_DIRECTION, DEFAULT_SORT_FIELD));
  }
}
