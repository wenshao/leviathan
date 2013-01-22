package com.alibaba.leviathan.message.codec;

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.alibaba.leviathan.message.TLVConstants;
import com.alibaba.leviathan.message.TLVMessage;

public class LeviathanEncoder extends OneToOneEncoder {

    private final AtomicLong sentBytes        = new AtomicLong();
    private final AtomicLong sentMessageCount = new AtomicLong();

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object object) throws Exception {
        if (object instanceof String) {
            String text = (String) object;
            byte[] bytes = text.getBytes(TLVConstants.UTF8);
            return writeBytes(ctx, TLVConstants.STRING_UTF8, bytes);
        }

        TLVMessage msg = (TLVMessage) object;
        return writeBytes(ctx, msg.getTag(), msg.toBytes());
    }

    private Object writeBytes(ChannelHandlerContext ctx, short tag, byte[] bytes) {
        int buffSize = bytes.length + TLVConstants.TAG_PREFIX_LENGTH;
        ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer(buffSize, //
                                                                   ctx.getChannel().getConfig().getBufferFactory()//
        );

        channelBuffer.writeShort(tag);
        channelBuffer.writeInt(bytes.length);
        channelBuffer.writeBytes(bytes);

        sentBytes.addAndGet(buffSize);
        sentMessageCount.incrementAndGet();

        return channelBuffer;
    }
    
    public long getSentMessageCount() {
        return sentMessageCount.get();
    }

    public long getSentBytes() {
        return sentBytes.get();
    }

    public void resetStat() {
        sentBytes.set(0);
        sentMessageCount.set(0);
    }
}
