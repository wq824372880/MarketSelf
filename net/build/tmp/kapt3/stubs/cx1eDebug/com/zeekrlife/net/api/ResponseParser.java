package com.zeekrlife.net.api;

import java.lang.System;

/**
 * 描述　: 输入T,输出T,并对code统一判断
 */
@rxhttp.wrapper.annotation.Parser(name = "Response")
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0017\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u0007\b\u0014\u00a2\u0006\u0002\u0010\u0003B\u000f\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0015\u0010\u0007\u001a\u00028\u00002\u0006\u0010\b\u001a\u00020\tH\u0016\u00a2\u0006\u0002\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lcom/zeekrlife/net/api/ResponseParser;", "T", "Lrxhttp/wrapper/parse/TypeParser;", "()V", "type", "Ljava/lang/reflect/Type;", "(Ljava/lang/reflect/Type;)V", "onParse", "response", "Lokhttp3/Response;", "(Lokhttp3/Response;)Ljava/lang/Object;", "net_cx1eDebug"})
public class ResponseParser<T extends java.lang.Object> extends rxhttp.wrapper.parse.TypeParser<T> {
    
    /**
     * 此构造方法适用于任意Class对象，但更多用于带泛型的Class对象，如：List<Student>
     *
     * 用法:
     * Java: .asParser(new ResponseParser<List<Student>>(){})
     * Kotlin: .asParser(object : ResponseParser<List<Student>>() {})
     *
     * 注：此构造方法一定要用protected关键字修饰，否则调用此构造方法将拿不到泛型类型
     */
    protected ResponseParser() {
        super();
    }
    
    /**
     * 此构造方法仅适用于不带泛型的Class对象，如: Student.class
     *
     * 用法
     * Java: .asParser(new ResponseParser<>(Student.class))   或者  .asResponse(Student.class)
     * Kotlin: .asParser(ResponseParser(Student::class.java)) 或者  .asResponse<Student>()
     */
    public ResponseParser(@org.jetbrains.annotations.NotNull
    java.lang.reflect.Type type) {
        super();
    }
    
    @kotlin.jvm.Throws(exceptionClasses = {java.io.IOException.class})
    @java.lang.Override
    public T onParse(@org.jetbrains.annotations.NotNull
    okhttp3.Response response) {
        return null;
    }
}