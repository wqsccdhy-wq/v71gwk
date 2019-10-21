package v71gwk;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.seeyon.ctp.common.security.MessageEncoder;
import com.seeyon.ctp.common.security.SecurityHelper;

/**
 * 测试文件
 */
public class Test {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		// TODO Auto-generated method stub
		
		 MessageEncoder encode = getMessageEncoder();
		 String encode2 = encode.encode("bgt.zxl", "112233");
		 String encode3 = encode.encode("bgt.zxl", encode2);
		 System.out.println(encode2);
		 System.out.println(encode3);
		 // nhuz8J7iBGltbpjYANspt6EScSU=

	}
	
	public static MessageEncoder getMessageEncoder() throws NoSuchAlgorithmException, NoSuchProviderException {
		boolean isSM3= false;
		MessageEncoder encode=null;
		//isSM3 = SecurityHelper.isSM3CryptPassword();
		if(isSM3){
			 encode = new MessageEncoder("SM3","BC");
		}else{
			 encode = new MessageEncoder();
		}
		return encode;
	}

}
