/* ***********************
** SiteListModel.java
** ***********************
** ���ڹ���վ��������б�ģ��
** Build 0715
** 07-15 WriteObject ��дΪ XML , �൱����д���ļ�
** **********************/

package ArkFTP.bin.model;

import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;


import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;


public class SiteListModel extends AbstractListModel
{
	
	private Vector<String[]> sites_vector;
	
	// վ�������XML�ļ��洢��
	final private String SiteManagerXML = "ArkFTP/res/SiteManager.xml";
	// ��д������ʹ ArkFTP �����վ���ļ�֧�� Filezilla �� XML ����վ�㹤��
	private String XMLRootElement = "ArkFTP";
	
	/* XML ��DTD����
			** Ŀǰ���õ�Ԫ��
			** [12] Name վ������
			** [0] Host վ���ַ
			** [1] Port �˿�
			** [5] User �û���
			** [6] Pass ����
			*/
	final private int[] UsedItem = {12, 0, 1, 5, 6};
	final private String[] ItemNameStr = {"Host", "Port", "Protocol", "Type",
					"Logontype", "User", "Pass", "TimezoneOffset", "PasvMode",
					"MaximumMultipleConnections", "EncodingType", 
					"BypassProxy", "Name", "Comments", "LocalDir", "RemoteDir"};
	
	private DocumentBuilder builder;

	public SiteListModel()
	{		
		File manager_file = new File(SiteManagerXML);
		Document doc = null;
		if (!manager_file.exists())
		{
			try
			{
				manager_file.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		sites_vector = new Vector<String[]>();
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setIgnoringElementContentWhitespace(true);
			builder = factory.newDocumentBuilder();
			doc = builder.parse(manager_file);
		}
		catch (Exception e)
		{
			System.err.println(e);
			System.exit(0);
		}
		// ��ʼ��ȡXML����

		Element root = doc.getDocumentElement();
		Node Servers = root.getChildNodes().item(0);
		NodeList ServerList = Servers.getChildNodes();
		
		for (int i = 0; i < ServerList.getLength(); i++)
		{
			Element ServerItem = (Element) ServerList.item(i);
			NodeList ServerItemData = ServerItem.getChildNodes();

			/* ��������Ҫ������ */
			
			String[] ServerVector = new String[5];
			for (int it = 0; it < UsedItem.length; it ++)
			{
				String value;
				Element ServerElementData = (Element) ServerItemData.item(UsedItem[it]);
				Text ServerElementDataText = (Text) ServerElementData.getFirstChild();
				if (ServerElementDataText == null)
					value = "";
				else
					value = ServerElementDataText.getData().trim();
				ServerVector[it] = value;	
			}
			sites_vector.add(ServerVector);
		}
		
	}
	
	public int getSize()
	{
		return sites_vector.size();
	}
	
	public void addSite(String siteName)
	{
		int index;
		for (index = 0; index < sites_vector.size(); index++)
		{
			String[] s = sites_vector.get(index);
			if (s[0].equals(siteName))
			{
				return;
			}
		}
		String[] site = new String[5];
		site[0] = siteName;
		sites_vector.add(site);
		this.fireIntervalAdded(this, index, index);
	}
	
	public boolean removeSite(String siteName)
	{
		int index;
		for (index = 0; index < sites_vector.size(); index++)
		{
			String[] s = sites_vector.get(index);
			if (s[0].equals(siteName))
			{
				sites_vector.removeElementAt(index);
				this.fireIntervalAdded(this, index, index);
				return true;
			}
		}
		return false;
	}

	public Object getElementAt(int index)
	{
		String[] str_array = sites_vector.get(index);
		return (Object)str_array[0];
	}
	
	public void save() {
		/* ��дΪ XML �����ʽ	*/
		
		Document savedoc = builder.newDocument();
		Element rootElement = savedoc.createElement(XMLRootElement);
		savedoc.appendChild(rootElement);
		Element ServersElement = savedoc.createElement("Servers");
		rootElement.appendChild(ServersElement);
		
		for (int i = 0; i < this.sites_vector.size(); i++)
		{
			String[] s = this.sites_vector.get(i);
			Element ServerElement = savedoc.createElement("Server");
			for (int j = 0; j < ItemNameStr.length; j++)
			{
				Element TempElement = savedoc.createElement(ItemNameStr[j]);
				Text TempElementText;
				if (j == UsedItem[0]) TempElementText = savedoc.createTextNode(s[0]);
				else if (j == UsedItem[1]) TempElementText = savedoc.createTextNode(s[1]);
				else if (j == UsedItem[2]) TempElementText = savedoc.createTextNode(s[2]);
				else if (j == UsedItem[3]) TempElementText = savedoc.createTextNode(s[3]);
				else if (j == UsedItem[4]) TempElementText = savedoc.createTextNode(s[4]);
				else TempElementText = savedoc.createTextNode("");
				ServerElement.appendChild(TempElement);
				TempElement.appendChild(TempElementText);
			}
			ServersElement.appendChild(ServerElement);
		}
		try
		{
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "SiteManager.dtd");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(savedoc), new StreamResult(new FileOutputStream(new File(SiteManagerXML))));
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
	
	public String[] getAll(int index)
	{
		return sites_vector.get(index);
	}
	
	public int findElemnt(String siteName_str)
	{
		for(int i = 0; i < this.sites_vector.size(); i++)
		{
			String[] s = this.sites_vector.get(i);
			if (s[0].equals(siteName_str))
				return i;
		}
		return -1;
	}
}
