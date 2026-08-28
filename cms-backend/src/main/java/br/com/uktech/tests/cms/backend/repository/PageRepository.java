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

package br.com.uktech.tests.cms.backend.repository;

import br.com.uktech.tests.cms.backend.model.Page;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Reactive repository interface for managing {@link Page} entities.
 * <p>
 * Extends {@link ReactiveCrudRepository} to provide non-blocking CRUD operations
 * and custom query derivation over PostgreSQL using Spring Data R2DBC.
 *
 * @created 27/08/2026
 * @author Carlos Alberto Cipriano Korovsky &lt;carlos.korovsky@uktech.com.br&gt;
 */
@Repository
public interface PageRepository extends ReactiveCrudRepository<Page, Long> {

    /**
     * Finds a page entity by its unique URL-friendly slug in a non-blocking reactive stream.
     *
     * @param slug the unique string identifier used for SEO routing.
     * @return a {@link Mono} emitting the found {@link Page}, or empty if not found.
     */
    Mono<Page> findBySlug(String slug);
}