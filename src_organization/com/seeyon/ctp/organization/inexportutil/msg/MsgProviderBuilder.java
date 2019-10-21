package com.seeyon.ctp.organization.inexportutil.msg;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.seeyon.ctp.common.i18n.ResourceBundleUtil;
import com.seeyon.ctp.common.i18n.ResourceUtil;

public class MsgProviderBuilder {
	// op
	public static final String ORG_IO_MSG_OP_OK = "ORG_IO_MSG_OP_OK";
	public static final String ORG_IO_MSG_OP_FAILED = "ORG_IO_MSG_OP_FAILED";
	public static final String ORG_IO_MSG_OP_IGNORED = "ORG_IO_MSG_OP_IGNORED";

	// name
	public static final String ORG_IO_MSG_NAME_REG = "ORG_IO_MSG_NAME_REG";
	public static final String ORG_IO_MSG_NAME_LOGINNAME = "ORG_IO_MSG_NAME_LOGINNAME";

	// alert
	public static final String ORG_IO_MSG_ALERT_INACCOUNT = "ORG_IO_MSG_ALERT_INACCOUNT";
	public static final String ORG_IO_MSG_ALERT_INOTHERACCOUNT = "ORG_IO_MSG_ALERT_INOTHERACCOUNT";
	public static final String ORG_IO_MSG_ALERT_NOBELONGCURRENTACCOUNT = "ORG_IO_MSG_ALERT_NOBELONGCURRENTACCOUNT";
	public static final String ORG_IO_MSG_ALERT_IGNORED4DOUBLE = "ORG_IO_MSG_ALERT_IGNORED4DOUBLE";

	// ok
	public static final String ORG_IO_MSG_OK_ADD = "ORG_IO_MSG_OK_ADD";
	public static final String ORG_IO_MSG_OK_UPDATE = "ORG_IO_MSG_OK_UPDATE";

	// error
	public static final String ORG_IO_MSG_ERROR_EXCEPTION = "ORG_IO_MSG_ERROR_EXCEPTION";
	public static final String ORG_IO_MSG_ERROR_FILEDATA = "ORG_IO_MSG_ERROR_FILEDATA";

	public static final String ORG_IO_MSG_ERROR_MUST_ACCOUNT = "ORG_IO_MSG_ERROR_MUST_ACCOUNT";

	public static final String ORG_IO_MSG_ERROR_MUST_DEP = "ORG_IO_MSG_ERROR_MUST_DEP";
	public static final String ORG_IO_MSG_ERROR_NOMATCH_DEP = "ORG_IO_MSG_ERROR_NOMATCH_DEP";

	public static final String ORG_IO_MSG_ERROR_MUST_LEV = "ORG_IO_MSG_ERROR_MUST_LEV";
	public static final String ORG_IO_MSG_ERROR_NOMATCH_LEV = "ORG_IO_MSG_ERROR_NOMATCH_LEV";

	public static final String ORG_IO_MSG_ERROR_MUST_PPOST = "ORG_IO_MSG_ERROR_MUST_PPOST";
	public static final String ORG_IO_MSG_ERROR_NOMATCH_PPOST = "ORG_IO_MSG_ERROR_NOMATCH_PPOST";
	public static final String ORG_IO_MSG_ERROR_MUST_POSTNAME = "ORG_IO_MSG_ERROR_MUST_POSTNAME";
	public static final String ORG_IO_MSG_ERROR_MUST_POSTTYPE = "ORG_IO_MSG_ERROR_MUST_POSTTYPE";
	public static final String ORG_IO_MSG_ERROR_NOMATCH_POSTTYPE = "ORG_IO_MSG_ERROR_NOMATCH_POSTTYPE";
	public static final String ORG_IO_MSG_ERROR_DOUBLESAMEFILE_POSTNAME = "ORG_IO_MSG_ERROR_DOUBLESAMEFILE_POSTNAME";

	public static final String ORG_IO_MSG_ERROR_MUST_MEMBERNAME = "ORG_IO_MSG_ERROR_MUST_MEMBERNAME";
	public static final String ORG_IO_MSG_ERROR_MUST_LOGINNAME = "ORG_IO_MSG_ERROR_MUST_LOGINNAME";
	public static final String ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME = "ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME";
	public static final String ORG_IO_MSG_ERROR_DOUBLESAMEFILE_CODE = "ORG_IO_MSG_ERROR_DOUBLESAMEFILE_CODE";
	public static final String ORG_IO_MSG_ERROR_EXT_MEMBER_WORKSCOPE_NO = "ORG_IO_MSG_ERROR_EXT_MEMBER_WORKSCOPE_NO";

	private static final Map<String, String> msgs = new HashMap<String, String>();

