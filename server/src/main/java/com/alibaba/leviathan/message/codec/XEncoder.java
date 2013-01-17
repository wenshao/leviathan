package com.alibaba.leviathan.message.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.alibaba.leviathan.message.TLVConstants;
import com.alibaba.leviathan.message.XMessage;

public class XEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object object) throws Exception {
        if (object instanceof String) {
            String text = (String) object;
            byte[] bytes = text.getBytes(TLVConstants.UTF8);
            return writeBytes(ctx, TLVConstants.STRING_UTF8, bytes);
        }

        XMessage msg = (XMessage) object;
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
        return channelBuffer;
    }

}
