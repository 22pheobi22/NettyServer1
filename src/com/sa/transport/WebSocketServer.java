package com.sa.transport;

import java.io.IOException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
	public void bind(int port) throws IOException {
		// 避免使用默认线程数参数
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime()
				.availableProcessors());
		System.err.println("WebSocketServer服务端已启动，正在监听用户的请求......");
		try {
			ServerBootstrap b = new ServerBootstrap();

			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024*10)
					.childHandler(new ChildChannelHandler());

			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			ChannelPipeline pipeline = arg0.pipeline();

			pipeline.addLast("http-codec", new HttpServerCodec());
			pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
			pipeline.addLast("http-chunked", new ChunkedWriteHandler());
			pipeline.addLast("handler", new WebSocketServerHandler());
		}
	}

}
