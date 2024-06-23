package rxhttp.wrapper.param;

import com.zeekrlife.net.api.NetUrl;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rxhttp.RxHttpPlugins;
import rxhttp.wrapper.cahce.CacheMode;
import rxhttp.wrapper.cahce.CacheStrategy;
import rxhttp.wrapper.callback.IConverter;
import rxhttp.wrapper.entity.DownloadOffSize;
import rxhttp.wrapper.entity.ParameterizedTypeImpl;
import rxhttp.wrapper.intercept.CacheInterceptor;
import rxhttp.wrapper.intercept.LogInterceptor;
import rxhttp.wrapper.parse.Parser;
import rxhttp.wrapper.parse.SimpleParser;
import rxhttp.wrapper.utils.LogUtil;

/**
 * Github
 * https://github.com/liujingxing/rxhttp
 * https://github.com/liujingxing/rxlife
 * https://github.com/liujingxing/rxhttp/wiki/FAQ
 * https://github.com/liujingxing/rxhttp/wiki/更新日志
 */
@SuppressWarnings("unchecked")
public class RxHttp<P extends Param, R extends RxHttp> extends BaseRxHttp {
  private long connectTimeoutMillis;

  private long readTimeoutMillis;

  private long writeTimeoutMillis;

  private OkHttpClient realOkClient;

  private OkHttpClient okClient = RxHttpPlugins.getOkHttpClient();

  protected IConverter converter = RxHttpPlugins.getConverter();

  protected boolean isAsync = true;

  protected P param;

  public Request request;

  protected RxHttp(P param) {
    this.param = param;
  }

  public P getParam() {
    return param;
  }

  public R setParam(P param) {
    this.param = param;
    return (R) this;
  }

  public R connectTimeout(long connectTimeout) {
    connectTimeoutMillis = connectTimeout;
    return (R) this;
  }

  public R readTimeout(long readTimeout) {
    readTimeoutMillis = readTimeout;
    return (R) this;
  }

  public R writeTimeout(long writeTimeout) {
    writeTimeoutMillis = writeTimeout;
    return (R) this;
  }

  /**
   * For example:
   *                                          
   * ```                                                  
   * RxHttp.get("/service/%d/...", 1)  
   *     .addQuery("size", 20)
   *     ...
   * ```
   *  url = /service/1/...?size=20
   */
  public static RxHttpNoBodyParam get(String url, Object... formatArgs) {
    return new RxHttpNoBodyParam(Param.get(format(url, formatArgs)));
  }

  public static RxHttpNoBodyParam head(String url, Object... formatArgs) {
    return new RxHttpNoBodyParam(Param.head(format(url, formatArgs)));
  }

  public static RxHttpBodyParam postBody(String url, Object... formatArgs) {
    return new RxHttpBodyParam(Param.postBody(format(url, formatArgs)));
  }

  public static RxHttpBodyParam putBody(String url, Object... formatArgs) {
    return new RxHttpBodyParam(Param.putBody(format(url, formatArgs)));
  }

  public static RxHttpBodyParam patchBody(String url, Object... formatArgs) {
    return new RxHttpBodyParam(Param.patchBody(format(url, formatArgs)));
  }

  public static RxHttpBodyParam deleteBody(String url, Object... formatArgs) {
    return new RxHttpBodyParam(Param.deleteBody(format(url, formatArgs)));
  }

  public static RxHttpFormParam postForm(String url, Object... formatArgs) {
    return new RxHttpFormParam(Param.postForm(format(url, formatArgs)));
  }

  public static RxHttpFormParam putForm(String url, Object... formatArgs) {
    return new RxHttpFormParam(Param.putForm(format(url, formatArgs)));
  }

  public static RxHttpFormParam patchForm(String url, Object... formatArgs) {
    return new RxHttpFormParam(Param.patchForm(format(url, formatArgs)));
  }

  public static RxHttpFormParam deleteForm(String url, Object... formatArgs) {
    return new RxHttpFormParam(Param.deleteForm(format(url, formatArgs)));
  }

