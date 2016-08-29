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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Crypt factory
 */
public class CryptFactory {
    private static final Map<String, String> crypts = new HashMap<String, String>() {{
        putAll(AesCrypt.getCiphers());
        putAll(CamelliaCrypt.getCiphers());
        putAll(BlowFishCrypt.getCiphers());
        putAll(SeedCrypt.getCiphers());
        // TODO: other crypts
    }};
    private static Logger logger = Logger.getLogger(CryptFactory.class.getName());

    public static boolean isCipherExisted(String name) {
        return (crypts.get(name) != null);
    }

    public static ICrypt get(String name, String password) {
        try {
            Object obj = getObj(crypts.get(name), String.class, name, String.class, password);
            return (ICrypt) obj;

        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return null;
    }

    public static List<String> getSupportedCiphers() {
        List sortedKeys = new ArrayList<String>(crypts.keySet());
        Collections.sort(sortedKeys);
        return sortedKeys;
    }

    public static Object getObj(String className, Object... args) {
        Object retValue = null;
        try {
            Class c = Class.forName(className);
            if (args.length == 0) {
                retValue = c.newInstance();
            } else if ((args.length & 1) == 0) {
                // args should come with pairs, for example
                // String.class, "arg1_value", String.class, "arg2_value"
                Class[] oParam = new Class[args.length / 2];
                for (int arg_i = 0, i = 0; arg_i < args.length; arg_i += 2, i++) {
                    oParam[i] = (Class) args[arg_i];
                }

                Constructor constructor = c.getConstructor(oParam);
                Object[] paramObjs = new Object[args.length / 2];
                for (int arg_i = 1, i = 0; arg_i < args.length; arg_i += 2, i++) {
                    paramObjs[i] = args[arg_i];
                }
                retValue = constructor.newInstance(paramObjs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retValue;
    }
}
