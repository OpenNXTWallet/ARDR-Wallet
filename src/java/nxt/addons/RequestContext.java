/*
 * Copyright © 2016-2019 Jelurida IP B.V.
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Jelurida B.V.,
 * no part of this software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package nxt.addons;

import nxt.http.callers.GetBlockCall;
import nxt.http.responses.BlockResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class RequestContext extends AbstractContractContext {

    private final HttpServletRequest req;
    private final JO requestParams;
    private BlockResponse blockResponse;

    public RequestContext(HttpServletRequest req, ContractRunnerConfig config, String contractName) {
        super(config, contractName);
        this.source = EventSource.REQUEST;
        this.req = req;
        this.requestParams = new JO();
        req.getParameterMap().forEach((k, v) -> requestParams.put(k, v[0])); // For parameters with multiple values we only take the first one
    }

    @Override
    public BlockResponse getBlock() {
        if (blockResponse != null) {
            return blockResponse;
        }
        blockResponse = GetBlockCall.create().getBlock();
        return blockResponse;
    }

    public JO getRuntimeParams() {
        return requestParams;
    }

    public HttpServletRequest getRequest() {
        return req;
    }

    public Object getAttribute(String name) {
        return req.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return req.getAttributeNames();
    }

    public String getCharacterEncoding() {
        return req.getCharacterEncoding();
    }

    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        req.setCharacterEncoding(enc);
    }

    public int getContentLength() {
        return req.getContentLength();
    }

    public long getContentLengthLong() {
        return req.getContentLengthLong();
    }

    public String getContentType() {
        return req.getContentType();
    }

    public InputStream getInputStream() throws IOException {
        return req.getInputStream();
    }

    public String getParameter(String name) {
        return req.getParameter(name);
    }

    public Map<String, String[]> getParameterMap() {
        return req.getParameterMap();
    }

    public Enumeration<String> getParameterNames() {
        return req.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        return req.getParameterValues(name);
    }

    public String getProtocol() {
        return req.getProtocol();
    }

    public String getScheme() {
        return req.getScheme();
    }

    public String getServerName() {
        return req.getServerName();
    }

    public int getServerPort() {
        return req.getServerPort();
    }

    public BufferedReader getReader() throws IOException {
        return req.getReader();
    }

    public String getRemoteAddr() {
        return req.getRemoteAddr();
    }

    public String getRemoteHost() {
        return req.getRemoteHost();
    }

    public void setAttribute(String name, Object o) {
        req.setAttribute(name, o);
    }

    public void removeAttribute(String name) {
        req.removeAttribute(name);
    }

    public Locale getLocale() {
        return req.getLocale();
    }

    public Enumeration<Locale> getLocales() {
        return req.getLocales();
    }

    public boolean isSecure() {
        return req.isSecure();
    }

    @Override
    protected String getReferencedTransaction() {
        return null;
    }
}
