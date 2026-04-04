package sdu.group_23.taskpie.data.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private int currentPage;
    private List<T> items;
    private int totalPages;
    private int totalItems;
    private boolean hasNext;

    public static <T> PageResponse<T> pageToResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .currentPage(page.getNumber() + 1)
                .items(page.getContent())
                .totalPages(page.getTotalPages())
                .totalItems((int) page.getTotalElements())
                .hasNext(page.hasNext())
                .build();
    }
}
