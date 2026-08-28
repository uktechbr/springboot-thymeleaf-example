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
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Domain model representing a captured lead from landing pages managed by the CMS backend.
 * <p>
 * This entity maps to the relational database table {@code leads} via Spring Data R2DBC,
 * allowing non-blocking reactive persistence of contact submissions.
 *
 * @created 27/08/2026
 * @author Carlos Alberto Cipriano Korovsky &lt;carlos.korovsky@uktech.com.br&gt;
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("leads")
public class Lead {

    /**
     * Unique identifier (primary key) of the lead record.
     */
    @Id
    private Long id;

    /**
     * Full name of the lead submitting the form.
     */
    private String name;

    /**
     * Email address of the lead.
     */
    private String email;

    /**
     * Timestamp indicating when the lead record was created.
     * Automatically managed by Spring Data R2DBC auditing.
     */
    @CreatedDate
    private LocalDateTime createdAt;
}