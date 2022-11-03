package com.example.eximporter.importer.service.js;

public enum JSParameter
{
	MESSAGE("message"), FILE_PATH("filePath"), MSG_TEMPLATE("msgTemplate"),FILE_NAME("fileName");
	private String identifier;

	JSParameter(String identifier)
	{
		this.identifier = identifier;
	}

	@Override
	public String toString()
	{
		return identifier;
	}

	public static JSParameter getValue(String identifier)
	{
		for (JSParameter jsParameter : JSParameter.values())
		{
			if (jsParameter.identifier.equals(identifier))
			{
				return jsParameter;
			}
		}
		throw new IllegalArgumentException("There is no enumerated value with the given identifier: " + identifier);
	}
}
