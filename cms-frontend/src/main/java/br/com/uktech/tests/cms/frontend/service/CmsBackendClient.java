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

package br.com.uktech.tests.cms.frontend.service;

import br.com.uktech.tests.cms.frontend.dto.LeadDto;
import br.com.uktech.tests.cms.frontend.dto.PageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

/**
 * Non-blocking reactive client for communicating with the CMS backend REST API
 * and managing the Redis caching layer utilizing the Cache-Aside pattern.
 *
 * @created 27/08/2026
 * @author Carlos Alberto Cipriano Korovsky &lt;carlos.korovsky@uktech.com.br&gt;
 */
@Service
public class CmsBackendClient {

    private final WebClient webClient;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX = "cms:page:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    /**
     * Constructs the client pointing to the configured backend API base URL,
     * along with Redis template and Jackson object mapper dependencies.
     *
     * @param backendApiBaseUrl the base URL injected from application properties.
     * @param redisTemplate     the reactive Redis template for caching operations.
     * @param objectMapper      the Jackson object mapper for JSON serialization/deserialization.
     */
    public CmsBackendClient(@Value("${backend.api.base-url:http://localhost:8081}") String backendApiBaseUrl,
                            ReactiveStringRedisTemplate redisTemplate,
                            ObjectMapper objectMapper) {
        this.webClient = WebClient.builder()
                .baseUrl(backendApiBaseUrl)
                .build();
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches a page by its slug, implementing the Cache-Aside pattern with Redis.
     * Tries to retrieve from cache first; on a miss, fetches from the backend API and updates the cache.
     *
     * @param slug the unique page identifier.
     * @return a reactive {@link Mono} emitting the {@link PageDto} if found.
     */
    public Mono<PageDto> getPageBySlug(String slug) {
        String cacheKey = CACHE_PREFIX + slug;

        return redisTemplate.opsForValue().get(cacheKey)
                .flatMap(json -> {
                    try {
                        PageDto page = objectMapper.readValue(json, PageDto.class);
                        return Mono.just(page);
                    } catch (Exception e) {
                        return Mono.empty();
                    }
                })
                .switchIfEmpty(
                        this.webClient.get()
                                .uri("/api/cms/pages/{slug}", slug)
                                .retrieve()
                                .bodyToMono(PageDto.class)
                                .flatMap(page -> {
                                    try {
                                        String json = objectMapper.writeValueAsString(page);
                                        return redisTemplate.opsForValue().set(cacheKey, json, CACHE_TTL)
                                                .thenReturn(page);
                                    } catch (Exception e) {
                                        return Mono.just(page);
                                    }
                                })
                );
    }

    /**
     * Sends a new lead submission to the backend API.
     *
     * @param leadDto the lead data transfer object.
     * @return a {@link Mono} emitting the saved lead response.
     */
    public Mono<Void> createLead(LeadDto leadDto) {
        return this.webClient.post()
                .uri("/api/cms/leads")
                .bodyValue(leadDto)
                .retrieve()
                .bodyToMono(Void.class);
    }

    /**
     * Fetches all available pages from the backend CMS service (used for navigation menu).
     *
     * @return a reactive {@link Flux} emitting {@link PageDto} instances.
     */
    public Flux<PageDto> getAllPages() {
        return webClient.get()
                .uri("/api/cms/pages")
                .retrieve()
                .bodyToFlux(PageDto.class);
    }
}