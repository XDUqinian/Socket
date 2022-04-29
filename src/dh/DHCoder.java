
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
    //�ǶԳƼ����㷨        
    private static final String KEY_ALGORITHM = "DH";  
    //������Կ�㷨�����ԳƼ����㷨   
    private static final String SELECT_ALGORITHM = "AES";  
    //��Կ����
    private static final int KEY_SIZE = 512;  
    //��Կ  
    private static final String PUBLIC_KEY = "DHPublicKey";  
    //˽Կ  
    private static final String PRIVATE_KEY = "DHPrivateKey";  
      
    //��ʼ���׷���Կ
    public static Map<String, Object> initKey() throws Exception{  
        //ʵ������Կ��������  
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
        //��ʼ����Կ��������  
        keyPairGenerator.initialize(KEY_SIZE);  
        //������Կ��  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
        //�׷���Կ  
        DHPublicKey publicKey = (DHPublicKey)keyPair.getPublic();  
        //�׷�˽Կ  
        DHPrivateKey privateKey = (DHPrivateKey)keyPair.getPrivate();  
        //����Կ�Դ洢��Map��  
        Map<String, Object> keyMap = new HashMap<String, Object>(2);  
        keyMap.put(PUBLIC_KEY, publicKey);  
        keyMap.put(PRIVATE_KEY, privateKey);  
        return keyMap;  
    }  
    //��ʼ���ҷ���Կ
    public static Map<String, Object> initKey(byte[] key) throws Exception{  
        //�����׷���Կת����Կ����  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);  
        //ʵ������Կ����  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        //������Կ  
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);  
        //�ɼ׷���Կ�����ҷ���Կ  
        DHParameterSpec dhParameterSpec = ((DHPublicKey)pubKey).getParams();  
        //ʵ������Կ��������  
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
        //��ʼ����Կ��������  
        keyPairGenerator.initialize(KEY_SIZE);  
        //������Կ��  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
        //�ҷ���Կ  
        DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();  
        //�ҷ�˽Լ  
        DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();  
        //����Կ�Դ洢��Map��  
        Map<String, Object> keyMap = new HashMap<String, Object>(2);  
        keyMap.put(PUBLIC_KEY, publicKey);  
        keyMap.put(PRIVATE_KEY, privateKey);  
        return keyMap;  
    }    
    //������Կ 
    public static byte[] getSecretKey(byte[] publicKey, byte[] privateKey) throws Exception{  
        //ʵ������Կ����  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        //��ʼ����Կ  
        //��Կ����ת��  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);  
        //������Կ  
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);  
        //��ʼ��˽Կ  
        //��Կ����ת��  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);  
        //����˽Կ  
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);  
        //ʵ����  
        KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());  
        //��ʼ��  
        keyAgree.init(priKey);  
        keyAgree.doPhase(pubKey, true);  
        //���ɱ�����Կ  
        SecretKey secretKey = keyAgree.generateSecret(SELECT_ALGORITHM);  
        return secretKey.getEncoded();  
    }  
    //ȡ˽Կ 
    public static byte[] getPrivateKey(Map<String, Object> keyMap) throws Exception{  
        Key key = (Key) keyMap.get(PRIVATE_KEY);  
        return key.getEncoded();  
    }   
    //ȡ��Կ   
    public static byte[] getPublicKey(Map<String, Object> keyMap) throws Exception{  
        Key key = (Key) keyMap.get(PUBLIC_KEY);  
        return key.getEncoded();  
    }  
} 
