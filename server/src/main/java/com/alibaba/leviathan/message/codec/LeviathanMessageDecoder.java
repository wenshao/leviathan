package com.alibaba.leviathan.message.codec;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

import com.alibaba.leviathan.message.TLVConstants;
import com.alibaba.leviathan.server.LeviathanServer;

public class LeviathanMessageDecoder extends LengthFieldBasedFrameDecoder {

    private final static Log LOG               = LogFactory.getLog(LeviathanMessageDecoder.class);

    private final static int maxFrameLength    = 1024 * 1024;                      // 1m
    private final static int lengthFieldOffset = 2;
    private final static int lengthFieldLength = 4;

    private final AtomicLong receivedBytes     = new AtomicLong();

    public LeviathanMessageDecoder(){
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    public long getRecevedBytes() {
        return receivedBytes.get();
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

        receivedBytes.addAndGet(length + TLVConstants.TAG_PREFIX_LENGTH);

        if (tag == TLVConstants.STRING_UTF8) {
            String text = frame.toString(TLVConstants.TAG_PREFIX_LENGTH, length, TLVConstants.UTF8);
            return text;
        }

        throw new Exception("not support format");
    }
}
