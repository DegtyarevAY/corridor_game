package conn;

/**
* conn/ConnHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Conn.idl
* 4 ������� 2018 �. 10:28:14 MSK
*/

public final class ConnHolder implements org.omg.CORBA.portable.Streamable
{
  public conn.Conn value = null;

  public ConnHolder ()
  {
  }

  public ConnHolder (conn.Conn initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = conn.ConnHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    conn.ConnHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return conn.ConnHelper.type ();
  }

}