  public static RxHttpJsonParam postJson(String url, Object... formatArgs) {
    return new RxHttpJsonParam(Param.postJson(format(url, formatArgs)));
  }

  public static RxHttpJsonParam putJson(String url, Object... formatArgs) {
    return new RxHttpJsonParam(Param.putJson(format(url, formatArgs)));
  }

  public static RxHttpJsonParam patchJson(String url, Object... formatArgs) {
    return new RxHttpJsonParam(Param.patchJson(format(url, formatArgs)));
  }

  public static RxHttpJsonParam deleteJson(String url, Object... formatArgs) {
    return new RxHttpJsonParam(Param.deleteJson(format(url, formatArgs)));
  }

  public static RxHttpJsonArrayParam postJsonArray(String url, Object... formatArgs) {
    return new RxHttpJsonArrayParam(Param.postJsonArray(format(url, formatArgs)));
  }

  public static RxHttpJsonArrayParam putJsonArray(String url, Object... formatArgs) {
    return new RxHttpJsonArrayParam(Param.putJsonArray(format(url, formatArgs)));
  }

  public static RxHttpJsonArrayParam patchJsonArray(String url, Object... formatArgs) {
    return new RxHttpJsonArrayParam(Param.patchJsonArray(format(url, formatArgs)));
  }

  public static RxHttpJsonArrayParam deleteJsonArray(String url, Object... formatArgs) {
    return new RxHttpJsonArrayParam(Param.deleteJsonArray(format(url, formatArgs)));
  }

  public R setUrl(String url) {
    param.setUrl(url);
    return (R) this;
  }

  /**
   * For example:
   *                                          
   * ```                                                  
   * RxHttp.get("/service/{page}/...")  
   *     .addPath("page", 1)
   *     ...
   * ```
   * url = /service/1/...
   */
  public R addPath(String name, Object value) {
    param.addPath(name, value);
    return (R) this;
  }

  public R addEncodedPath(String name, Object value) {
    param.addEncodedPath(name, value);
    return (R) this;
  }

  public R addQuery(String key) {
    param.addQuery(key, null);
    return (R) this;
  }

  public R addEncodedQuery(String key) {
    param.addEncodedQuery(key, null);
    return (R) this;
  }

  public R addQuery(String key, Object value) {
    param.addQuery(key,value);
    return (R) this;
  }

  public R addEncodedQuery(String key, Object value) {
    param.addEncodedQuery(key,value);
    return (R) this;
  }

  public R addAllQuery(String key, List<?> list) {
    param.addAllQuery(key, list);
    return (R) this;
  }

  public R addAllEncodedQuery(String key, List<?> list) {
    param.addAllEncodedQuery(key, list);
    return (R) this;
  }

  public R addAllQuery(Map<String, ?> map) {
    param.addAllQuery(map);
    return (R) this;
  }

  public R addAllEncodedQuery(Map<String, ?> map) {
    param.addAllEncodedQuery(map);
    return (R) this;
  }

  public R addHeader(String line) {
    param.addHeader(line);
    return (R) this;
  }

  public R addHeader(String line, boolean isAdd) {
    if (isAdd) 
        param.addHeader(line);
    return (R) this;
  }

  /**
   * Add a header with the specified name and value. Does validation of header names, allowing non-ASCII values.
   */
  public R addNonAsciiHeader(String key, String value) {
    param.addNonAsciiHeader(key,value);
    return (R) this;
  }

  /**
   * Set a header with the specified name and value. Does validation of header names, allowing non-ASCII values.
   */
  public R setNonAsciiHeader(String key, String value) {
    param.setNonAsciiHeader(key,value);
    return (R) this;
  }

  public R addHeader(String key, String value) {
    param.addHeader(key,value);
    return (R) this;
  }

  public R addHeader(String key, String value, boolean isAdd) {
    if (isAdd)
        param.addHeader(key, value);
    return (R) this;
  }

