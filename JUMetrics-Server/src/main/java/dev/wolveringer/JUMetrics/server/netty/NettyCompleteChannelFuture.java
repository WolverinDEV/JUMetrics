/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package dev.wolveringer.JUMetrics.server.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;

/**
 * A skeletal {@link ChannelFuture} implementation which represents a
 * {@link ChannelFuture} which has been completed already.
 */
public abstract class NettyCompleteChannelFuture extends CompleteFuture<Void> implements ChannelFuture {

    private final Channel channel;

    /**
     * Creates a new instance.
     *
     * @param channel the {@link Channel} associated with this future
     */
    protected NettyCompleteChannelFuture(Channel channel, EventExecutor executor) {
        super(executor);
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        this.channel = channel;
    }

    @Override
    protected EventExecutor executor() {
        EventExecutor e = super.executor();
        if (e == null) {
            return channel().eventLoop();
        } else {
            return e;
        }
    }

    @Override
    public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        super.addListener(listener);
        return this;
    }

    @Override
    public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
        super.addListeners(listeners);
        return this;
    }

    @Override
    public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        super.removeListener(listener);
        return this;
    }

    @Override
    public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
        super.removeListeners(listeners);
        return this;
    }

    @Override
    public ChannelFuture syncUninterruptibly() {
        return this;
    }

    @Override
    public ChannelFuture sync() throws InterruptedException {
        return this;
    }

    @Override
    public ChannelFuture await() throws InterruptedException {
        return this;
    }

    @Override
    public ChannelFuture awaitUninterruptibly() {
        return this;
    }

    public Channel channel() {
        return channel;
    }

    public Void getNow() {
        return null;
    }

    public boolean isVoid() {
        return false;
    }
    
    public static class FailedChannelFuture extends NettyCompleteChannelFuture {
        private final Throwable cause;

        /**
         * Creates a new instance.
         *
         * @param channel the {@link Channel} associated with this future
         * @param cause   the cause of failure
         */
        public FailedChannelFuture(Channel channel, EventExecutor executor, Throwable cause) {
            super(channel, executor);
            if (cause == null) {
                throw new NullPointerException("cause");
            }
            this.cause = cause;
        }

        public Throwable cause() {
            return cause;
        }

        public boolean isSuccess() {
            return false;
        }

        @Override
        public ChannelFuture sync() {
            PlatformDependent.throwException(cause);
            return this;
        }

        @Override
        public ChannelFuture syncUninterruptibly() {
            PlatformDependent.throwException(cause);
            return this;
        }
    }
}
