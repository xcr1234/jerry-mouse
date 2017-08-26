package com.jerry.mouse.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * StreamReuse 输入流复用
 */
public class StreamReuse extends InputStream {

    private InputStream in;

    public StreamReuse(InputStream in){
        if(in.markSupported()){
            this.in = in;
        }else{
            this.in = new BufferedInputStream(in);
        }
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return in.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}
