/*
 * Copyright (c) 2026 Uhlig & Korovsky Tecnologia Ltda
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package br.com.uktech.tests.cms.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Domain model representing an institutional or landing page managed by the CMS backend.
 * <p>
 * This entity is mapped to the relational database table {@code pages} via Spring Data R2DBC,
 * supporting non-blocking reactive operations for high-performance retrieval and SEO optimization.
 *
 * @created 27/08/2026
 * @author Carlos Alberto Cipriano Korovsky &lt;carlos.korovsky@uktech.com.br&gt;
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("pages")
public class Page {

    /**
     * Unique identifier (primary key) of the page.
     */
    @Id
    private Long id;

    /**
     * Unique URL-friendly slug used for SEO routing (e.g., 'home', 'about-us').
     */
    private String slug;

    /**
     * The main title of the page rendered in browser tabs and meta tags.
     */
    private String title;

    /**
     * Meta description used by search engine crawlers for SEO indexing.
     */
    private String metaDescription;

    /**
     * HTML content blocks or raw markup managed by the CMS.
     */
    private String content;

    /**
     * Timestamp indicating when the page record was created.
     * Automatically managed by Spring Data R2DBC auditing.
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating when the page record was last updated.
     * Automatically managed by Spring Data R2DBC auditing.
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}