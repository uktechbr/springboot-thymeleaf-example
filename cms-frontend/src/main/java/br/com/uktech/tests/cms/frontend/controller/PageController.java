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

package br.com.uktech.tests.cms.frontend.controller;

import br.com.uktech.tests.cms.frontend.dto.LeadDto;
import br.com.uktech.tests.cms.frontend.dto.PageDto;
import br.com.uktech.tests.cms.frontend.service.CmsBackendClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Reactive Web Controller for rendering CMS pages with Thymeleaf and Redis caching.
 *
 * @created 27/08/2026
 * @author Carlos Alberto Cipriano Korovsky &lt;carlos.korovsky@uktech.com.br&gt;
 */
@Controller
public class PageController {

    private final CmsBackendClient backendClient;

    private static final String CACHE_PREFIX = "cms:page:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    /**
     * Constructs the controller with required dependencies.
     *
     * @param backendClient the REST client to fetch data from the backend.
     */
    public PageController(CmsBackendClient backendClient) {
        this.backendClient = backendClient;
    }

    /**
     * Renders the home page (slug 'home') with reactive Redis caching,
     * including the navigation menu list and optional lead feedback.
     *
     * @param success optional flag indicating successful lead submission.
     * @param error   optional flag indicating lead submission error.
     * @return a reactive {@link Mono} emitting the {@link Rendering} view.
     */
    @GetMapping({"", "/"})
    public Mono<Rendering> getHomePage(
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) Boolean error) {

        return loadPageWithMenu("home", success, error);
    }

    /**
     * Renders any dynamic page based on its unique URL slug.
     *
     * @param slug    the unique URL identifier of the page.
     * @param success optional flag indicating successful lead submission.
     * @param error   optional flag indicating error during lead submission.
     * @return a reactive {@link Mono} emitting the {@link Rendering} view.
     */
    @GetMapping("/{slug}")
    public Mono<Rendering> getPageBySlug(
            @PathVariable String slug,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) Boolean error) {

        return loadPageWithMenu(slug, success, error);
    }

    /**
     * Helper method to fetch the target page (cached via service) and the full menu list reactively,
     * combining them into a single Thymeleaf rendering view.
     */
    private Mono<Rendering> loadPageWithMenu(String slug, Boolean success, Boolean error) {
        // Service handles both Redis cache and backend fallback transparently
        Mono<PageDto> pageMono = backendClient.getPageBySlug(slug)
                .switchIfEmpty(backendClient.getPageBySlug("home")); // Fallback if slug not found

        Flux<PageDto> menuFlux = backendClient.getAllPages();

        return Mono.zip(pageMono, menuFlux.collectList())
                .map(tuple -> Rendering.view("index")
                        .modelAttribute("page", tuple.getT1())
                        .modelAttribute("menuPages", tuple.getT2())
                        .modelAttribute("success", success)
                        .modelAttribute("error", error)
                        .build())
                .defaultIfEmpty(Rendering.view("index")
                        .modelAttribute("success", success)
                        .modelAttribute("error", error)
                        .build());
    }

    /**
     * Handles lead form submissions from the landing pages.
     *
     * @param leadDto the lead data transferred from the form submission.
     * @return a redirect to the home page with a success query param.
     */
    @PostMapping("/leads")
    public Mono<String> submitLead(@ModelAttribute LeadDto leadDto) {
        return backendClient.createLead(leadDto)
                .thenReturn("redirect:/?success=true")
                .onErrorResume(ex -> Mono.just("redirect:/?error=true"));
    }

}