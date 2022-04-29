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
	
	//甲方公钥  
    private static byte[] publicKey1 ;  
    //乙方公钥  
    private static byte[] publicKey2;  
    //甲方私钥  
    private static byte[] privateKey1;  
    //甲方本地密钥  
    private static byte[] key1;  
	
    //生成甲方密钥对
	private void initKey() throws Exception{    
        Map<String, Object> keyMap1 = DHCoder.initKey();  
        publicKey1 = DHCoder.getPublicKey(keyMap1);  
        privateKey1 = DHCoder.getPrivateKey(keyMap1);  
        System.out.println("甲方公钥:\n" + Base64.encodeBase64String(publicKey1));  
        System.out.println("甲方私钥:\n" + Base64.encodeBase64String(privateKey1));   
    }
	//生成甲方本地密钥
	private void initMyKey()
	{
		try {
			key1 = DHCoder.getSecretKey(publicKey2, privateKey1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("甲方本地密钥:\n" + Base64.encodeBase64String(key1));
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
		//系统指定的端口号
		final int PORT = 8888;
		//接收文件的相对路径
		final String receive_file_path = "ReceiveFile";
		ServerSocket server = new ServerSocket(PORT);
		while(true)
		{
			Socket socket = server.accept();
			System.out.println("来访客户信息:\n" + "客户端IP：" + socket.getInetAddress() 
			+ " 客户端端口：" + socket.getInetAddress().getLocalHost() + "已连接服务器");
			//将公钥发给客户端
	        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
	        dos.writeInt(publicKey1.length);
	        dos.write(publicKey1);	
	        //接收客户端的公钥
			DataInputStream dis=new DataInputStream(socket.getInputStream());
			publicKey2 = new byte[dis.readInt()];
			dis.readFully(publicKey2);
			System.out.println("乙方公钥:\n" + Base64.encodeBase64String(publicKey2));
			initMyKey();
			//读取文件名
			String receive_file_name = dis.readUTF();
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						//构造待解密文件存放的路径
						InputStream is = socket.getInputStream();
						String decode_file_path=receive_file_path+File.separator+"decode"+receive_file_name;
						//若路径不存在，创建文件夹
						File file = new File(receive_file_path);
						if(!file.exists())
						{
							file.mkdir();
						}
						//添加时间戳和扩展名
						SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
						String receive_file_path = file + File.separator + df.format(new Date()) + receive_file_name;
						System.out.println("receive_file_name:" + receive_file_name);
						System.out.println("receive_file_path:" + receive_file_path);
						//接收待解密文件
						FileOutputStream fos = new FileOutputStream(decode_file_path);
						int len = 0;
						byte[] bytes = new byte[1024];
						while((len = is.read(bytes)) != -1)
						{
							fos.write(bytes,0,len);
						}
						//告诉客户端上传成功
						socket.getOutputStream().write("上传成功".getBytes());
						//解密
						AESFileUtil.decryptFile( decode_file_path,receive_file_path, Base64.encodeBase64String(key1));
						//资源释放
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