  public R addAllHeader(Map<String, String> headers) {
    param.addAllHeader(headers);
    return (R) this;
  }

  public R addAllHeader(Headers headers) {
    param.addAllHeader(headers);
    return (R) this;
  }

  public R setHeader(String key, String value) {
    param.setHeader(key,value);
    return (R) this;
  }

  public R setAllHeader(Map<String, String> headers) {
    param.setAllHeader(headers);
    return (R) this;
  }

  public R setRangeHeader(long startIndex) {
    return setRangeHeader(startIndex, -1, false);
  }

  public R setRangeHeader(long startIndex, long endIndex) {
    return setRangeHeader(startIndex, endIndex, false);
  }

  public R setRangeHeader(long startIndex, boolean connectLastProgress) {
    return setRangeHeader(startIndex, -1, connectLastProgress);
  }

  /**
   * 设置断点下载开始/结束位置
   * @param startIndex 断点下载开始位置
   * @param endIndex 断点下载结束位置，默认为-1，即默认结束位置为文件末尾
   * @param connectLastProgress 是否衔接上次的下载进度，该参数仅在带进度断点下载时生效
   */
  public R setRangeHeader(long startIndex, long endIndex, boolean connectLastProgress) {
    param.setRangeHeader(startIndex, endIndex);                         
    if (connectLastProgress)                                            
        param.tag(DownloadOffSize.class, new DownloadOffSize(startIndex));
    return (R) this;                                                    
  }

  public R removeAllHeader(String key) {
    param.removeAllHeader(key);
    return (R) this;
  }

  public R setHeadersBuilder(Headers.Builder builder) {
    param.setHeadersBuilder(builder);
    return (R) this;
  }

  /**
   * 设置单个接口是否需要添加公共参数,
   * 即是否回调通过{@link RxHttpPlugins#setOnParamAssembly(Function)}方法设置的接口,默认为true
   */
  public R setAssemblyEnabled(boolean enabled) {
    param.setAssemblyEnabled(enabled);
    return (R) this;
  }

  /**
   * 设置单个接口是否需要对Http返回的数据进行解码/解密,
   * 即是否回调通过{@link RxHttpPlugins#setResultDecoder(Function)}方法设置的接口,默认为true
   */
  public R setDecoderEnabled(boolean enabled) {
    param.addHeader(Param.DATA_DECRYPT,String.valueOf(enabled));
    return (R) this;
  }

  public boolean isAssemblyEnabled() {
    return param.isAssemblyEnabled();
  }

  public String getUrl() {
    addDefaultDomainIfAbsent();
    return param.getUrl();
  }

  public String getSimpleUrl() {
    return param.getSimpleUrl();
  }

  public String getHeader(String key) {
    return param.getHeader(key);
  }

  public Headers getHeaders() {
    return param.getHeaders();
  }

  public Headers.Builder getHeadersBuilder() {
    return param.getHeadersBuilder();
  }

  public R tag(Object tag) {
    param.tag(tag);
    return (R) this;
  }

  public <T> R tag(Class<? super T> type, T tag) {
    param.tag(type,tag);
    return (R) this;
  }

  public R cacheControl(CacheControl cacheControl) {
    param.cacheControl(cacheControl);
    return (R) this;
  }

  public CacheStrategy getCacheStrategy() {
    return param.getCacheStrategy();
  }

  public R setCacheKey(String cacheKey) {
    param.setCacheKey(cacheKey);
    return (R) this;
  }

  public R setCacheValidTime(long cacheValidTime) {
    param.setCacheValidTime(cacheValidTime);
    return (R) this;
  }

  public R setCacheMode(CacheMode cacheMode) {
    param.setCacheMode(cacheMode);
    return (R) this;
  }

  public Response execute() throws IOException {
    return newCall().execute();
  }

  public <T> T execute(Parser<T> parser) throws IOException {
    return parser.onParse(execute());
  }

  public String executeString() throws IOException {
    return executeClass(String.class);
  }

