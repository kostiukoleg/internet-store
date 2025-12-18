package com.internetstore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageItem {
    private String url;

    private String altText;

    private Integer position;   // for sorting (0 = main image)

    private boolean primary;
}
