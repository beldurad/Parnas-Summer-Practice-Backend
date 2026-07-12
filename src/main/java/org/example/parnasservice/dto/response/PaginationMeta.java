package org.example.parnasservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMeta {

    private int page;
    private int pageSize;
    private long totalItems;
    private int totalPages;

}
