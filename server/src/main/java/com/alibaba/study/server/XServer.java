package com.alibaba.study.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.alibaba.study.message.codec.XDecoder;
import com.alibaba.study.message.codec.XEncoder;

public class XServer implements XServerMBean {

    private static Log                    LOG               = LogFactory.getLog(XServer.class);

    private ServerBootstrap               bootstrap;
    private ThreadPoolExecutor            bossExecutor;
    private ThreadPoolExecutor            workerExecutor;

    private int                           workerThreadCount = Runtime.getRuntime().availableProcessors();

    private NioServerSocketChannelFactory channelFactory;

    private final AtomicLong              acceptedCount     = new AtomicLong();
    private final AtomicLong              closedCount       = new AtomicLong();
    private final AtomicLong              sessionCount      = new AtomicLong();
    private final AtomicLong              runningMax        = new AtomicLong();

    public void start() {
        bossExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                                              new SynchronousQueue<Runnable>());
        workerExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                                                new SynchronousQueue<Runnable>());

        channelFactory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor, workerThreadCount);
        bootstrap = new ServerBootstrap(channelFactory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new XEncoder(), //
                                         new XDecoder(), //
                                         new NettyServerHanlder() //
                );
            }

        });

        SocketAddress address = new InetSocketAddress("0.0.0.0", 7001);
        bootstrap.bind(address);
        if (LOG.isInfoEnabled()) {
            LOG.info("XServer listening " + address);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("XServer started.");
        }
    }

    public class NettyServerHanlder extends SimpleChannelUpstreamHandler {

        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
            acceptedCount.incrementAndGet();
            incrementSessionCount();

            if (LOG.isDebugEnabled()) {
                Channel channel = ctx.getChannel();
                LOG.debug("accepted " + channel.getRemoteAddress());
            }
            ctx.sendUpstream(e);
        }

        public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            closedCount.incrementAndGet();
            decrementSessionCount();

            ctx.sendUpstream(e);
        }

        public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            ctx.sendUpstream(e);
        }

        public void channelUnbound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            ctx.sendUpstream(e);
        }
        
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            ctx.sendUpstream(e);
        }
    }
    
    void decrementSessionCount() {
        this.sessionCount.decrementAndGet();
    }
    
    void incrementSessionCount() {
        long current = this.sessionCount.incrementAndGet();
        for (;;) {
            long max = this.runningMax.get();
            if (current > max) {
                boolean success = this.runningMax.compareAndSet(max, current);
                if (success) {
                    break;
                }
            } else {
                break;
            }
        }
    }
    
    public long getSessionCount() {
        return sessionCount.get();
    }

    public long getClosedCount() {
        return this.closedCount.get();
    }

    public long getAcceptedCount() {
        return this.acceptedCount.get();
    }

    public static void main(String args[]) throws Exception {
        XServer server = new XServer();
        server.start();
    }
}
