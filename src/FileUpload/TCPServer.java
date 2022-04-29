package FileUpload;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import dh.AESFileUtil;
import dh.DHCoder;

public class TCPServer {
	
	//�׷���Կ  
    private static byte[] publicKey1 ;  
    //�ҷ���Կ  
    private static byte[] publicKey2;  
    //�׷�˽Կ  
    private static byte[] privateKey1;  
    //�׷�������Կ  
    private static byte[] key1;  
	
    //���ɼ׷���Կ��
	private void initKey() throws Exception{    
        Map<String, Object> keyMap1 = DHCoder.initKey();  
        publicKey1 = DHCoder.getPublicKey(keyMap1);  
        privateKey1 = DHCoder.getPrivateKey(keyMap1);  
        System.out.println("�׷���Կ:\n" + Base64.encodeBase64String(publicKey1));  
        System.out.println("�׷�˽Կ:\n" + Base64.encodeBase64String(privateKey1));   
    }
	//���ɼ׷�������Կ
	private void initMyKey()
	{
		try {
			key1 = DHCoder.getSecretKey(publicKey2, privateKey1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("�׷�������Կ:\n" + Base64.encodeBase64String(key1));
	}
    public TCPServer() throws IOException
	{		
		try {
			initKey();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		init();
	}
	private void init() throws IOException
	{
		//ϵͳָ���Ķ˿ں�
		final int PORT = 8888;
		//�����ļ������·��
		final String receive_file_path = "ReceiveFile";
		ServerSocket server = new ServerSocket(PORT);
		while(true)
		{
			Socket socket = server.accept();
			System.out.println("���ÿͻ���Ϣ:\n" + "�ͻ���IP��" + socket.getInetAddress() 
			+ " �ͻ��˶˿ڣ�" + socket.getInetAddress().getLocalHost() + "�����ӷ�����");
			//����Կ�����ͻ���
	        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
	        dos.writeInt(publicKey1.length);
	        dos.write(publicKey1);	
	        //���տͻ��˵Ĺ�Կ
			DataInputStream dis=new DataInputStream(socket.getInputStream());
			publicKey2 = new byte[dis.readInt()];
			dis.readFully(publicKey2);
			System.out.println("�ҷ���Կ:\n" + Base64.encodeBase64String(publicKey2));
			initMyKey();
			//��ȡ�ļ���
			String receive_file_name = dis.readUTF();
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						//����������ļ���ŵ�·��
						InputStream is = socket.getInputStream();
						String decode_file_path=receive_file_path+File.separator+"decode"+receive_file_name;
						//��·�������ڣ������ļ���
						File file = new File(receive_file_path);
						if(!file.exists())
						{
							file.mkdir();
						}
						//���ʱ�������չ��
						SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
						String receive_file_path = file + File.separator + df.format(new Date()) + receive_file_name;
						System.out.println("receive_file_name:" + receive_file_name);
						System.out.println("receive_file_path:" + receive_file_path);
						//���մ������ļ�
						FileOutputStream fos = new FileOutputStream(decode_file_path);
						int len = 0;
						byte[] bytes = new byte[1024];
						while((len = is.read(bytes)) != -1)
						{
							fos.write(bytes,0,len);
						}
						//���߿ͻ����ϴ��ɹ�
						socket.getOutputStream().write("�ϴ��ɹ�".getBytes());
						//����
						AESFileUtil.decryptFile( decode_file_path,receive_file_path, Base64.encodeBase64String(key1));
						//��Դ�ͷ�
						fos.close();
						socket.close();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		TCPServer server = new TCPServer();
	}
}
