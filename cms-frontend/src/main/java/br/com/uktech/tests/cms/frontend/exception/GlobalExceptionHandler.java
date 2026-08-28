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

package br.com.uktech.tests.cms.frontend.exception;

import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Global reactive error handler that intercepts application exceptions
 * and renders the custom Thymeleaf 'error.html' template gracefully.
 *
 * @created 27/08/2026
 * @author Carlos Alberto Cipriano Korovsky &lt;carlos.korovsky@uktech.com.br&gt;
 */
@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final List<ViewResolver> viewResolvers;

    /**
     * Constructs the global exception handler with the required Thymeleaf view resolvers.
     *
     * @param viewResolvers the list of view resolvers used to render HTML templates.
     */
    public GlobalExceptionHandler(List<ViewResolver> viewResolvers) {
        this.viewResolvers = viewResolvers;
    }

    /**
     * Handles any uncaught reactive exception, determines the proper HTTP status,
     * and renders the custom error HTML template using Thymeleaf.
     *
     * @param exchange the current server web exchange containing request and response.
     * @param ex       the throwable exception caught by the reactive pipeline.
     * @return a reactive {@link Mono} signaling when the error response rendering is complete.
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "An internal error occurred while processing your request.";

        if (ex instanceof ResponseStatusException responseStatusException) {
            status = (HttpStatus) responseStatusException.getStatusCode();
            errorMessage = responseStatusException.getReason();
        } else if (ex.getCause() instanceof ConnectException || ex instanceof TimeoutException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            errorMessage = "The backend service is currently unavailable. Please try again later.";
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_HTML);

        // Prepare the model attributes to be injected into error.html
        Map<String, Object> model = new HashMap<>();
        model.put("errorDescription", errorMessage);
        model.put("status", status.value());

        // Use ServerResponse to cleanly render the 'error' template via view resolvers
        return ServerResponse.status(status)
                .contentType(MediaType.TEXT_HTML)
                .render("error", model)
                .flatMap(response -> response.writeTo(exchange, new HandlerContext(viewResolvers)));
    }

    /**
     * Helper context class to satisfy view resolution rendering in WebFlux functional flows.
     */
    private static class HandlerContext implements ServerResponse.Context {
        private final List<ViewResolver> viewResolvers;

        public HandlerContext(List<ViewResolver> viewResolvers) {
            this.viewResolvers = viewResolvers;
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return this.viewResolvers;
        }

        @Override
        public List<org.springframework.http.codec.HttpMessageWriter<?>> messageWriters() {
            return java.util.Collections.emptyList();
        }
    }
}