package org.erlide.engine.internal.services.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.erlide.runtime.rpc.IOtpRpc;
import org.erlide.runtime.rpc.RpcException;
import org.erlide.util.ErlLogger;
import org.erlide.util.Util;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;

public class ErlideXref {
    private static final String ERLIDE_XREF = "erlide_xref";

    public static void addDirs(final IOtpRpc backend, final Collection<String> dirs) {
        try {
            backend.call(ErlideXref.ERLIDE_XREF, "add_dirs", "ls", dirs);
        } catch (final RpcException e) {
            ErlLogger.warn(e);
        }
    }

    public static List<String> modules(final IOtpRpc backend) {
        final List<String> result = new ArrayList<>();
        try {
            final OtpErlangObject res = backend.call(ErlideXref.ERLIDE_XREF, "modules",
                    "");
            if (Util.isOk(res)) {
                final OtpErlangTuple t = (OtpErlangTuple) res;
                final OtpErlangList l = (OtpErlangList) t.elementAt(1);
                for (final OtpErlangObject i : l) {
                    if (i instanceof OtpErlangAtom) {
                        final OtpErlangAtom m = (OtpErlangAtom) i;
                        result.add(m.atomValue());
                    }
                }
            }
        } catch (final RpcException e) {
            ErlLogger.error(e);
        }
        return result;
    }

    public static void setScope(final IOtpRpc backend, final List<String> scope) {
        final List<String> mods = ErlideXref.modules(backend);
        ErlideXref.removeModules(backend, mods);
        ErlideXref.addDirs(backend, scope);
    }

    private static void removeModules(final IOtpRpc backend, final List<String> mods) {
        try {
            backend.call(ErlideXref.ERLIDE_XREF, "remove_modules", "ls", mods);
        } catch (final RpcException e) {
            ErlLogger.error(e);
        }
    }

}
