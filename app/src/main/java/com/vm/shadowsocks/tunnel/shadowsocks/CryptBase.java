/*
 * Copyright (c) 2015, Blake
 * All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote
 * products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.vm.shadowsocks.tunnel.shadowsocks;

import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

/**
 * Crypt base class implementation
 */
public abstract class CryptBase implements ICrypt {

    protected abstract StreamBlockCipher getCipher(boolean isEncrypted) throws InvalidAlgorithmParameterException;

    protected abstract SecretKey getKey();

    protected abstract void _encrypt(byte[] data, ByteArrayOutputStream stream);

    protected abstract void _decrypt(byte[] data, ByteArrayOutputStream stream);

    protected final String _name;
    protected final SecretKey _key;
    protected final ShadowSocksKey _ssKey;
    protected final int _ivLength;
    protected final int _keyLength;
    protected boolean _encryptIVSet;
    protected boolean _decryptIVSet;
    protected byte[] _encryptIV;
    protected byte[] _decryptIV;
    protected final Lock encLock = new ReentrantLock();
    protected final Lock decLock = new ReentrantLock();
    protected StreamBlockCipher encCipher;
    protected StreamBlockCipher decCipher;
    private Logger logger = Logger.getLogger(CryptBase.class.getName());

    public CryptBase(String name, String password) {
        _name = name.toLowerCase();
        _ivLength = getIVLength();
        _keyLength = getKeyLength();
        _ssKey = new ShadowSocksKey(password, _keyLength);
        _key = getKey();
    }

    protected void setIV(byte[] iv, boolean isEncrypt) {
        if (_ivLength == 0) {
            return;
        }

        if (isEncrypt) {
            _encryptIV = new byte[_ivLength];
            System.arraycopy(iv, 0, _encryptIV, 0, _ivLength);
            try {
                encCipher = getCipher(isEncrypt);
                ParametersWithIV parameterIV = new ParametersWithIV(new KeyParameter(_key.getEncoded()), _encryptIV);
                encCipher.init(isEncrypt, parameterIV);
            } catch (InvalidAlgorithmParameterException e) {
                logger.info(e.toString());
            }
        } else {
            _decryptIV = new byte[_ivLength];
            System.arraycopy(iv, 0, _decryptIV, 0, _ivLength);
            try {
                decCipher = getCipher(isEncrypt);
                ParametersWithIV parameterIV = new ParametersWithIV(new KeyParameter(_key.getEncoded()), _decryptIV);
                decCipher.init(isEncrypt, parameterIV);
            } catch (InvalidAlgorithmParameterException e) {
                logger.info(e.toString());
            }
        }
    }

    public byte[] encrypt(byte[] data) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        encrypt(data, stream);
        return stream.toByteArray();
    }

    public byte[] decrypt(byte[] data) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        decrypt(data, stream);
        return stream.toByteArray();
    }

    @Override
    public void encrypt(byte[] data, ByteArrayOutputStream stream) {
        synchronized (encLock) {
            stream.reset();
            if (!_encryptIVSet) {
                _encryptIVSet = true;
                byte[] iv = new byte[_ivLength];
                new SecureRandom().nextBytes(iv);
                setIV(iv, true);
                try {
                    stream.write(iv);
                } catch (IOException e) {
                    logger.info(e.toString());
                }

            }

            _encrypt(data, stream);
        }
    }

    @Override
    public void encrypt(byte[] data, int length, ByteArrayOutputStream stream) {
        byte[] d = new byte[length];
        System.arraycopy(data, 0, d, 0, length);
        encrypt(d, stream);
    }

    @Override
    public void decrypt(byte[] data, ByteArrayOutputStream stream) {
        byte[] temp;

        synchronized (decLock) {
            stream.reset();
            if (!_decryptIVSet) {
                _decryptIVSet = true;
                setIV(data, false);
                temp = new byte[data.length - _ivLength];
                System.arraycopy(data, _ivLength, temp, 0, data.length - _ivLength);
            } else {
                temp = data;
            }

            _decrypt(temp, stream);
        }
    }

    @Override
    public void decrypt(byte[] data, int length, ByteArrayOutputStream stream) {
        byte[] d = new byte[length];
        System.arraycopy(data, 0, d, 0, length);
        decrypt(d, stream);
    }
}
