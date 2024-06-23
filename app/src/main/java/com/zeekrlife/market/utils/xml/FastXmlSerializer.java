package com.zeekrlife.market.utils.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import org.xmlpull.v1.XmlSerializer;

/**
 * This is a quick and dirty implementation of XmlSerializer that isn't horribly
 * painfully slow like the normal one.  It only does what is needed for the
 * specific XML files being written with it.
 */
public class FastXmlSerializer implements XmlSerializer {
    private static final String ESCAPE_TABLE[] = new String[] {
        "&#0;", "&#1;", "&#2;", "&#3;", "&#4;", "&#5;", "&#6;", "&#7;",  // 0-7
        "&#8;", "&#9;", "&#10;", "&#11;", "&#12;", "&#13;", "&#14;", "&#15;", // 8-15
        "&#16;", "&#17;", "&#18;", "&#19;", "&#20;", "&#21;", "&#22;", "&#23;", // 16-23
        "&#24;", "&#25;", "&#26;", "&#27;", "&#28;", "&#29;", "&#30;", "&#31;", // 24-31
        null, null, "&quot;", null, null, null, "&amp;", null,   // 32-39
        null, null, null, null, null, null, null, null,   // 40-47
        null, null, null, null, null, null, null, null,   // 48-55
        null, null, null, null, "&lt;", null, "&gt;", null,   // 56-63
    };

    private static final int DEFAULT_BUFFER_LEN = 32 * 1024;

    private static String sSpace = "                                                              ";

    private final int mBufferLen;
    private final char[] mText;
    private int mPos;

    private Writer mWriter;

    private OutputStream mOutputStream;
    private CharsetEncoder mCharset;
    private ByteBuffer mBytes;

    private boolean mIndent = false;
    private boolean mInTag;

    private int mNesting = 0;
    private boolean mLineStart = true;

    public FastXmlSerializer() {
        this(DEFAULT_BUFFER_LEN);
    }

    /**
     * Allocate a FastXmlSerializer with the given internal output buffer size.  If the
     * size is zero or negative, then the default buffer size will be used.
     *
     * @param bufferSize Size in bytes of the in-memory output buffer that the writer will use.
     */
    public FastXmlSerializer(int bufferSize) {
        mBufferLen = (bufferSize > 0) ? bufferSize : DEFAULT_BUFFER_LEN;
        mText = new char[mBufferLen];
        mBytes = ByteBuffer.allocate(mBufferLen);
    }

    /**
     * 将指定的字符追加到缓冲区中。如果缓冲区将满，则先刷新缓冲区，然后继续追加。
     *
     * @param c 要追加的字符。
     * @throws IOException 如果在刷新缓冲区时发生输入输出错误。
     */
    private void append(char c) throws IOException {
        int pos = mPos; // 获取当前写入位置
        // 检查缓冲区是否将满，如果将满，则先刷新缓冲区
        if (pos >= (mBufferLen - 1)) {
            flush();
            pos = mPos; // 刷新后重新获取写入位置
        }
        mText[pos] = c; // 将字符写入到缓冲区的当前位置
        mPos = pos + 1; // 更新下一个写入位置
    }


    /**
     * 将字符串的一部分追加到某个目标中。如果待追加内容长度超过缓冲区长度，则会分段追加。
     *
     * @param str 要追加的字符串
     * @param i 要追加的字符串的开始索引
     * @param length 要追加的字符串的长度
     * @throws IOException 如果在追加过程中发生IO错误
     */
    private void append(String str, int i, final int length) throws IOException {
        // 如果要追加的长度超过缓冲区长度，分段追加
        if (length > mBufferLen) {
            final int end = i + length;
            while (i < end) {
                int next = i + mBufferLen;
                // 递归调用，分段处理
                append(str, i, next < end ? mBufferLen : (end - i));
                i = next;
            }
            return;
        }
        int pos = mPos;
        // 如果追加后超出缓冲区，先刷新缓冲区
        if ((pos + length) > mBufferLen) {
            flush();
            pos = mPos;
        }
        // 将字符串部分字符追加到缓冲区
        str.getChars(i, i + length, mText, pos);
        mPos = pos + length; // 更新位置
    }