  public <T> List<T> executeList(Class<T> type) throws IOException {
    Type tTypeList = ParameterizedTypeImpl.get(List.class, type);
    return execute(new SimpleParser<>(tTypeList));
  }

  public <T> T executeClass(Class<T> type) throws IOException {
    return execute(new SimpleParser<>(type));
  }

  @Override
  public final Call newCall() {
    Request request = buildRequest();
    OkHttpClient okClient = getOkHttpClient();
    return okClient.newCall(request);
  }

  public final Request buildRequest() {
    if (request == null) {
        doOnStart();
        request = param.buildRequest();
    }
    return request;
  }

  public OkHttpClient getOkHttpClient() {
    if (realOkClient != null) return realOkClient;
    final OkHttpClient okClient = this.okClient;
    OkHttpClient.Builder builder = null;

    if (LogUtil.isDebug()) {
        builder = okClient.newBuilder();
        builder.addInterceptor(new LogInterceptor(okClient));
    }

    if (connectTimeoutMillis != 0L) {
        if (builder == null) builder = okClient.newBuilder();
        builder.connectTimeout(connectTimeoutMillis, TimeUnit.MILLISECONDS);
    }

    if (readTimeoutMillis != 0L) {
        if (builder == null) builder = okClient.newBuilder();
        builder.readTimeout(readTimeoutMillis, TimeUnit.MILLISECONDS);
    }

    if (writeTimeoutMillis != 0L) {
       if (builder == null) builder = okClient.newBuilder();
       builder.writeTimeout(writeTimeoutMillis, TimeUnit.MILLISECONDS);
    }

    if (param.getCacheMode() != CacheMode.ONLY_NETWORK) {                      
        if (builder == null) builder = okClient.newBuilder();              
        builder.addInterceptor(new CacheInterceptor(getCacheStrategy()));
    }
                                                                            
    realOkClient = builder != null ? builder.build() : okClient;
    return realOkClient;
  }

  /**
   * 请求开始前内部调用，用于添加默认域名等操作
   */
  private final void doOnStart() {
    setConverterToParam(converter);
    addDefaultDomainIfAbsent();
  }

  public R setConverter(IConverter converter) {
    if (converter == null)
        throw new IllegalArgumentException("converter can not be null");
    this.converter = converter;
    return (R) this;
  }

  /**
   * 给Param设置转换器，此方法会在请求发起前，被RxHttp内部调用
   */
  private R setConverterToParam(IConverter converter) {
    param.tag(IConverter.class, converter);
    return (R) this;
  }

  public R setOkClient(OkHttpClient okClient) {
    if (okClient == null) 
        throw new IllegalArgumentException("okClient can not be null");
    this.okClient = okClient;
    return (R) this;
  }

  /**
   * 给Param设置默认域名(如果缺席的话)，此方法会在请求发起前，被RxHttp内部调用
   */
  private void addDefaultDomainIfAbsent() {
    setDomainIfAbsent(NetUrl.BASE_URL);
  }

  public R setDomainToBaseUrlOperateIfAbsent() {
    return setDomainIfAbsent(NetUrl.BASE_URL_OPERATE);
  }

  public R setDomainIfAbsent(String domain) {
    String newUrl = addDomainIfAbsent(param.getSimpleUrl(), domain);
    param.setUrl(newUrl);
    return (R) this;
  }

  private static String addDomainIfAbsent(String url, String domain) {
    if (url.startsWith("http")) return url;
    if (url.startsWith("/")) {
        if (domain.endsWith("/"))
            return domain + url.substring(1);
        else
            return domain + url;
    } else if (domain.endsWith("/")) {
        return domain + url;
    } else {
        return domain + "/" + url;
    }
  }

  /**
   * Returns a formatted string using the specified format string and arguments.
   */
  private static String format(String url, Object... formatArgs) {
    return formatArgs == null || formatArgs.length == 0 ? url : String.format(url, formatArgs);
  }
}
