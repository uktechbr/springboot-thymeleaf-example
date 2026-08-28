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

package br.com.uktech.tests.cms.backend.controller;

import br.com.uktech.tests.cms.backend.model.Lead;
import br.com.uktech.tests.cms.backend.model.Page;
import br.com.uktech.tests.cms.backend.repository.LeadRepository;
import br.com.uktech.tests.cms.backend.repository.PageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive REST Controller exposing CMS operations for pages and leads.
 * <p>
 * Handles non-blocking HTTP requests coming from the frontend application,
 * providing endpoints to retrieve dynamic SEO-friendly pages and capture form leads.
 *
 * @created 27/08/2026
 * @author Carlos Alberto Cipriano Korovsky &lt;carlos.korovsky@uktech.com.br&gt;
 */
@RestController
@RequestMapping("/api/cms")
public class CmsController {

    private final PageRepository pageRepository;
    private final LeadRepository leadRepository;

    /**
     * Constructs the controller with required reactive repositories.
     *
     * @param pageRepository the reactive repository for institutional pages.
     * @param leadRepository the reactive repository for form leads.
     */
    public CmsController(PageRepository pageRepository, LeadRepository leadRepository) {
        this.pageRepository = pageRepository;
        this.leadRepository = leadRepository;
    }

    /**
     * Retrieves a page entity by its unique slug in a non-blocking stream.
     *
     * @param slug the unique URL identifier of the page.
     * @return a {@link Mono} emitting the found {@link Page}, or empty if not found.
     */
    @GetMapping("/pages/{slug}")
    public Mono<Page> getPageBySlug(@PathVariable String slug) {
        return pageRepository.findBySlug(slug);
    }

    /**
     * Persists a new lead submission coming from landing page forms.
     *
     * @param lead the lead entity containing name and email.
     * @return a {@link Mono} emitting the saved {@link Lead} with HTTP status 201 Created.
     */
    @PostMapping("/leads")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Lead> createLead(@RequestBody Lead lead) {
        return leadRepository.save(lead);
    }

    /**
     * Retrieves a list of all pages available in the CMS.
     *
     * @return a reactive {@link Flux} emitting all stored {@link Page} entities.
     */
    @GetMapping("/pages")
    public Flux<Page> getAllPages() {
        return pageRepository.findAll();
    }

    /**
     * Creates a new CMS page.
     *
     * @param page the page entity to be created, passed in the request body.
     * @return a reactive {@link Mono} emitting the saved {@link Page} entity.
     */
    @PostMapping("/pages")
    public Mono<Page> createPage(@RequestBody Page page) {
        return pageRepository.save(page);
    }

    /**
     * Updates an existing CMS page identified by its ID.
     *
     * @param id          the unique identifier of the page to update.
     * @param pageDetails the new details for the page.
     * @return a reactive {@link Mono} emitting the updated {@link Page},
     *         or an empty mono with a 404 status if the page is not found.
     */
    @PutMapping("/pages/{id}")
    public Mono<ResponseEntity<Page>> updatePage(@PathVariable Long id, @RequestBody Page pageDetails) {
        return pageRepository.findById(id)
                .flatMap(existingPage -> {
                    // Update fields
                    existingPage.setSlug(pageDetails.getSlug());
                    existingPage.setTitle(pageDetails.getTitle());
                    existingPage.setMetaDescription(pageDetails.getMetaDescription());
                    existingPage.setContent(pageDetails.getContent());
                    // Note: updatedAt can also be updated automatically if handled by entity listeners
                    return pageRepository.save(existingPage);
                })
                .map(updatedPage -> ResponseEntity.ok(updatedPage))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a CMS page by its unique identifier.
     *
     * @param id the unique identifier of the page to delete.
     * @return a reactive {@link Mono} emitting a {@link ResponseEntity} with NO_CONTENT
     *         if successful, or NOT_FOUND if the page does not exist.
     */
    @DeleteMapping("/pages/{id}")
    public Mono<ResponseEntity<Void>> deletePage(@PathVariable Long id) {
        return pageRepository.findById(id)
                .flatMap(page -> pageRepository.delete(page)
                        .thenReturn(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}