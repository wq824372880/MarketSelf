
package com.zeekrlife.market.utils.xml;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Objects;
import org.xmlpull.v1.XmlSerializer;

/**
 * Wrapper which delegates all calls through to the given {@link XmlSerializer}.
 * @author Lei.Chen29
 */
public class XmlSerializerWrapper implements XmlSerializer {
    private final XmlSerializer mWrapped;

    public XmlSerializerWrapper(@NonNull XmlSerializer wrapped) {
        mWrapped = Objects.requireNonNull(wrapped);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void setFeature(String name, boolean state) {
        mWrapped.setFeature(name, state);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public boolean getFeature(String name) {
        return mWrapped.getFeature(name);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void setProperty(String name, Object value) {
        mWrapped.setProperty(name, value);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public Object getProperty(String name) {
        return mWrapped.getProperty(name);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void setOutput(OutputStream os, String encoding) throws IOException {
        mWrapped.setOutput(os, encoding);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void setOutput(Writer writer) throws IOException, IllegalArgumentException, IllegalStateException {
        mWrapped.setOutput(writer);
    }

    @Override
    public void startDocument(String encoding, Boolean standalone) throws IOException {
        mWrapped.startDocument(encoding, standalone);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void endDocument() throws IOException {
        mWrapped.endDocument();
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void setPrefix(String prefix, String namespace) throws IOException {
        mWrapped.setPrefix(prefix, namespace);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public String getPrefix(String namespace, boolean generatePrefix) {
        return mWrapped.getPrefix(namespace, generatePrefix);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public int getDepth() {
        return mWrapped.getDepth();
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public String getNamespace() {
        return mWrapped.getNamespace();
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public String getName() {
        return mWrapped.getName();
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public XmlSerializer startTag(String namespace, String name) throws IOException {
        return mWrapped.startTag(namespace, name);
    }

    @Override
    public XmlSerializer attribute(String namespace, String name, String value) throws IOException {
        return mWrapped.attribute(namespace, name, value);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public XmlSerializer endTag(String namespace, String name) throws IOException {
        return mWrapped.endTag(namespace, name);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public XmlSerializer text(String text) throws IOException {
        return mWrapped.text(text);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public XmlSerializer text(char[] buf, int start, int len) throws IOException {
        return mWrapped.text(buf, start, len);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void cdsect(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        mWrapped.cdsect(text);
    }

    @Override
    public void entityRef(String text) throws IOException {
        mWrapped.entityRef(text);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void processingInstruction(String text) throws IOException {
        mWrapped.processingInstruction(text);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void comment(String text) throws IOException {
        mWrapped.comment(text);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void docdecl(String text) throws IOException {
        mWrapped.docdecl(text);
    }
    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void ignorableWhitespace(String text) throws IOException {
        mWrapped.ignorableWhitespace(text);
    }    /**
     * Decodes the provided hexadecimal sequence.
     *
     * @param encoded char array of hexadecimal characters to decode. Letters
     * can be either uppercase or lowercase.
     * @param allowSingleChar If {@code true} odd-length inputs are allowed and
     * the first character is interpreted as the lower bits of the first
     * result byte. If {@code false} odd-length inputs are not allowed.
     * @return the decoded data
     * @throws IllegalArgumentException if the input is malformed
     * @hide
     */
    @Override
    public void flush() throws IOException {
        mWrapped.flush();
    }
}