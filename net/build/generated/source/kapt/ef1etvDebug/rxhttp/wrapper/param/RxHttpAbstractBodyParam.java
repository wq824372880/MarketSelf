package rxhttp.wrapper.param;

import rxhttp.wrapper.BodyParamFactory;
import rxhttp.wrapper.param.AbstractBodyParam;

/**
 * Github
 * https://github.com/liujingxing/rxhttp
 * https://github.com/liujingxing/rxlife
 * https://github.com/liujingxing/rxhttp/wiki/FAQ
 * https://github.com/liujingxing/rxhttp/wiki/更新日志
 */
@SuppressWarnings("unchecked")
public class RxHttpAbstractBodyParam<P extends AbstractBodyParam<P>, R extends RxHttpAbstractBodyParam<P, R>> 
    extends RxHttp<P, R> implements BodyParamFactory {

    protected RxHttpAbstractBodyParam(P param) {
        super(param);
    }

    public final R setUploadMaxLength(long maxLength) {
        param.setUploadMaxLength(maxLength);
        return (R) this;
    }
}