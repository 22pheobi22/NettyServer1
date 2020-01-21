/**
 * 
 * 项目名称:[NettyServer]
 * 包:	 [com.sa.transport]
 * 类名称: [HttpServer]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [Y.P]
 * 创建时间:[2017年8月2日 上午11:50:52]
 * 修改人: [Y.P]
 * 修改时间:[2017年8月2日 上午11:50:52]
 * 修改备注:[说明本次修改内容]  
 * 版本:	 [v1.0]   
 * 
 */
package com.sa.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServer implements Runnable {
	private int port = 8090;

	public HttpServer() {
	}

	public HttpServer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			this.bind();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void bind() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							// server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
							ch.pipeline().addLast("decoder", new HttpRequestDecoder());
							// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
							ch.pipeline().addLast("encoder", new HttpResponseEncoder());
							ch.pipeline().addLast("handler", new HttpServerHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(port).sync();

			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
