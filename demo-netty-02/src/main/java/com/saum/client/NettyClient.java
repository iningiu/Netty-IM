package com.saum.client;

import com.saum.client.handler.LoginResponseHandler;
import com.saum.client.handler.MessageResponseHandler;
import com.saum.codec.PacketCodeC;
import com.saum.codec.PacketDecoder;
import com.saum.codec.PacketEncoder;
import com.saum.codec.ProcotolFrameDecoder;
import com.saum.protocol.request.MessageRequestPacket;
import com.saum.server.handler.LoginRequestHandler;
import com.saum.server.handler.MessageRequestHandler;
import com.saum.util.LoginUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * @Author saum
 * @Description:
 */
@Slf4j
public class NettyClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(LOGGING_HANDLER);
                            ch.pipeline().addLast(new ProcotolFrameDecoder());
                            ch.pipeline().addLast(new PacketDecoder());
                            ch.pipeline().addLast(new PacketEncoder());
                            ch.pipeline().addLast(new LoginResponseHandler());
                            ch.pipeline().addLast(new MessageResponseHandler());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("localhost", 8000).sync();
            startConsoleThread(channelFuture.channel());
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error");
        } finally {
            group.shutdownGracefully();
        }
    }

    private static void startConsoleThread(Channel channel){
        new Thread(()->{
            while(!Thread.interrupted()){
                if(LoginUtil.hasLogin(channel)){
                    System.out.print("请输入：");
                    Scanner sc = new Scanner(System.in);
                    String line = sc.nextLine();

                    MessageRequestPacket messageRequestPacket = new MessageRequestPacket();
                    messageRequestPacket.setMessage(line);
                    ByteBuf byteBuf = PacketCodeC.INSTANCE.encode(channel.alloc().ioBuffer(), messageRequestPacket);
                    channel.writeAndFlush(byteBuf);
                }
            }
        }).start();
    }
}