    /**
     * 将字符数组的一部分追加到内部缓冲区中。
     * 如果待追加的内容超出了缓冲区的剩余空间，将先尝试通过调用flush()方法清空缓冲区，
     * 如果还是无法容纳，则会拆分待追加的内容，分多次进行追加。
     *
     * @param buf 要追加的字符数组
     * @param i 起始索引，从buf的此位置开始读取字符
     * @param length 要追加的字符数量
     * @throws IOException 如果在执行过程中发生I/O错误
     */
    private void append(char[] buf, int i, final int length) throws IOException {
        // 如果待追加的长度超过了缓冲区的剩余空间，需拆分并递归处理
        if (length > mBufferLen) {
            final int end = i + length;
            while (i < end) {
                int next = i + mBufferLen;
                append(buf, i, next < end ? mBufferLen : (end - i));
                i = next;
            }
            return;
        }

        // 确定追加位置并检查是否有足够的空间
        int pos = mPos;
        if ((pos + length) > mBufferLen) {
            flush(); // 清空缓冲区以腾出空间
            pos = mPos; // 重新确定位置
        }

        // 将字符从源数组复制到缓冲区
        System.arraycopy(buf, i, mText, pos, length);
        mPos = pos + length; // 更新位置
    }


    private void append(String str) throws IOException {
        append(str, 0, str.length());
    }

    /**
     * 向当前目标中追加指定数量的缩进。这个方法通过将给定的缩进级别转换为对应数量的空格来实现。
     * 注意，如果指定的缩进级别超过了预定义的最大缩进长度，则只追加最大缩进长度的空格。
     *
     * @param indent 需要追加的缩进级别。每个缩进级别等同于4个空格。
     * @throws IOException 如果在追加过程中发生IO异常。
     */
    private void appendIndent(int indent) throws IOException {
        // 将缩进级别转换为对应的空格数
        indent *= 4;
        // 确保不会追加超过预定义最大长度的空格
        if (indent > sSpace.length()) {
            indent = sSpace.length();
        }
        // 追加指定数量的空格
        append(sSpace, 0, indent);
    }

    /**
     * 将输入字符串中的特定字符转义后追加到目标中。
     * 此方法针对字符串中的每个字符检查其是否需要转义，如果需要，则使用预定义的转义表进行转义，并将转义后的字符或原始字符追加到目标字符串。
     *
     * @param string 需要进行转义处理的原始字符串。
     * @throws IOException 如果在追加字符串过程中发生IO异常。
     */
    private void escapeAndAppendString(final String string) throws IOException {
        // 定义字符串的长度，转义表的最大字符值，以及转义表
        final int N = string.length();
        final char NE = (char) ESCAPE_TABLE.length;
        final String[] escapes = ESCAPE_TABLE;

        // 记录上一次转义或追加的位置
        int lastPos = 0;
        int pos;

        // 遍历字符串中的每个字符
        for (pos = 0; pos < N; pos++) {
            char c = string.charAt(pos);

            // 如果字符超出转义表范围，则继续下一个字符
            if (c >= NE) {
                continue;
            }

            String escape = escapes[c];

            // 如果字符在转义表中有定义
            if (escape != null) {
                // 如果有未转义或追加的字符，则先追加这部分
                if (lastPos < pos) {
                    append(string, lastPos, pos - lastPos);
                }
                lastPos = pos + 1; // 更新最后一次转义或追加的位置
                append(escape); // 追加转义序列
            }
        }

        // 处理字符串末尾未转义或追加的部分
        if (lastPos < pos) {
            append(string, lastPos, pos - lastPos);
        }
    }