	static {
		msgs.put(MsgContants.ORG_IO_MSG_OP_OK, ORG_IO_MSG_OP_OK);
		msgs.put(MsgContants.ORG_IO_MSG_OP_FAILED, ORG_IO_MSG_OP_FAILED);
		msgs.put(MsgContants.ORG_IO_MSG_OP_IGNORED, ORG_IO_MSG_OP_IGNORED);

		msgs.put(MsgContants.ORG_IO_MSG_NAME_REG, ORG_IO_MSG_NAME_REG);
		msgs.put(MsgContants.ORG_IO_MSG_NAME_LOGINNAME,
				ORG_IO_MSG_NAME_LOGINNAME);

		msgs.put(MsgContants.ORG_IO_MSG_ALERT_INACCOUNT,
				ORG_IO_MSG_ALERT_INACCOUNT);
		msgs.put(MsgContants.ORG_IO_MSG_ALERT_INOTHERACCOUNT,
				ORG_IO_MSG_ALERT_INOTHERACCOUNT);
		msgs.put(MsgContants.ORG_IO_MSG_ALERT_NOBELONGCURRENTACCOUNT,
				ORG_IO_MSG_ALERT_NOBELONGCURRENTACCOUNT);
		msgs.put(MsgContants.ORG_IO_MSG_ALERT_IGNORED4DOUBLE,
				ORG_IO_MSG_ALERT_IGNORED4DOUBLE);

		msgs.put(MsgContants.ORG_IO_MSG_OK_ADD, ORG_IO_MSG_OK_ADD);
		msgs.put(MsgContants.ORG_IO_MSG_OK_UPDATE, ORG_IO_MSG_OK_UPDATE);

		msgs.put(MsgContants.ORG_IO_MSG_ERROR_EXCEPTION,
				ORG_IO_MSG_ERROR_EXCEPTION);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_FILEDATA,
				ORG_IO_MSG_ERROR_FILEDATA);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_ACCOUNT,
				ORG_IO_MSG_ERROR_MUST_ACCOUNT);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_DEP,
				ORG_IO_MSG_ERROR_MUST_DEP);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_DEP,
				ORG_IO_MSG_ERROR_NOMATCH_DEP);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_LEV,
				ORG_IO_MSG_ERROR_MUST_LEV);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_LEV,
				ORG_IO_MSG_ERROR_NOMATCH_LEV);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_PPOST,
				ORG_IO_MSG_ERROR_MUST_PPOST);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_PPOST,
				ORG_IO_MSG_ERROR_NOMATCH_PPOST);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_POSTTYPE,
				ORG_IO_MSG_ERROR_MUST_POSTTYPE);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_POSTTYPE,
				ORG_IO_MSG_ERROR_NOMATCH_POSTTYPE);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_POSTNAME,
				ORG_IO_MSG_ERROR_DOUBLESAMEFILE_POSTNAME);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_MEMBERNAME,
				ORG_IO_MSG_ERROR_MUST_MEMBERNAME);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_LOGINNAME,
				ORG_IO_MSG_ERROR_MUST_LOGINNAME);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME,
				ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_CODE,
				ORG_IO_MSG_ERROR_DOUBLESAMEFILE_CODE);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_EXT_MEMBER_WORKSCOPE_NO,
				ORG_IO_MSG_ERROR_EXT_MEMBER_WORKSCOPE_NO);
	}

	static String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";

	static MsgProviderBuilder mpb = new MsgProviderBuilder();

	static public MsgProviderBuilder getInstance() {
		return mpb;
	}

	public MsgProvider createMsgProvider() {
		return new MapMsgProvider();
	}

	public MsgProvider createMsgProvider(Locale local) {
		if (local == null)
			return createMsgProvider();

		OrgResourceBundleMsgProvider rbmp = new OrgResourceBundleMsgProvider();
		rbmp.setLocal(local);

		return rbmp;
	}

	public MsgProvider createMsgProvider(String res, Locale local) {
		if (StringUtils.hasText(res)) {
			ResourceBundleMsgProvider rbmp = new ResourceBundleMsgProvider();
			rbmp.setRes(res);

			return rbmp;
		}

		return createMsgProvider(local);
	}

	public class MapMsgProvider implements MsgProvider {

		public String getMsg(String key) {
			return ResourceUtil.getString(msgs.get(key));
		}
	}

	public class OrgResourceBundleMsgProvider implements MsgProvider {
		protected Locale local;

		public String getMsg(String key) {
			String ret = null;
			try {
				ret = ResourceBundleUtil.getString(this.getResource(), local,
						key);
			} catch (Exception e) {

			}
			if (null == ret) {
				ret = msgs.get(key);
			}

			return ret;
		}

		public Locale getLocal() {
			return local;
		}

		public void setLocal(Locale local) {
			this.local = local;
		}

		public String getResource() {
			return resource;
		}

	}

	public class ResourceBundleMsgProvider extends OrgResourceBundleMsgProvider {
		private String res;

		public String getResource() {
			return this.res;
		}

		public String getRes() {
			return this.res;
		}

		public void setRes(String val) {
			this.res = val;
		}

	}
}// end class
