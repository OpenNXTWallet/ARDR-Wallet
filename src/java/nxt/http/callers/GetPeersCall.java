// Auto generated code, do not modify
package nxt.http.callers;

import nxt.http.APICall;

public class GetPeersCall extends APICall.Builder<GetPeersCall> {
    private GetPeersCall() {
        super("getPeers");
    }

    public static GetPeersCall create() {
        return new GetPeersCall();
    }

    public GetPeersCall includeNewer(boolean includeNewer) {
        return param("includeNewer", includeNewer);
    }

    public GetPeersCall service(String... service) {
        return param("service", service);
    }

    public GetPeersCall active(String active) {
        return param("active", active);
    }

    public GetPeersCall state(String state) {
        return param("state", state);
    }

    public GetPeersCall includePeerInfo(boolean includePeerInfo) {
        return param("includePeerInfo", includePeerInfo);
    }

    public GetPeersCall version(String version) {
        return param("version", version);
    }

    public GetPeersCall connect(String connect) {
        return param("connect", connect);
    }

    public GetPeersCall adminPassword(String adminPassword) {
        return param("adminPassword", adminPassword);
    }
}
