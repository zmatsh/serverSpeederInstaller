import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JOptionPane;


public class LicGenerator {
	final static int[] OFFSET = {0xd2,0x3f,0x1f,0xfa,0x69,0x3e,0x5d,0xd4,0xc3,0x18,0xa8,0x8a,0xa5,0x3f,0x1f,0x18};
	
	public static void main(String[] args) throws IOException{
		try{
		String MAC = JOptionPane.showInputDialog(null,"在此输入你的mac地址","锐速破解",JOptionPane.DEFAULT_OPTION);
		String Serial = toSerial(MAC);
		byte[] SerialByte = toSerialByte(Serial);
		byte[] OldLic = new byte[0x98];
		
		// Read old lic file into memory
		File InputLic = new File("resource1.dat");
		FileInputStream fis = new FileInputStream(InputLic);
		fis.read(OldLic);
		
		// Write date to the lic
		File OutputLic = new File("serverSpeeder_2034-12-31/apxfiles/etc/apx-20341231.lic");
		if(OutputLic.exists())
			OutputLic.delete();
		else
			OutputLic.createNewFile();
		FileOutputStream fos = new FileOutputStream(OutputLic);
		fos.write(OldLic, 0, 0x40);
		fos.write(SerialByte);
		fos.write(OldLic, 0x50, 0x48);
		fos.close();
		
		// Create new config file
		File InputConf = new File("resource2.dat");
		File OutputConf = new File("serverSpeeder_2034-12-31/apxfiles/etc/config");
		if(OutputConf.exists())
			OutputConf.delete();
		else
			OutputConf.createNewFile();
		PrintWriter ConfWritter = new PrintWriter(OutputConf);
		Scanner ConfReader = new Scanner(InputConf);
		while(ConfReader.hasNextLine()){
			String Content = ConfReader.nextLine();
			if(Content.startsWith("serial"))
				ConfWritter.print("serial=\""+Serial.toUpperCase()+"\"\n");
			else
				ConfWritter.print(Content + "\n");
		}
		ConfWritter.close();
		
		StringSelection stsel = new StringSelection(Serial.toUpperCase());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
		JOptionPane.showMessageDialog(null, "写入成功，你的序列号已经复制到剪切板：\n" + Serial.toUpperCase());
		System.exit(0);
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(null, "文件未找到，请不要更改文件夹内文件名");
		}
		finally{
			System.exit(0);
		}
	}
	
	// Calculate the lic bytes of serial number
	public static byte[] toSerialByte(String Serial){
		Serial = Serial.toLowerCase();
		int[] temp = new int[16];
		byte[] result = new byte[16];
		for(int i =0;i<=15;i++){
			temp[i] = valueOf(Serial.charAt(i)) + OFFSET[i];
			if(valueOf(Serial.charAt(i))>9)
				temp[i] +=7;
			result[i] = (byte)(0xff & temp[i]);
		}
		return result;
	}
	
	// Calculate the serial of a certain MAC address
	public static String toSerial(String MAC){
		int[] temp = new int[16];
		String result = "";
		
		MAC = MAC.toLowerCase();
		MAC = MAC.replace(":", "");
		MAC = MAC.replace("：", "");
		MAC = MAC.replace(" ", "");
		for(int i = 0; i <= 5;i++)
			temp[i] = Integer.parseInt(MAC.substring(i*2, i*2+2), 16);
		for(int i = 0; i <= 15;i++)
			temp[i] = temp[i%6] + i;
		for(int i = 0; i <= 7;i++)
			result += format(Integer.toHexString((temp[i] + temp[i+8]) % 256));
		return result;
	}
	
	public static int valueOf(char temp){
		String a = "0123456789abcdef";
		return a.indexOf(temp);
	}
	
	public static String format(String origin){
		while(origin.length()<2)
			origin = "0" + origin;
		return origin;
	}
}
