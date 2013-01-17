package com.alibaba.study.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.study.message.TLVConstants;
import com.alibaba.study.message.codec.Bits;

public class XClient {

    private final static Log  LOG = LogFactory.getLog(XClient.class);

    private Socket            socket;
    private final InetAddress address;
    private final int         port;

    public XClient(String address, int port) throws IOException{
        this.address = InetAddress.getByName(address);
        this.port = port;
    }

    public void connect() throws IOException {
        if (LOG.isInfoEnabled()) {
            LOG.debug("connect to" + address + ":" + port + " ...");
        }
        socket = new Socket(address, port);
        if (LOG.isInfoEnabled()) {
            LOG.debug("connected to" + address + ":" + port);
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void write(String text) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] bytes = text.getBytes("UTF-8");
        byte[] prefix = new byte[6];

        int len = bytes.length;
        Bits.putShort(prefix, 0, TLVConstants.STRING_UTF8);
        Bits.putInt(prefix, 2, len);

        out.write(prefix);
        out.write(bytes);
    }

    public String readString() throws IOException {
        InputStream in = socket.getInputStream();
        byte[] prefix = new byte[6];
        readFully(prefix);

        int tag = Bits.getShort(prefix, 0);
        int len = Bits.getInt(prefix, 2);
        byte[] bytes = new byte[len];
        readFully(bytes);

        if (tag != TLVConstants.STRING_UTF8) {
            throw new IOException("not support tag : " + tag);
        }

        return new String(bytes, "UTF-8");
    }

    public final void readFully(byte b[]) throws IOException {
        readFully(b, 0, b.length);
    }

    public final void readFully(byte b[], int off, int len) throws IOException {
        InputStream in = socket.getInputStream();

        if (len < 0) throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0) throw new EOFException();
            n += count;
        }
    }

    public static void main(String[] args) throws Exception {
        XClient client = new XClient("127.0.0.1", 7001);
        client.connect();
        client.write("hello world");

        String resp = client.readString();
        System.out.println(resp);
        client.close();
    }
}
