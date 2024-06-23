package com.zeekrlife.market.utils.xml;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Wrapper which delegates all calls through to the given {@link XmlPullParser}.
 * @author Lei.Chen29
 */
public class XmlPullParserWrapper implements XmlPullParser {
    private final XmlPullParser mWrapped;

    public XmlPullParserWrapper(@NonNull XmlPullParser wrapped) {
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
    public void setFeature(String name, boolean state) throws XmlPullParserException {
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
    public void setProperty(String name, Object value) throws XmlPullParserException {
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
    public void setInput(Reader in) throws XmlPullParserException {
        mWrapped.setInput(in);
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
    public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
        mWrapped.setInput(inputStream, inputEncoding);
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
    public String getInputEncoding() {
        return mWrapped.getInputEncoding();
    }

    @Override
    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
        mWrapped.defineEntityReplacementText(entityName, replacementText);
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
    public int getNamespaceCount(int depth) throws XmlPullParserException {
        return mWrapped.getNamespaceCount(depth);
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
    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        return mWrapped.getNamespacePrefix(pos);
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
    public String getNamespaceUri(int pos) throws XmlPullParserException {
        return mWrapped.getNamespaceUri(pos);
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
    public String getNamespace(String prefix) {
        return mWrapped.getNamespace(prefix);
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
    public String getPositionDescription() {
        return mWrapped.getPositionDescription();
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
    public int getLineNumber() {
        return mWrapped.getLineNumber();
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
    public int getColumnNumber() {
        return mWrapped.getColumnNumber();
    }

    @Override
    public boolean isWhitespace() throws XmlPullParserException {
        return mWrapped.isWhitespace();
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
    public String getText() {
        return mWrapped.getText();
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
    public char[] getTextCharacters(int[] holderForStartAndLength) {
        return mWrapped.getTextCharacters(holderForStartAndLength);
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
    public String getPrefix() {
        return mWrapped.getPrefix();
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
    public boolean isEmptyElementTag() throws XmlPullParserException {
        return mWrapped.isEmptyElementTag();
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
    public int getAttributeCount() {
        return mWrapped.getAttributeCount();
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
    public String getAttributeNamespace(int index) {
        return mWrapped.getAttributeNamespace(index);
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
    public String getAttributeName(int index) {
        return mWrapped.getAttributeName(index);
    }

    @Override
    public String getAttributePrefix(int index) {
        return mWrapped.getAttributePrefix(index);
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
    public String getAttributeType(int index) {
        return mWrapped.getAttributeType(index);
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
    public boolean isAttributeDefault(int index) {
        return mWrapped.isAttributeDefault(index);
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
    public String getAttributeValue(int index) {
        return mWrapped.getAttributeValue(index);
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
    public String getAttributeValue(String namespace, String name) {
        return mWrapped.getAttributeValue(namespace, name);
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
    public int getEventType() throws XmlPullParserException {
        return mWrapped.getEventType();
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
    public int next() throws XmlPullParserException, IOException {
        return mWrapped.next();
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
    public int nextToken() throws XmlPullParserException, IOException {
        return mWrapped.nextToken();
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
    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        mWrapped.require(type, namespace, name);
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
    public String nextText() throws XmlPullParserException, IOException {
        return mWrapped.nextText();
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
    public int nextTag() throws XmlPullParserException, IOException {
        return mWrapped.nextTag();
    }
}