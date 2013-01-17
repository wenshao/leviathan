package com.alibaba.study.message.codec;

import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

import com.alibaba.study.message.TLVConstants;

public class XDecoder extends LengthFieldBasedFrameDecoder {

    private final static Log     LOG               = LogFactory.getLog(XDecoder.class);

    private final static int     maxFrameLength    = 1024 * 1024;                      // 1m
    private final static int     lengthFieldOffset = 2;
    private final static int     lengthFieldLength = 4;

    private final static Charset UTF8              = Charset.forName("UTF-8");

    public XDecoder(){
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        ChannelBuffer frame = null;
        try {
            frame = (ChannelBuffer) super.decode(ctx, channel, buffer);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            channel.close();
            return null;
        }

        if (frame == null) {
            return null;
        }

        short tag = frame.readShort();
        int length = frame.readInt();

        String text = frame.toString(TLVConstants.TAG_PREFIX_LENGTH, length, UTF8);
        return text;
    }
}
