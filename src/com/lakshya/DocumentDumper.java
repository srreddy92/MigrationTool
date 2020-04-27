package com.lakshya;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocumentDumper
{

	/**
	 * Print indentation for the given level (3 spaces per level).
	 */
	private void printIndentation(StringBuffer buffer, int level)
	{
		// Print indentation.
		for (int i = 0; i < level; i++)
		{
			buffer.append("    ");
		}
	}

	/**
	 * Print the document starting at the given node.
	 */
	private void print(Node node, StringBuffer buffer, int level)
	{
		// If not level 0, print blank line and indentation.
		if (level > 0)
		{
			buffer.append("\n");
			printIndentation(buffer, level);
		}

		// Print node name.
		buffer.append("<" + node.getNodeName());

		// If attributes, print them.
		if (node.hasAttributes())
		{
			NamedNodeMap attributes = node.getAttributes();

			if (attributes.getLength() > 0)
			{
				for (int i = 0; i < attributes.getLength(); i++)
				{
					Node attribute = attributes.item(i);
					if (!"field".equals(node.getNodeName()))
					{
						buffer.append("\n");
						printIndentation(buffer, level + 1);
					}
					else
					{
						buffer.append(" ");
					}
					buffer.append(attribute.getNodeName() + "=\"" + attribute.getNodeValue() + "\"");
				}
			}

			if (level > 0)
			{
				if (!"field".equals(node.getNodeName()))
				{
					buffer.append("\n");
					printIndentation(buffer, level);
				}
				else
				{
					buffer.append("/");
				}
			}
		}

		buffer.append(">");

		// Get node value.
		String value = node.getNodeValue();
		value = (value == null ? "" : value.trim());

		// If node value not empty, print it.
		if (value.length() > 0)
		{
			printIndentation(buffer, level);
			buffer.append(value);
		}

		// If node has children, print them.
		if (node.hasChildNodes())
		{
			NodeList children = node.getChildNodes();

			for (int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				print(child, buffer, level + 1);
			}
		}

		if (!"field".equals(node.getNodeName()))
		{
			buffer.append("\n");
			if (level > 0)
			{
				printIndentation(buffer, level);
			}
			buffer.append("</" + node.getNodeName() + ">");
		}
	}

	public void dump(Document doc, String folder,String filename) throws Exception
	{
		File f = new File(folder);
		f.mkdirs();
		PrintWriter pw = new PrintWriter(new FileWriter(folder + filename, false));
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		print(doc.getFirstChild(), buffer, 0);
		System.out.println("Dump: " + buffer);
		System.out.println("filename is:"+filename);
		pw.print(buffer.toString());
		pw.flush();
		pw.close();
	}

}