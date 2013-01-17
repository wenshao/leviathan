package com.alibaba.study.message.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.alibaba.study.message.XMessage;

public class XEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object objMessage) throws Exception {
        XMessage msg = (XMessage) objMessage;
        
        byte[] bytes = msg.toBytes();
        ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer(bytes.length, ctx.getChannel().getConfig().getBufferFactory());
        channelBuffer.writeBytes(bytes);
        return channelBuffer;
    }


}