    /**
     * 将指定字符数组中的一部分字符进行转义后追加到目标中。
     * 该方法主要用于处理字符数组中需要特殊处理的字符（例如，非打印字符或特定的控制字符），
     * 通过查找转义表并将对应的字符转义后追加到输出中。
     *
     * @param buf 待处理的字符数组。
     * @param start 处理的起始位置。
     * @param len 处理的字符长度。
     * @throws IOException 在追加字符到目标时发生IO错误。
     */
    private void escapeAndAppendString(char[] buf, int start, int len) throws IOException {
        // 定义转义表未覆盖的字符上限
        final char NE = (char) ESCAPE_TABLE.length;
        // 转义表
        final String[] escapes = ESCAPE_TABLE;
        int end = start + len;
        int lastPos = start;
        int pos;

        // 遍历字符数组，对每个字符进行转义处理
        for (pos = start; pos < end; pos++) {
            char c = buf[pos];
            // 如果字符在转义表索引范围之外，则跳过该字符
            if (c >= NE) {
                continue;
            }
            // 获取字符c对应的转义序列
            String escape = escapes[c];
            // 如果没有对应的转义序列，则跳过该字符
            if (escape == null) {
                continue;
            }
            // 如果存在未处理的字符，则将其追加到输出
            if (lastPos < pos) {
                append(buf, lastPos, pos - lastPos);
            }
            lastPos = pos + 1;
            // 将转义序列追加到输出
            append(escape);
        }
        // 处理最后一段未转义的字符
        if (lastPos < pos) {
            append(buf, lastPos, pos - lastPos);
        }
    }

    /**
     * 向当前 XML 序列器添加一个属性。
     * 这个方法会按照 XML 属性的格式，添加指定的属性到正在序列化的对象中。
     * 属性的格式为：[命名空间:]名称="值"。
     *
     * @param namespace 属性的命名空间。如果该属性没有命名空间，则传入 null。
     * @param name 属性的名称。
     * @param value 属性的值。
     * @return 返回当前的 XmlSerializer 实例，支持链式调用。
     * @throws IOException 如果在序列化过程中发生 IO 错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不合法的状态下调用此方法（例如，在开始标签之外调用）。
     */
    @Override
    public XmlSerializer attribute(String namespace, String name, String value)
        throws IOException, IllegalArgumentException, IllegalStateException {
        // 在属性名之前添加一个空格
        append(' ');
        // 如果存在命名空间，则添加命名空间和冒号
        if (namespace != null) {
            append(namespace);
            append(':');
        }
        // 添加属性名
        append(name);
        append("=\"");

        // 对属性值进行转义并添加到序列化输出中
        escapeAndAppendString(value);
        append('"');
        // 重置行开始标志为 false，表示当前输出行已被占用
        mLineStart = false;
        return this;
    }


