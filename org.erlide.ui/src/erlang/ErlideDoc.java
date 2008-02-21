package erlang;

import org.erlide.jinterface.rpc.RpcException;
import org.erlide.runtime.backend.BackendManager;
import org.erlide.runtime.backend.IBackend;
import org.erlide.runtime.backend.exceptions.BackendException;
import org.erlide.runtime.backend.exceptions.ErlangRpcException;

import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;

public class ErlideDoc {

	public static  OtpErlangObject getExported(final IBackend b, String prefix,
			final String mod) throws ErlangRpcException, BackendException,
			RpcException {
		final OtpErlangObject res = b.rpcx("erlide_otp_doc",
				"get_exported", "as", mod, prefix);
		return res;
	}

	public static OtpErlangObject getFunDoc(OtpErlangList list, String mod,
			final String s) throws ErlangRpcException, BackendException,
			RpcException {
		final OtpErlangObject r1 = BackendManager.getDefault()
				.getIdeBackend().rpcx("erlide_otp_doc",
						"get_doc_from_fun_arity_list", "axs", mod, list, s);
		return r1;
	}

	@SuppressWarnings("boxing")
	public
	static OtpErlangObject getDocFromScan(int offset, String s,
			String module, OtpErlangList imports) throws ErlangRpcException,
			BackendException, RpcException {
		OtpErlangObject r1;
		r1 = BackendManager.getDefault().getIdeBackend()
				.rpcx("erlide_otp_doc", "get_doc_from_scan_tuples", "ailxs",
						module, offset, imports, s);
		return r1;
	}

}
