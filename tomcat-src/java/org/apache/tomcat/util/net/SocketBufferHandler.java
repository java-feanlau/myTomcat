/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomcat.util.net;

import java.nio.ByteBuffer;

import org.apache.tomcat.util.buf.ByteBufferUtils;

public class SocketBufferHandler {

    private volatile boolean readBufferConfiguredForWrite = true;
    private volatile ByteBuffer readBuffer;

    private volatile boolean writeBufferConfiguredForWrite = true;
    private volatile ByteBuffer writeBuffer;

    private final boolean direct;
    // 创建socketBufferHandler
    public SocketBufferHandler(int readBufferSize, int writeBufferSize,
            boolean direct) {
        // 默认不适用 directBuffer
        this.direct = direct;
        if (direct) {
            // 如果设置了使用directBuffer呢
            // 设置 directBuffer 缓冲区
            readBuffer = ByteBuffer.allocateDirect(readBufferSize);
            writeBuffer = ByteBuffer.allocateDirect(writeBufferSize);
        } else {
            // 分配 读写 缓冲区
            readBuffer = ByteBuffer.allocate(readBufferSize);
            writeBuffer = ByteBuffer.allocate(writeBufferSize);
        }
    }

    // 配置readBuffer
    public void configureReadBufferForWrite() {
        setReadBufferConfiguredForWrite(true);
    }


    public void configureReadBufferForRead() {
        setReadBufferConfiguredForWrite(false);
    }

    // 配置 readBuffer到 写模式
    private void setReadBufferConfiguredForWrite(boolean readBufferConFiguredForWrite) {
        // NO-OP if buffer is already in correct state
        if (this.readBufferConfiguredForWrite != readBufferConFiguredForWrite) {
            if (readBufferConFiguredForWrite) {
                // Switching to write
                int remaining = readBuffer.remaining();
                // 如果没有数据,则直接 clear
                if (remaining == 0) {
                    readBuffer.clear();
                } else {
                    // readBuffer中有数据,则compact 数据
                    readBuffer.compact();
                }
            } else {
                // Switching to read
                // 转换到 读模式
                readBuffer.flip();
            }
            this.readBufferConfiguredForWrite = readBufferConFiguredForWrite;
        }
    }


    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }


    public boolean isReadBufferEmpty() {
        if (readBufferConfiguredForWrite) {
            return readBuffer.position() == 0;
        } else {
            return readBuffer.remaining() == 0;
        }
    }


    public void configureWriteBufferForWrite() {
        setWriteBufferConfiguredForWrite(true);
    }


    public void configureWriteBufferForRead() {
        setWriteBufferConfiguredForWrite(false);
    }


    private void setWriteBufferConfiguredForWrite(boolean writeBufferConfiguredForWrite) {
        // NO-OP if buffer is already in correct state
        if (this.writeBufferConfiguredForWrite != writeBufferConfiguredForWrite) {
            if (writeBufferConfiguredForWrite) {
                // Switching to write
                int remaining = writeBuffer.remaining();
                if (remaining == 0) {
                    writeBuffer.clear();
                } else {
                    writeBuffer.compact();
                    writeBuffer.position(remaining);
                    writeBuffer.limit(writeBuffer.capacity());
                }
            } else {
                // Switching to read
                writeBuffer.flip();
            }
            this.writeBufferConfiguredForWrite = writeBufferConfiguredForWrite;
        }
    }


    public boolean isWriteBufferWritable() {
        if (writeBufferConfiguredForWrite) {
            return writeBuffer.hasRemaining();
        } else {
            return writeBuffer.remaining() == 0;
        }
    }


    public ByteBuffer getWriteBuffer() {
        return writeBuffer;
    }

        // writeBuffer 是否为空
    public boolean isWriteBufferEmpty() {
        if (writeBufferConfiguredForWrite) {
            return writeBuffer.position() == 0;
        } else {
            return writeBuffer.remaining() == 0;
        }
    }

    // 读写缓冲区复位 以及 标志位的设置
    public void reset() {
        readBuffer.clear();
        readBufferConfiguredForWrite = true;
        writeBuffer.clear();
        writeBufferConfiguredForWrite = true;
    }


    public void expand(int newSize) {
        configureReadBufferForWrite();
        readBuffer = ByteBufferUtils.expand(readBuffer, newSize);
        configureWriteBufferForWrite();
        writeBuffer = ByteBufferUtils.expand(writeBuffer, newSize);
    }

    public void free() {
        if (direct) {
            ByteBufferUtils.cleanDirectBuffer(readBuffer);
            ByteBufferUtils.cleanDirectBuffer(writeBuffer);
        }
    }

}
