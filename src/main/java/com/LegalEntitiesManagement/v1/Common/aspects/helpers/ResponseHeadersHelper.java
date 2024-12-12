package com.LegalEntitiesManagement.v1.Common.aspects.helpers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseHeadersHelper {
    private static final String CONTENT_TYPE = "application/json";
    private static final String CACHE_CONTROL_VALUE = "no-cache";

    /**
     * Get base headers included in all responses
     * @return HttpHeaders with base configuration
     */
    public static HttpHeaders getBaseHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
        headers.add(HttpHeaders.DATE, getCurrentHttpDate());
        return headers;
    }

    /**
     * Get headers for GET and PUT success responses
     * @return HttpHeaders with cache control
     */
    public static HttpHeaders getSuccessGetPutHeaders() {
        HttpHeaders headers = getBaseHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL_VALUE);
        return headers;
    }

    /**
     * Get headers for POST success responses
     * @param resourceUrl URL of the created resource
     * @return HttpHeaders with Location header
     */
    public static HttpHeaders getSuccessPostHeaders(String resourceUrl) {
        HttpHeaders headers = getBaseHeaders();
        headers.add(HttpHeaders.LOCATION, resourceUrl);
        return headers;
    }

    /**
     * Get headers for error responses
     * @param status HTTP status code
     * @param errorCode Application-specific error code
     * @return HttpHeaders with error information
     */
    public static HttpHeaders getErrorHeaders(HttpStatus status, String errorCode) {
        HttpHeaders headers = getBaseHeaders();
        headers.add("X-Error-Code", errorCode);
        return headers;
    }

    /**
     * Get current date in HTTP date format
     * @return Formatted date string
     */
    private static String getCurrentHttpDate() {
        return DateTimeFormatter
                .RFC_1123_DATE_TIME
                .format(ZonedDateTime.now(ZoneOffset.UTC));
    }
}
