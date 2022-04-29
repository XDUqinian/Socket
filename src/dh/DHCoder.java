
package dh;  
  
import java.security.Key;  
import java.security.KeyFactory;  
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.PrivateKey;  
import java.security.PublicKey;  
import java.security.spec.PKCS8EncodedKeySpec;  
import java.security.spec.X509EncodedKeySpec;  
import java.util.HashMap;  
import java.util.Map;  
import javax.crypto.KeyAgreement;  
import javax.crypto.SecretKey;  
import javax.crypto.interfaces.DHPrivateKey;  
import javax.crypto.interfaces.DHPublicKey;  
import javax.crypto.spec.DHParameterSpec;  
  
public abstract class DHCoder {  
    //非对称加密算法        
    private static final String KEY_ALGORITHM = "DH";  
    //本地密钥算法，即对称加密算法   
    private static final String SELECT_ALGORITHM = "AES";  
    //密钥长度
    private static final int KEY_SIZE = 512;  
    //公钥  
    private static final String PUBLIC_KEY = "DHPublicKey";  
    //私钥  
    private static final String PRIVATE_KEY = "DHPrivateKey";  
      
    //初始化甲方密钥
    public static Map<String, Object> initKey() throws Exception{  
        //实例化密钥对生成器  
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
        //初始化密钥对生成器  
        keyPairGenerator.initialize(KEY_SIZE);  
        //生成密钥对  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
        //甲方公钥  
        DHPublicKey publicKey = (DHPublicKey)keyPair.getPublic();  
        //甲方私钥  
        DHPrivateKey privateKey = (DHPrivateKey)keyPair.getPrivate();  
        //将密钥对存储在Map中  
        Map<String, Object> keyMap = new HashMap<String, Object>(2);  
        keyMap.put(PUBLIC_KEY, publicKey);  
        keyMap.put(PRIVATE_KEY, privateKey);  
        return keyMap;  
    }  
    //初始化乙方密钥
    public static Map<String, Object> initKey(byte[] key) throws Exception{  
        //解析甲方公钥转换公钥材料  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);  
        //实例化密钥工厂  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        //产生公钥  
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);  
        //由甲方公钥构建乙方密钥  
        DHParameterSpec dhParameterSpec = ((DHPublicKey)pubKey).getParams();  
        //实例化密钥对生成器  
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
        //初始化密钥对生成器  
        keyPairGenerator.initialize(KEY_SIZE);  
        //产生密钥对  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
        //乙方公钥  
        DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();  
        //乙方私约  
        DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();  
        //将密钥对存储在Map中  
        Map<String, Object> keyMap = new HashMap<String, Object>(2);  
        keyMap.put(PUBLIC_KEY, publicKey);  
        keyMap.put(PRIVATE_KEY, privateKey);  
        return keyMap;  
    }    
    //构建密钥 
    public static byte[] getSecretKey(byte[] publicKey, byte[] privateKey) throws Exception{  
        //实例化密钥工厂  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        //初始化公钥  
        //密钥材料转换  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);  
        //产生公钥  
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);  
        //初始化私钥  
        //密钥材料转换  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);  
        //产生私钥  
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);  
        //实例化  
        KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());  
        //初始化  
        keyAgree.init(priKey);  
        keyAgree.doPhase(pubKey, true);  
        //生成本地密钥  
        SecretKey secretKey = keyAgree.generateSecret(SELECT_ALGORITHM);  
        return secretKey.getEncoded();  
    }  
    //取私钥 
    public static byte[] getPrivateKey(Map<String, Object> keyMap) throws Exception{  
        Key key = (Key) keyMap.get(PRIVATE_KEY);  
        return key.getEncoded();  
    }   
    //取公钥   
    public static byte[] getPublicKey(Map<String, Object> keyMap) throws Exception{  
        Key key = (Key) keyMap.get(PUBLIC_KEY);  
        return key.getEncoded();  
    }  
} 
