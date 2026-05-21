package com.drive.keychain.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChainConstants {


    // constants for HTTP client
    public final String HTTP_REQUEST_ACCEPT = "Accept";
    public final String HTTP_REQUEST_AUTHORIZATION = "Authorization";
    public final String HTTP_METHOD_POST = "POST";
    public final String HTTP_REQUEST_CONTENT_TYPE = "Content-Type";
    public final String HTTP_METHOD_PUT = "PUT";

    public final String UTF8 = "UTF-8";
    
    // Example external API used by agents/tests
    public final String JSONPLACEHOLDER_TODO_1 = "https://jsonplaceholder.typicode.com/todos/1";
}
