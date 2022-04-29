package FileUpload;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import dh.AESFileUtil;
import dh.DHCoder;

public class TCPClient {
	//�ҷ���Կ  
    private static byte[] publicKey2;  
    //�ҷ�˽Կ  
    private static byte[] privateKey2;  
    //�ҷ���Կ  
    private static byte[] key2; 
    //�׷���Կ  
    private static byte[] publicKey1;  
    
	public TCPClient(String IP,int PORT,String send_file_path,String send_file_name) throws IOException
	{
		init(IP,PORT,send_file_path,send_file_name);
	}
	//������Կ
	private void initMyKey()
	{  
        try {
        	Map<String, Object> keyMap2 = DHCoder.initKey(publicKey1);
			publicKey2 = DHCoder.getPublicKey(keyMap2);
			privateKey2 = DHCoder.getPrivateKey(keyMap2); 
			key2 = DHCoder.getSecretKey(publicKey1, privateKey2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        System.out.println("�ҷ���Կ:\n" + Base64.encodeBase64String(publicKey2));  
        System.out.println("�ҷ�˽Կ:\n" + Base64.encodeBase64String(privateKey2));
        System.out.println("�ҷ�������Կ:\n" + Base64.encodeBase64String(key2));
	}
	private void init(String IP,int PORT,String send_file_path,String send_file_name) throws IOException
	{		
		Socket socket = new Socket(IP,PORT);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		
		//���շ������˵Ĺ�Կ
		DataInputStream dis=new DataInputStream(socket.getInputStream());
		publicKey1 = new byte[dis.readInt()];
		dis.readFully(publicKey1);
		System.out.println("�׷���Կ:\n" + Base64.encodeBase64String(publicKey1));
		//������Կ
		initMyKey();
		try {
			key2 = DHCoder.getSecretKey(publicKey1, privateKey2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("�ҷ�������Կ:\n" + Base64.encodeBase64String(key2));
		//����Կ������������
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(publicKey2.length);
        dos.write(publicKey2);
		//�����ļ���
		dos.writeUTF(send_file_name);
		System.out.println("send_file_name:"+send_file_name);
		//�����ļ��Ĵ洢·��
		File file = new File(send_file_path);
		String encode_file_path=String.valueOf(file.getParent())+File.separator+"encode"+send_file_name;
		System.out.println(encode_file_path);
		//����
		AESFileUtil.encryptFile(send_file_path, encode_file_path, Base64.encodeBase64String(key2));
        //���ͼ����ļ�
        FileInputStream fis = new FileInputStream(encode_file_path);
		int len=0;
		byte[] bytes = new byte[1024];
		while((len = fis.read(bytes))!=-1)
		{
			os.write(bytes,0,len);
		}
		//���߷������ѷ������
		socket.shutdownOutput();
		//��ȡ���������ص�����
		while((len = is.read(bytes)) != -1)
		{
			System.out.println(new String(bytes,0,len));
		}
		//�ͷ���Դ
		fis.close();
		socket.close();
	}
	public static void main(String[] args) throws IOException
	{
		TCPClient client = new TCPClient("127.0.0.1",8888,"sendFile/ttt.png","ttt.png");
	}
}