    /**
     * 进行目录切换操作。
     * 该方法旨在实现目录切换功能，但当前实现抛出 UnsupportedOperationException。
     *
     * @param text 指定要切换到的目录的文本路径。
     * @throws IOException 如果在切换目录时发生 I/O 错误。
     * @throws IllegalArgumentException 如果提供的路径参数不合法。
     * @throws IllegalStateException 如果在当前状态下无法执行目录切换操作。
     * @throws UnsupportedOperationException 在当前版本中，此方法的功能未实现。
     */
    @Override
    public void cdsect(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    /**
     * 此方法用于添加注释，但当前实现抛出UnsupportedOperationException异常。
     * 试图调用此方法将导致操作不可支持的异常。
     *
     * @param text 要添加的注释文本。此参数为字符串类型，用于指定要添加的注释内容。
     * @throws IOException 如果在注释过程中发生I/O错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不合法的状态下调用此方法。
     */
    @Override
    public void comment(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     * @param text 文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void docdecl(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void endDocument() throws IOException, IllegalArgumentException, IllegalStateException {
        flush();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public XmlSerializer endTag(String namespace, String name) throws IOException, IllegalArgumentException, IllegalStateException {
        mNesting--;
        if (mInTag) {
            append(" />\n");
        } else {
            if (mIndent && mLineStart) {
                appendIndent(mNesting);
            }
            append("</");
            if (namespace != null) {
                append(namespace);
                append(':');
            }
            append(name);
            append(">\n");
        }
        mLineStart = true;
        mInTag = false;
        return this;
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void entityRef(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    private void flushBytes() throws IOException {
        int position;
        if ((position = mBytes.position()) > 0) {
            mBytes.flip();
            mOutputStream.write(mBytes.array(), 0, position);
            mBytes.clear();
        }
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void flush() throws IOException {
        //Log.i("PackageManager", "flush mPos=" + mPos);
        if (mPos > 0) {
            if (mOutputStream != null) {
                CharBuffer charBuffer = CharBuffer.wrap(mText, 0, mPos);
                CoderResult result = mCharset.encode(charBuffer, mBytes, true);
                while (true) {
                    if (result.isError()) {
                        throw new IOException(result.toString());
                    } else if (result.isOverflow()) {
                        flushBytes();
                        result = mCharset.encode(charBuffer, mBytes, true);
                        continue;
                    }
                    break;
                }
                flushBytes();
                mOutputStream.flush();
            } else {
                mWriter.write(mText, 0, mPos);
                mWriter.flush();
            }
            mPos = 0;
        }
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public int getDepth() {
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public boolean getFeature(String name) {
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public String getNamespace() {
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public String getPrefix(String namespace, boolean generatePrefix) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public Object getProperty(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void ignorableWhitespace(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void processingInstruction(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void setFeature(String name, boolean state) throws IllegalArgumentException, IllegalStateException {
        String tempName = "http://xmlpull.org/v1/doc/features.html#indent-output";
        if (tempName.equals(name)) {
            mIndent = true;
            return;
        }
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void setOutput(OutputStream os, String encoding) throws IOException, IllegalArgumentException, IllegalStateException {
        if (os == null) {
            throw new IllegalArgumentException();
        }
        if (true) {
            try {
                mCharset = Charset.forName(encoding)
                    .newEncoder()
                    .onMalformedInput(CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(CodingErrorAction.REPLACE);
            } catch (IllegalCharsetNameException e) {
                throw (UnsupportedEncodingException) (new UnsupportedEncodingException(encoding).initCause(e));
            } catch (UnsupportedCharsetException e) {
                throw (UnsupportedEncodingException) (new UnsupportedEncodingException(encoding).initCause(e));
            }
            mOutputStream = os;
        } else {
            setOutput(encoding == null ? new OutputStreamWriter(os) : new OutputStreamWriter(os, encoding));
        }
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void setOutput(Writer writer) throws IOException, IllegalArgumentException, IllegalStateException {
        mWriter = writer;
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void setPrefix(String prefix, String namespace) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void setProperty(String name, Object value) throws IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public void startDocument(String encoding, Boolean standalone) throws IOException, IllegalArgumentException, IllegalStateException {
        append("<?xml version='1.0' encoding='utf-8'");
        if (standalone != null) {
            append(" standalone='" + (standalone ? "yes" : "no") + "'");
        }
        append(" ?>\n");
        mLineStart = true;
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public XmlSerializer startTag(String namespace, String name) throws IOException, IllegalArgumentException, IllegalStateException {
        if (mInTag) {
            append(">\n");
        }
        if (mIndent) {
            appendIndent(mNesting);
        }
        mNesting++;
        append('<');
        if (namespace != null) {
            append(namespace);
            append(':');
        }
        append(name);
        mInTag = true;
        mLineStart = false;
        return this;
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public XmlSerializer text(char[] buf, int start, int len) throws IOException, IllegalArgumentException, IllegalStateException {
        if (mInTag) {
            append(">");
            mInTag = false;
        }
        escapeAndAppendString(buf, start, len);
        if (mIndent) {
            mLineStart = buf[start + len - 1] == '\n';
        }
        return this;
    }
    /**
     * 处理文档声明的函数。
     * 该方法的实现抛出了UnsupportedOperationException，意味着在当前情况下不支持此操作。
     *   文档声明的文本内容。
     * @throws IOException 如果发生输入/输出错误。
     * @throws IllegalArgumentException 如果提供的参数不合法。
     * @throws IllegalStateException 如果在不正确的状态下调用此方法。
     */
    @Override
    public XmlSerializer text(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        if (mInTag) {
            append(">");
            mInTag = false;
        }
        escapeAndAppendString(text);
        if (mIndent) {
            mLineStart = text.length() > 0 && (text.charAt(text.length() - 1) == '\n');
        }
        return this;
    }
}